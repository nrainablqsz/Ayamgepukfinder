package com.example.ayamgepukfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail;
    private Button btnLogout;
    private LinearLayout btnEditProfile;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        if (user != null) {
            String name = user.getDisplayName();

            // If no display name is set, use email username part
            if (name == null || name.isEmpty()) {
                String email = user.getEmail();
                name = extractUsernameFromEmail(email);
            }

            tvName.setText(name);
            tvEmail.setText(user.getEmail());
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        // Edit Profile LinearLayout click
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        // Find "My Reviews" LinearLayout
        LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.btnEditProfile).getParent();
        LinearLayout btnMyReviews = (LinearLayout) parentLayout.getChildAt(2);

        if (btnMyReviews != null) {
            btnMyReviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), MyReviewsActivity.class));
                }
            });
        }
    }

    /**
     * Extract username from email (e.g., "john@gmail.com" -> "John")
     */
    private String extractUsernameFromEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "User";
        }

        // Extract part before @
        String username;
        if (email.contains("@")) {
            username = email.substring(0, email.indexOf("@"));
        } else {
            username = email;
        }

        // Remove any numbers or special characters at the end
        username = username.replaceAll("[0-9]+$", "");

        // Capitalize first letter
        if (!username.isEmpty()) {
            username = username.substring(0, 1).toUpperCase() +
                    (username.length() > 1 ? username.substring(1) : "");
        } else {
            username = "User";
        }

        return username;
    }
}