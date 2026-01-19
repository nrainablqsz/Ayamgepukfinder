package com.example.ayamgepukfinder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etAddress;
    private Button btnSave, btnChangePassword, btnBack;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        // Check if user is logged in
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load current user data
        loadUserData();

        // Save button click
        btnSave.setOnClickListener(v -> updateProfile());

        // Change password button click
        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, ChangePasswordActivity.class));
        });

        // Back button click
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            // Get display name
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                etName.setText(displayName);
            } else {
                etName.setText("");
            }

            // Get email
            String email = currentUser.getEmail();
            if (email != null) {
                etEmail.setText(email);
            }

            etPhone.setText("");
            etAddress.setText("");

            etEmail.setEnabled(false);
            etEmail.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Name validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (name.length() < 2) {
            etName.setError("Name must be at least 2 characters");
            etName.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        if (currentUser != null) {
            // Update profile in Firebase Authentication
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(EditProfileActivity.this,
                                        "Profile updated successfully\nName saved, phone/address will be saved when you add database",
                                        Toast.LENGTH_LONG).show();

                                finish();
                            } else {
                                String errorMessage = task.getException() != null ?
                                        task.getException().getMessage() : "Update failed";
                                Toast.makeText(EditProfileActivity.this,
                                        "Error: " + errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}