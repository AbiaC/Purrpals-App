package com.example.purrpalsapp.ui.search;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.cat.profile.CatProfile;
import com.example.purrpalsapp.ui.cat.profile.CatProfileAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragment_search extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private CatProfileAdapter adapter;
    private DatabaseReference mRef;
    private ProgressBar progressBar;
    private List<CatProfile> catProfiles = new ArrayList<>();
    private List<CatProfile> filteredCatProfiles = new ArrayList<>();

    private EditText searchEditText, pickUpDateEditText, dropOffDateEditText;
    private SeekBar rateSeekBar;
    private RatingBar ratingBar;
    private Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        progressBar = view.findViewById(R.id.progress_bar_search);

        searchEditText = view.findViewById(R.id.searchEditText);
        pickUpDateEditText = view.findViewById(R.id.pickUpDateEditText);
        dropOffDateEditText = view.findViewById(R.id.dropOffDateEditText);
        rateSeekBar = view.findViewById(R.id.rateSeekBar);
        ratingBar = view.findViewById(R.id.ratingBar);
        searchButton = view.findViewById(R.id.searchButton);

        pickUpDateEditText.setFocusable(false);
        pickUpDateEditText.setClickable(true);
        pickUpDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(pickUpDateEditText);
            }
        });
        dropOffDateEditText.setFocusable(false);
        dropOffDateEditText.setClickable(true);
        dropOffDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(dropOffDateEditText);
            }
        });
        mRef = FirebaseDatabase.getInstance().getReference("cats");

        // Load data from Firebase
        loadDataFromFirebase();

        // Set up search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterResults();
            }
        });


        return view;
    }

    // Method to show DatePickerDialog
    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, day);

                String date = String.format("%d-%02d-%02d", year, month + 1, day);
                editText.setText(date);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void loadDataFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (isAdded()) { // Check if the fragment is added to the activity
                    catProfiles.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CatProfile catProfile = snapshot.getValue(CatProfile.class);
                        if (catProfile != null) {
                            catProfiles.add(catProfile);
                        }
                    }
                    // Initially display all cat profiles
                    adapter = new CatProfileAdapter(catProfiles, getContext(), 3);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                if (isAdded()) {
                    Log.e("Error", "Error retrieving cat profiles");
                }
            }
        });
    }

    private void filterResults() {
        String searchText = searchEditText.getText().toString().toLowerCase();
        int maxRate = rateSeekBar.getProgress();
        float minRating = ratingBar.getRating();
        String pickUpDate = pickUpDateEditText.getText().toString();
        String dropOffDate = dropOffDateEditText.getText().toString();

        filteredCatProfiles.clear();

        for (CatProfile cat : catProfiles) {
            boolean matchesSearchText = (cat.getName() != null && cat.getName().toLowerCase().contains(searchText)) || (cat.getBreed() != null && cat.getBreed().toLowerCase().contains(searchText) ||searchText.isEmpty());
            boolean matchesRate = cat.getPrice() >= maxRate;
            boolean matchesRating = cat.getRating() >= minRating;
            boolean matchesDateRange = isDateInRange(cat.getAvailableFrom(), cat.getAvailableTo(), pickUpDate, dropOffDate);

            if (matchesSearchText && matchesRate && matchesRating ) {
                filteredCatProfiles.add(cat);
            }
        }

        if (filteredCatProfiles.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), filteredCatProfiles.size()+" Results found from search", Toast.LENGTH_SHORT).show();
        }

        adapter.updateData(filteredCatProfiles);
    }

    private boolean isDateInRange(String availableFrom, String availableTo, String pickUpDate, String dropOffDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date availableFromDate = sdf.parse(availableFrom);
            Date availableToDate = sdf.parse(availableTo);
            Date pickUpDateParsed = sdf.parse(pickUpDate);
            Date dropOffDateParsed = sdf.parse(dropOffDate);

            return pickUpDateParsed != null && dropOffDateParsed != null && pickUpDateParsed.after(availableFromDate) && dropOffDateParsed.before(availableToDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRef.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Remove the listener
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Remove the listener
            }
        });
    }
}
