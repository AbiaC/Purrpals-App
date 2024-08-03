package com.example.purrpalsapp.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.create.User;
import com.example.purrpalsapp.utils.LocalDataManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class fragment_editProfile extends Fragment {

    private ImageView userImageView;
    private EditText editUsername;
    private EditText editBirthdate;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editPassword;
    private Button saveProfileButton;

    private LocalDataManager localDataManager;

    private FirebaseUser user;
    private Uri selectedImageUri;
    private String userProfileImageUrl = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userImageView = view.findViewById(R.id.userImageView);
        editUsername = view.findViewById(R.id.editUsername);
        editBirthdate = view.findViewById(R.id.editBirthdate);
        editPhone = view.findViewById(R.id.editPhone);
        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);
        saveProfileButton = view.findViewById(R.id.saveProfileButton);

        localDataManager = new LocalDataManager(getActivity());

        // Load data from Firebase Realtime Database
        loadProfileDataFromDatabase();

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        userImageView.setImageURI(user.getPhotoUrl());

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    private void openGalleryAndUploadToDatabase(Uri imageUri) {
        Toast.makeText(requireActivity(), "Setting profile...", Toast.LENGTH_SHORT).show();
        saveProfileButton.setEnabled(false);
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        StorageReference mStorageRef = mStorage.getReference("profile_images");
        StorageReference imageRef = mStorageRef.child(imageUri.getLastPathSegment());
        imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userProfileImageUrl = uri.toString();
                            saveProfileButton.setEnabled(true);
                        }
                    });
                } else {
                    saveProfileButton.setEnabled(true);
                    Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            userImageView.setImageURI(selectedImageUri);
            if (selectedImageUri != null) {
                openGalleryAndUploadToDatabase(selectedImageUri);
            }
        }
    }

    private void loadProfileDataFromDatabase() {
        // Get the Firebase Realtime Database instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference usersRef = database.getReference("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    // Set data to EditTexts
                    editUsername.setText(user.getName());
                    editBirthdate.setText(user.getDob());
                    editPhone.setText(user.getPhoneNumber());
                    editEmail.setText(user.getEmail());
                    editPassword.setText(user.getPassword());

                    // Save data to SharedPreferences
                    localDataManager.saveName(user.getName());
                    localDataManager.saveUserDob(user.getDob());
                    localDataManager.savePhoneNumber(user.getPhoneNumber());
                    localDataManager.saveUserPass(user.getPassword());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String username = editUsername.getText().toString().trim();
        String birthdate = editBirthdate.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Check if user made changes to field
        if (!username.equals(localDataManager.getName()) ||
                !birthdate.equals(localDataManager.getDob()) ||
                !phone.equals(localDataManager.getPhoneNumber()) ||
                !password.equals(localDataManager.getPass())) {

            // Update SharedPreferences
            localDataManager.saveName(username);
            localDataManager.saveUserDob(birthdate);
            localDataManager.savePhoneNumber(phone);
            localDataManager.saveUserPass(password);

            // Get the Firebase Realtime Database instance
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference usersRef = database.getReference("users");
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            User user = new User(user1.getUid(), userProfileImageUrl, username, email, phone, birthdate, 30, password);
            usersRef.child(userId).setValue(user);

            Toast.makeText(getContext(), "Profile saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No changes made", Toast.LENGTH_SHORT).show();
        }
    }
}