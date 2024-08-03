package com.example.purrpalsapp.ui.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.mainPage;
import com.example.purrpalsapp.ui.payment.PaymentModal;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyPaymentsAdapter extends RecyclerView.Adapter<MyPaymentsAdapter.MessageViewHolder> {
    private List<PaymentModal> mMessages;
    private Context mContext;
    private int uses;

    public MyPaymentsAdapter(List<PaymentModal> paymentModals, Context context, int uses) {
        mMessages = paymentModals;
        mContext = context;
        this.uses = uses;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_payments_item, parent, false);
        return new MessageViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        PaymentModal paymentModal = mMessages.get(position);
        if (uses == 1) {
            if (paymentModal.getPayUserId().matches(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.amount.setVisibility(View.VISIBLE);
                holder.srNo.setVisibility(View.VISIBLE);
                holder.rentDate.setVisibility(View.VISIBLE);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(paymentModal.getPayDate(), formatter);
                LocalDate currentDate = LocalDate.now();
                String status;
                if (date.isBefore(currentDate)) {
                    status = "In-Coming";
                } else if (date.isAfter(currentDate)) {
                    status = "Completed";
                } else {
                    status = "In-Progress";
                }
                holder.amount.setText("Amount Paid: $" + paymentModal.getPayAmount() + " ( " + status + " )");
                holder.srNo.setText(paymentModal.getPayCatName() + " Period: " + paymentModal.getPayHours() + " Hours");
                holder.rentDate.setText("Rent Date: " + paymentModal.getPayDate());
            } else {
                holder.amount.setVisibility(View.GONE);
                holder.srNo.setVisibility(View.GONE);
                holder.rentDate.setVisibility(View.GONE);
            }
        } else if (uses == 2) {
            if (paymentModal.getOwnerId().matches(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.amount.setVisibility(View.VISIBLE);
                holder.srNo.setVisibility(View.VISIBLE);
                holder.rentDate.setVisibility(View.VISIBLE);
                holder.amount.setText("Amount Paid: $" + paymentModal.getPayAmount() + " For " + paymentModal.getPayHours() + " Hours");
                holder.srNo.setText(paymentModal.getPayCatName() + " (" + paymentModal.getPayCatStatus() + ")");
                holder.rentDate.setText("Rent Date: " + paymentModal.getPayDate());
            } else {
                holder.amount.setVisibility(View.GONE);
                holder.srNo.setVisibility(View.GONE);
                holder.rentDate.setVisibility(View.GONE);
            }
        } else if (uses == 3) {
            if (paymentModal.getOwnerId().matches(FirebaseAuth.getInstance().getCurrentUser().getUid()) || paymentModal.getPayUserId().matches(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.amount.setVisibility(View.VISIBLE);
                holder.srNo.setVisibility(View.GONE);
                holder.rentDate.setVisibility(View.VISIBLE);
                holder.amount.setText("Amount Paid: $" + paymentModal.getPayAmount() + " By " + paymentModal.getEmail());
                holder.rentDate.setText("Rent Date: " + paymentModal.getPayDate() + " For " + paymentModal.getPayCatName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment = new ChatFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("payId", paymentModal.getPayId());
                        fragment.setArguments(bundle);
                        ((mainPage) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    }
                });
            } else {
                holder.amount.setVisibility(View.GONE);
                holder.srNo.setVisibility(View.GONE);
                holder.rentDate.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView amount, srNo, rentDate;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount_payment);
            srNo = itemView.findViewById(R.id.sr_no_and_catName);
            rentDate = itemView.findViewById(R.id.rent_date_payment);
        }
    }

    private String getTime(long time) {
        // Convert timestamp to a readable time format
        Date date = new Date(time * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        return sdf.format(date);
    }
}

