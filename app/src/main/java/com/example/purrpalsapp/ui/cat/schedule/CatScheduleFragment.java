package com.example.purrpalsapp.ui.cat.schedule;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.chat.MyPaymentsAdapter;
import com.example.purrpalsapp.ui.payment.PaymentModal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CatScheduleFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private MyPaymentsAdapter adapter;
    private DatabaseReference mRef;
    private ProgressBar progressBar;
    private TextView noDataTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cat_schedule, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        progressBar = view.findViewById(R.id.progress_reny_cat);
        noDataTextView = view.findViewById(R.id.no_data_found);
        mRef = FirebaseDatabase.getInstance().getReference("payments");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (isAdded()) { // Check if the fragment is added to the activity
                    List<PaymentModal> catProfiles = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PaymentModal catProfile;
                        catProfile = snapshot.getValue(PaymentModal.class);
                        catProfiles.add(catProfile);
                    }
                    adapter = new MyPaymentsAdapter(catProfiles, getContext(), 2);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                noDataTextView.setVisibility(View.VISIBLE);
                if (isAdded()) { // Check if the fragment is added to the activity
                    Log.e("Error", "Error retrieving cat profiles");
                }
            }
        });

        return view;
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