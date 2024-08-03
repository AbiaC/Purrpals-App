package com.example.purrpalsapp.ui.create;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.login.initialPage;
import com.example.purrpalsapp.ui.terms.termsAndServicePage;
import com.example.purrpalsapp.utils.LocalDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class createAccountPage extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword, etPhoneNumber;
    private CheckBox cbAcceptTerms;
    private ImageButton btnCreateAccount;
    private FirebaseAuth mAuth;
    private LocalDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_create_email);
        etPassword = findViewById(R.id.et_create_password);
        etConfirmPassword = findViewById(R.id.et_create_confirmPassword);
        etPhoneNumber = findViewById(R.id.et_create_phoneNumber);
        cbAcceptTerms = findViewById(R.id.cb_create_acceptTerms);
        btnCreateAccount = findViewById(R.id.imageButton);

        mAuth = FirebaseAuth.getInstance();
        dataManager = new LocalDataManager(this);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(createAccountPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(createAccountPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if (!cbAcceptTerms.isChecked()) {
                    Toast.makeText(createAccountPage.this, "Please accept terms and conditions", Toast.LENGTH_SHORT).show();
                } else {
                    createAccount(email, password, phoneNumber);
                }
            }
        });
    }

    private void createAccount(String email, String password, final String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userId = user.getUid();
                        dataManager.saveName(user.getDisplayName());
                        dataManager.saveEmail(email);
                        dataManager.savePhoneNumber(phoneNumber);
                        dataManager.saveIsLoggedIn(true);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference usersRef = database.getReference("users");
                        User userData = new User(user.getUid(), "", user.getDisplayName(), email, phoneNumber, "1990-01-01", 30, password);
                        usersRef.child(userId).setValue(userData);

                        Toast.makeText(createAccountPage.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(createAccountPage.this, initialPage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(createAccountPage.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void MoveToTermsAndService(View view) {
        Intent intent = new Intent(createAccountPage.this, termsAndServicePage.class);
        startActivity(intent);
    }
}