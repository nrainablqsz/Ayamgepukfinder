package com.example.ayamgepukfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Log.d(TAG, "AboutActivity created");

        // Back button functionality
        Button btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Back button clicked");
                    finish(); // Close this activity and go back
                }
            });
        } else {
            Log.e(TAG, "Back button not found in layout");
        }

        // GitHub link functionality
        TextView txtGithubLink = findViewById(R.id.txtGithubLink);
        if (txtGithubLink != null) {
            txtGithubLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "GitHub link clicked");
                    // Replace with your actual GitHub repository URL
                    String githubUrl = "https://github.com/yourusername/ayam-gepuk-finder";

                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to open GitHub link: " + e.getMessage());
                    }
                }
            });
        } else {
            Log.e(TAG, "GitHub link TextView not found in layout");
        }

        setupMemberImageClickListeners();
    }

    private void setupMemberImageClickListeners() {
        // You can add functionality when member images are clicked
        int[] imageIds = {
                R.id.imageMember1,
                R.id.imageMember2,
                R.id.imageMember3,
                R.id.imageMember4
        };

        for (int i = 0; i < imageIds.length; i++) {
            ImageView imageView = findViewById(imageIds[i]);
            if (imageView != null) {
                final int memberIndex = i + 1;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Member " + memberIndex + " image clicked");
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AboutActivity destroyed");
    }
}