package com.example.purrpalsapp.ui.cat.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.purrpalsapp.R;
import com.example.purrpalsapp.mainPage;
import com.example.purrpalsapp.ui.payment.PaymentFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class CatProfileAdapter extends RecyclerView.Adapter<CatProfileAdapter.ViewHolder> {

    private List<CatProfile> catProfiles;
    private Context context;
    private DatabaseReference databaseReference, databaseReferenceAll;
    private int uses, amount;

    public CatProfileAdapter(List<CatProfile> catProfiles, Context context, int uses) {
        this.catProfiles = catProfiles;
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cats");
        this.databaseReferenceAll = FirebaseDatabase.getInstance().getReference("cats");
        this.uses = uses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_cat_rent, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CatProfile catProfile = catProfiles.get(position);
        holder.catNameTextView.setText("Name: " + catProfile.name);
        holder.catAgeTextView.setText("Age: " + catProfile.age);
        holder.catBreedTextView.setText("Breed: " + catProfile.breed);
        holder.catDescriptionTextView.setText("Description: " + catProfile.description);
        holder.ratingBar.setRating(catProfile.getRating());
        List<String> status = List.of("Available", "Unavailable");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, status);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatusCatPost.setAdapter(adapter);

        if (catProfile.price > 0) {
            if (catProfile.status.equals("Available")) {
                holder.spinnerStatusCatPost.setSelection(0);
            } else {
                holder.spinnerStatusCatPost.setSelection(1);
            }
            holder.ratePerHour.setText(String.valueOf(catProfile.price));
            holder.editTextFrom.setText(catProfile.availableFrom);
            holder.editTextTo.setText(catProfile.availableTo);
        }
        if (uses == 3) {
            holder.delete.setText("$" + catProfile.getPrice() + " Rent Now");
            holder.priceText.setText("Status: " + catProfile.getStatus());
        }
        // Load image using a library like Picasso or Glide We're using Glide
        Glide.with(context).load(catProfile.profileImage).into(holder.catImage);

    }

    public void updateData(List<CatProfile> newCatProfiles) {
        this.catProfiles = newCatProfiles;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return catProfiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView catNameTextView;
        TextView catAgeTextView;
        TextView catBreedTextView;
        TextView catDescriptionTextView, priceText;
        ImageView catImage;
        Button delete;
        LinearLayout postForRent, priceLayout;
        Spinner spinnerStatusCatPost;
        EditText ratePerHour, editTextFrom, editTextTo;
        AppCompatRatingBar ratingBar;


        @SuppressLint("SetTextI18n")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catNameTextView = itemView.findViewById(R.id.cat_name);
            catAgeTextView = itemView.findViewById(R.id.cat_age);
            catBreedTextView = itemView.findViewById(R.id.cat_breed);
            catDescriptionTextView = itemView.findViewById(R.id.cat_description);
            catImage = itemView.findViewById(R.id.cat_image);
            postForRent = itemView.findViewById(R.id.for_rent_layout);
            spinnerStatusCatPost = itemView.findViewById(R.id.spinner_status_cat_post);
            ratePerHour = itemView.findViewById(R.id.rate_per_hour);
            editTextFrom = itemView.findViewById(R.id.pickUpDateEditText);
            editTextTo = itemView.findViewById(R.id.dropOffDateEditText);
            ratingBar = itemView.findViewById(R.id.rating_bar_profile_data);
            priceText = itemView.findViewById(R.id.price_textview_search);
            priceLayout = itemView.findViewById(R.id.price_sech_layout);
            delete = itemView.findViewById(R.id.delete_button);
            if (delete != null) {
                if (uses == 1) {
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle item click
                            int position = getAdapterPosition();
                            CatProfile catProfile = catProfiles.get(position);
                            // Show details of cat profile and allow deleting
                            showConfirmToDeleteDialog(catProfile);
                        }
                    });
                } else if (uses == 2) {
                    delete.setText("Update");
                    postForRent.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(v -> {
                        // Handle item click
                        int position = getAdapterPosition();
                        CatProfile catProfile = catProfiles.get(position);
                        // Show details of cat profile and allow deleting
                        updateCatProfileForPost(delete, catProfile, ratePerHour.getText().toString(), editTextFrom.getText().toString(), editTextTo.getText().toString(), spinnerStatusCatPost.getSelectedItem().toString());
                    });
                    editTextFrom.setFocusable(false);
                    editTextFrom.setClickable(true);
                    editTextFrom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(editTextFrom);
                        }
                    });

                    editTextTo.setFocusable(false);
                    editTextTo.setClickable(true);
                    editTextTo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDatePickerDialog(editTextTo);
                        }
                    });
                } else if (uses == 3) {
                    delete.setText("Rent Now");
                    priceLayout.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(v -> {
                        // Handle item click
                        int position = getAdapterPosition();
                        CatProfile catProfile = catProfiles.get(position);
                        // Show details of cat profile and allow deleting
                        if (catProfile.getStatus().matches("Available")) {
                            showPaymentDialog(catProfile, FirebaseAuth.getInstance().getCurrentUser());
                        } else {
                            Toast.makeText(context, "Currently this cat is not available for rent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    private void showPaymentDialog(CatProfile catProfile, FirebaseUser currentUser) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.payment_dialog_layout);

        EditText hoursEditText = dialog.findViewById(R.id.hours_payment);
        EditText dateEditText = dialog.findViewById(R.id.date_payment);
        Button continueButton = dialog.findViewById(R.id.continue_pay);
        dateEditText.setFocusable(false);
        dateEditText.setClickable(true);
        dateEditText.setOnClickListener(v -> {
            showDatePickerDialog(dateEditText);
        });
        hoursEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int hours = 0;
                try {
                    hours = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    // Handle invalid input
                }

                if (hours > 168) {
                    hoursEditText.setError("Maximum 168 hours or 7 days");
                    continueButton.setEnabled(false);
                } else {
                    amount = hours * catProfile.getPrice();
                    continueButton.setText("Make Payment ($" + amount + ")");
                    continueButton.setEnabled(true);
                }
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle payment logic here
                if (dateEditText.getText().toString().isEmpty() || hoursEditText.getText().toString().isEmpty() || amount <= 0) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("payAmount", String.valueOf(amount));
                bundle.putString("payDate", dateEditText.getText().toString());
                bundle.putString("payHours", hoursEditText.getText().toString());
                bundle.putString("payId", catProfile.getId());
                bundle.putString("payName", catProfile.getName());
                bundle.putString("payBreed", catProfile.getBreed());
                bundle.putString("payAge", catProfile.getAge());
                bundle.putString("payDescription", catProfile.getDescription());
                bundle.putString("payImage", catProfile.getProfileImage());
                bundle.putString("payStatus", catProfile.getStatus());
                bundle.putString("payRating", String.valueOf(catProfile.getRating()));
                bundle.putString("payPrice", String.valueOf(catProfile.getPrice()));
                bundle.putString("payFrom", catProfile.getAvailableFrom());
                bundle.putString("payTo", catProfile.getAvailableTo());
                bundle.putString("payUser", currentUser.getUid());
                bundle.putString("payCat", catProfile.getId());
                bundle.putString("ownerId", catProfile.getOwnerId());
                PaymentFragment fragment2 = new PaymentFragment();
                fragment2.setArguments(bundle);
                ((mainPage) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, fragment2)
                        .commit();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Method to show DatePickerDialog
    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, day);

                if (selectedDate.before(Calendar.getInstance())) {
                    Toast.makeText(context, "Date cannot be older than today", Toast.LENGTH_SHORT).show();
                    return;
                }

                String date = String.format("%d-%02d-%02d", year, month + 1, day);
                editText.setText(date);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void updateCatProfileForPost(Button btn, CatProfile catProfile, String rate, String fromDate, String toDate, String status) {
        Log.d("updateCatProfileForPost", "rate: " + rate + ", fromDate: " + fromDate + ", toDate: " + toDate + ", status: " + status);
        if (fromDate.isEmpty() || toDate.isEmpty() || status.isEmpty() || Objects.equals(rate, "") || fromDate.isBlank() || toDate.isBlank() || status.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            btn.setEnabled(false);
            // Update catProfile in database
            CatProfile cat = new CatProfile(catProfile.id, catProfile.name, catProfile.age, catProfile.breed, catProfile.description, catProfile.profileImage, status, catProfile.rating, Integer.parseInt(rate), fromDate, toDate, FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.child(catProfile.getId()).setValue(cat);
            databaseReferenceAll.child(catProfile.getId()).setValue(cat);
            catProfile.setPrice(Integer.parseInt(rate));
            catProfile.setAvailableFrom(fromDate);
            catProfile.setAvailableTo(toDate);
            catProfile.setStatus(status);
            Toast.makeText(context, "Posted For Rent", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
            btn.setEnabled(true);
        }
    }

    private void deleteCatProfile(CatProfile catProfile) {
        // Delete catProfile from database
        databaseReference.child(catProfile.getId()).removeValue();
        databaseReferenceAll.child(catProfile.getId()).removeValue();
        // Remove catProfile from list
        catProfiles.remove(catProfile);
        notifyDataSetChanged();
    }

    private void showConfirmToDeleteDialog(final CatProfile catProfile) {
        AlertDialog alertDialog = getAlertDialog(catProfile);

        // Change the text color of the buttons
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(Color.BLACK);

                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(Color.BLACK);
            }
        });

        alertDialog.show();
    }

    private AlertDialog getAlertDialog(CatProfile catProfile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete " + catProfile.getName() + "?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCatProfile(catProfile);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}