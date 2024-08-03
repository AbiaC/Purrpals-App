package com.example.purrpalsapp.ui.login;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.mainPage;
import com.example.purrpalsapp.ui.create.createAccountPage;
import com.example.purrpalsapp.ui.reset.resetPasswordPage;
import com.example.purrpalsapp.utils.LocalDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class initialPage extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private ImageButton btnSignIn;
    private FirebaseAuth mAuth;
    private LocalDataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.initial_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);

        mAuth = FirebaseAuth.getInstance();
        dataManager = new LocalDataManager(this);

        checkIfLoggedIn();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(initialPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    signIn(username, password);
                }
            }
        });
    }

    private void checkIfLoggedIn() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            dataManager.saveIsLoggedIn(true);
            Intent intent = new Intent(initialPage.this, mainPage.class);
            startActivity(intent);
            finish();
        }
    }

    private void signIn(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        dataManager.saveIsLoggedIn(true);
                        dataManager.saveEmail(username);
                        dataManager.saveUserPass(password);
                        dataManager.saveName(user.getDisplayName());
                        dataManager.saveIsLoggedIn(true);
                        Intent intent = new Intent(initialPage.this, mainPage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(initialPage.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void moveToResetPasswordPage(View view) {
        Intent intent = new Intent(initialPage.this, resetPasswordPage.class);
        startActivity(intent);
    }

    public void moveToCreateAccountPage(View view) {
        Intent intent = new Intent(initialPage.this, createAccountPage.class);
        startActivity(intent);
    }
}