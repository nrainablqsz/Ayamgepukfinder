package com.example.ayamgepukfinder;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private ImageView ivCurrentPasswordToggle, ivNewPasswordToggle, ivConfirmPasswordToggle;
    private Button btnChangePassword, btnBack;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private boolean isCurrentPasswordVisible = false;
    private boolean isNewPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        ivCurrentPasswordToggle = findViewById(R.id.ivCurrentPasswordToggle);
        ivNewPasswordToggle = findViewById(R.id.ivNewPasswordToggle);
        ivConfirmPasswordToggle = findViewById(R.id.ivConfirmPasswordToggle);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Password toggles
        ivCurrentPasswordToggle.setOnClickListener(v -> toggleCurrentPasswordVisibility());
        ivNewPasswordToggle.setOnClickListener(v -> toggleNewPasswordVisibility());
        ivConfirmPasswordToggle.setOnClickListener(v -> toggleConfirmPasswordVisibility());

        // Change password button click
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void toggleCurrentPasswordVisibility() {
        if (isCurrentPasswordVisible) {
            // Hide password
            etCurrentPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivCurrentPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // Show password
            etCurrentPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivCurrentPasswordToggle.setImageResource(R.drawable.ic_visibility);
        }

        // Move cursor to end
        etCurrentPassword.setSelection(etCurrentPassword.getText().length());
        isCurrentPasswordVisible = !isCurrentPasswordVisible;
    }

    private void toggleNewPasswordVisibility() {
        if (isNewPasswordVisible) {
            // Hide password
            etNewPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivNewPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // Show password
            etNewPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivNewPasswordToggle.setImageResource(R.drawable.ic_visibility);
        }

        // Move cursor to end
        etNewPassword.setSelection(etNewPassword.getText().length());
        isNewPasswordVisible = !isNewPasswordVisible;
    }

    private void toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            // Hide password
            etConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivConfirmPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
        } else {
            // Show password
            etConfirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivConfirmPasswordToggle.setImageResource(R.drawable.ic_visibility);
        }

        // Move cursor to end
        etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Current password is required");
            etCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Please login to change password", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnChangePassword.setEnabled(false);

        AuthCredential credential = EmailAuthProvider
                .getCredential(currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> reauthTask) {
                        if (reauthTask.isSuccessful()) {

                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> updateTask) {
                                            progressBar.setVisibility(View.GONE);
                                            btnChangePassword.setEnabled(true);

                                            if (updateTask.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this,
                                                        "Password changed successfully!",
                                                        Toast.LENGTH_LONG).show();
                                                finish();
                                            } else {
                                                String errorMessage = updateTask.getException() != null ?
                                                        updateTask.getException().getMessage() : "Password change failed";
                                                Toast.makeText(ChangePasswordActivity.this,
                                                        "Failed: " + errorMessage,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {

                            progressBar.setVisibility(View.GONE);
                            btnChangePassword.setEnabled(true);

                            String errorMessage = reauthTask.getException() != null ?
                                    reauthTask.getException().getMessage() : "Re-authentication failed";

                            if (errorMessage.contains("wrong-password") || errorMessage.contains("invalid-credential")) {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Current password is incorrect",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Re-authentication failed: " + errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
}