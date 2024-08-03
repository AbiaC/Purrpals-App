package com.example.purrpalsapp.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.create.User;
import com.example.purrpalsapp.utils.LocalDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class fragment_profile extends Fragment {

    View view;
    private Button editProfileButton;
    private ImageView userImageView;
    private TextView usernameTextView, nameTextView, birthdateTextView, phoneTextView, emailTextView, passwordTextView;
    private LocalDataManager dataManager;

    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        dataManager = new LocalDataManager(getContext());

        user = FirebaseAuth.getInstance().getCurrentUser();
        userImageView = view.findViewById(R.id.userImageView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        birthdateTextView = view.findViewById(R.id.birthdateTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        passwordTextView = view.findViewById(R.id.passwordTextView);

        editProfileButton = view.findViewById(R.id.editProfileButton);

        userImageView.setImageURI(user.getPhotoUrl());

        // Get the Firebase Realtime Database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference usersRef = database.getReference("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    // Set data to TextViews
                    assert user != null;
                    usernameTextView.setText("User" + user.getPhoneNumber());
                    nameTextView.setText(user.getName());
                    birthdateTextView.setText(user.getDob());
                    phoneTextView.setText(user.getPhoneNumber());
                    emailTextView.setText(user.getEmail());
                    if (!Objects.equals(user.getProfileImage(), "")) {
                        Glide.with(requireContext()).load(user.getProfileImage()).into(userImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new fragment_editProfile());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}