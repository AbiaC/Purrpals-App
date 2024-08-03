package com.example.purrpalsapp.ui.cat.profile;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class fragment_create_cat_profile extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private View view;
    private EditText catName, catAge, catBreed, catDescription;
    private Button submitButton, uploadImageButton;
    private ImageView catImageView;
    private Uri selectedImageUri;
    private String catProfileImageUrl = "";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_create_cat_profile, container, false);

        catName = view.findViewById(R.id.cat_name);
        catAge = view.findViewById(R.id.cat_age);
        catBreed = view.findViewById(R.id.cat_breed);
        catDescription = view.findViewById(R.id.cat_description);
        submitButton = view.findViewById(R.id.submit_button);
        uploadImageButton = view.findViewById(R.id.upload_image_button);
        catImageView = view.findViewById(R.id.cat_image_view);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).child("cats");
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("cat_images");

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    submitButton.setEnabled(false);
                    uploadImageToFirebaseStorage(selectedImageUri);
                } else {
                    Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            catImageView.setImageURI(selectedImageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference imageRef = mStorageRef.child(imageUri.getLastPathSegment());
        imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            catProfileImageUrl = uri.toString();
                            String catId = mRef.push().getKey(); // Generate a unique key
                            List<Float> rate = List.of(3.0f,3.5f,4.0f,4.5f,5.0f);
                            // Generate a random rating from this list
                            float randomRating = rate.get((int) (Math.random() * rate.size()));

                            CatProfile catProfile = new CatProfile(catId, catName.getText().toString(), catAge.getText().toString(), catBreed.getText().toString(), catDescription.getText().toString(), catProfileImageUrl, "Unavailable", randomRating, 0, "", "",
                                    FirebaseAuth.getInstance().getCurrentUser().getUid());
                            mRef.child(catId).setValue(catProfile);
                            Toast.makeText(getActivity(), "Cat profile created successfully", Toast.LENGTH_SHORT).show();
                            catName.setText("");
                            catAge.setText("");
                            catBreed.setText("");
                            catDescription.setText("");
                            catImageView.setImageResource(R.drawable.baseline_add_circle_24);
                            submitButton.setEnabled(true);
                        }
                    });
                } else {
                    submitButton.setEnabled(true);
                    Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}