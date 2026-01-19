package com.example.ayamgepukfinder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment {

    private static final String ARG_RESTAURANT_ID = "restaurant_id";
    private static final String TAG = "ReviewsFragment";

    private RecyclerView recyclerView;
    private TextView tvEmptyReviews;
    private ReviewAdapter reviewAdapter;
    private final List<Review> reviewList = new ArrayList<>();

    private DatabaseReference reviewsRef;
    private int currentRestaurantId;

    public static ReviewsFragment newInstance(int restaurantId) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RESTAURANT_ID, restaurantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        recyclerView = view.findViewById(R.id.recyclerReviews);
        tvEmptyReviews = view.findViewById(R.id.tvEmptyReviews);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reviewAdapter = new ReviewAdapter(
                requireContext(),
                reviewList,
                false
        );

        recyclerView.setAdapter(reviewAdapter);

        currentRestaurantId = getArguments().getInt(ARG_RESTAURANT_ID);

        // Log which restaurant we're loading reviews for
        Log.d(TAG, "Loading reviews for restaurant ID: " + currentRestaurantId);
        Toast.makeText(getContext(), "Loading reviews for restaurant: " + currentRestaurantId,
                Toast.LENGTH_SHORT).show();

        // Get reference to reviews node for this specific restaurant
        reviewsRef = FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(String.valueOf(currentRestaurantId));

        // Log the Firebase path we're querying
        Log.d(TAG, "Firebase path: " + reviewsRef.toString());

        loadReviews();

        return view;
    }

    private void loadReviews() {
        Log.d(TAG, "Starting to load reviews from: " + reviewsRef.toString());

        reviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: Found " + snapshot.getChildrenCount() + " reviews");

                reviewList.clear();

                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    count++;
                    Review review = ds.getValue(Review.class);
                    if (review != null) {
                        Log.d(TAG, "Review " + count + ": " + review.getUserName() +
                                " - " + review.getComment());
                        reviewList.add(review);
                    } else {
                        Log.d(TAG, "Review " + count + ": NULL (couldn't parse)");
                    }
                }

                reviewAdapter.notifyDataSetChanged();

                if (reviewList.isEmpty()) {
                    Log.d(TAG, "No reviews found");
                    tvEmptyReviews.setVisibility(View.VISIBLE);
                    tvEmptyReviews.setText("No reviews yet. Be the first to review!");
                } else {
                    Log.d(TAG, "Found " + reviewList.size() + " reviews");
                    tvEmptyReviews.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load reviews: " + error.getMessage());
                tvEmptyReviews.setVisibility(View.VISIBLE);
                tvEmptyReviews.setText("Failed to load reviews: " + error.getMessage());
            }
        });
    }
}