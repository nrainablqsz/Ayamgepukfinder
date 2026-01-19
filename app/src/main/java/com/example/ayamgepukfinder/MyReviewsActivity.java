package com.example.ayamgepukfinder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyReviewsActivity extends AppCompatActivity {

    private static final String TAG = "MyReviewsActivity";

    private RecyclerView rvReviews;
    private ProgressBar progressBar;
    private TextView tvNoReviews, tvTitle;
    private Button btnBack; // 🔥 ADD THIS
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference reviewsRef;

    // Add ValueEventListener as a separate variable
    private ValueEventListener reviewsListener;
    private boolean isListenerAttached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();

        // Setup RecyclerView
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, reviewList, true); // Show restaurant name
        rvReviews.setAdapter(reviewAdapter);

        // Setup click listeners
        setupListeners();
    }

    private void initViews() {
        rvReviews = findViewById(R.id.rvReviews);
        progressBar = findViewById(R.id.progressBar);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        tvTitle = findViewById(R.id.tvTitle);

        btnBack = findViewById(R.id.btnBack);

        // Debug logging
        if (btnBack == null) {
            Log.e(TAG, "❌ Back button is NULL! Check your XML layout for btnBack ID");
            // Add ActionBar back button as fallback
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("My Reviews");
            }
        } else {
            Log.d(TAG, "✅ Back button initialized successfully");
        }
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Back button clicked - finishing activity");
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load reviews when activity resumes
        if (currentUser != null) {
            loadMyReviews();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove listener when activity pauses to prevent memory leaks
        removeReviewsListener();
    }

    private void loadMyReviews() {
        if (currentUser == null) {
            tvNoReviews.setText("Please login to see your reviews");
            tvNoReviews.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        String currentUserId = currentUser.getUid();
        reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        Log.d(TAG, "=== DEBUG START ===");
        Log.d(TAG, "Current User ID: " + currentUserId);
        Log.d(TAG, "Firebase path: " + reviewsRef.toString());

        progressBar.setVisibility(View.VISIBLE);
        rvReviews.setVisibility(View.GONE);
        tvNoReviews.setVisibility(View.GONE);

        // Remove any existing listener first
        removeReviewsListener();

        // Create new ValueEventListener
        reviewsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Total restaurant folders found: " + snapshot.getChildrenCount());

                reviewList.clear();
                int totalReviewsFound = 0;
                int restaurantFoldersChecked = 0;

                // Loop through each restaurant folder (1, 2, 3, etc.)
                for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                    String restaurantKey = restaurantSnapshot.getKey();
                    Log.d(TAG, "Checking node: " + restaurantKey);

                    try {
                        Integer.parseInt(restaurantKey);
                        restaurantFoldersChecked++;

                        Log.d(TAG, "  ✓ Restaurant folder: " + restaurantKey +
                                " has " + restaurantSnapshot.getChildrenCount() + " reviews");

                        for (DataSnapshot reviewSnapshot : restaurantSnapshot.getChildren()) {
                            Review review = reviewSnapshot.getValue(Review.class);
                            if (review != null) {
                                Log.d(TAG, "  - Review by user: " + review.getUserId() +
                                        " vs current: " + currentUserId);

                                if (review.getUserId().equals(currentUserId)) {
                                    reviewList.add(review);
                                    totalReviewsFound++;
                                    Log.d(TAG, "  ✓ MATCH! Added review: " + review.getComment());
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        Log.d(TAG, "  ✗ Skipping non-restaurant node: " + restaurantKey);
                    }
                }

                Log.d(TAG, "=== DEBUG END ===");
                Log.d(TAG, "Restaurant folders checked: " + restaurantFoldersChecked);
                Log.d(TAG, "Total reviews found for user: " + totalReviewsFound);

                progressBar.setVisibility(View.GONE);

                if (reviewList.isEmpty()) {
                    tvNoReviews.setText("You haven't written any reviews yet.");
                    tvNoReviews.setVisibility(View.VISIBLE);
                    rvReviews.setVisibility(View.GONE);
                } else {
                    tvNoReviews.setVisibility(View.GONE);
                    rvReviews.setVisibility(View.VISIBLE);
                    reviewAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Total reviews displayed: " + totalReviewsFound);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load reviews: " + error.getMessage());
                progressBar.setVisibility(View.GONE);
                tvNoReviews.setText("Failed to load reviews: " + error.getMessage());
                tvNoReviews.setVisibility(View.VISIBLE);
            }
        };

        if (reviewsRef != null && reviewsListener != null) {
            reviewsRef.addValueEventListener(reviewsListener);
            isListenerAttached = true;
            Log.d(TAG, "Firebase listener attached");
        }
    }

    private void removeReviewsListener() {
        if (reviewsRef != null && reviewsListener != null && isListenerAttached) {
            reviewsRef.removeEventListener(reviewsListener);
            isListenerAttached = false;
            Log.d(TAG, "Firebase listener removed");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeReviewsListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}