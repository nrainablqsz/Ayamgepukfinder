package com.example.ayamgepukfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private CardView[] cardViews = new CardView[5];
    private ImageView[] locationIcons = new ImageView[5];
    private TextView[] tvNames = new TextView[5];
    private TextView[] tvRatings = new TextView[5];
    private TextView[] tvPhones = new TextView[5];
    private TextView[] tvHours = new TextView[5];
    private TextView[] tvAddresses = new TextView[5];
    private LinearLayout[] locationViews = new LinearLayout[5];
    private Button[] btnHideLocations = new Button[5];

    private String[] restaurantNames = {
            "Ayam Gepuk Jogja Shah Alam",
            "Ayam Gepuk Pak Gembus",
            "Ayam Gepuk Top Global",
            "Ayam Gepuk Pak Raden",
            "Ayam Gepuk Tok Mat"
    };

    private String[] restaurantPhones = {
            "📞 012-345 6789",
            "📞 013-987 6543",
            "📞 011-2233 4455",
            "📞 03-5521 3344",
            "📞 019-876 5432"
    };

    private String[] restaurantHours = {
            "⏰ 11:00 AM - 9:30 PM",
            "⏰ 11:00 AM - 9:30 PM",
            "⏰ 11:00 AM - 9:30 PM",
            "⏰ 11:00 AM - 9:30 PM",
            "⏰ 11:00 AM - 9:30 PM"
    };

    private String[] restaurantAddresses = {
            "• 15, Jalan Plumbum S7/S, Seksyen 7, 40000 Shah Alam, Selangor, Malaysia",
            "• Shah Alam S7, 1, Jalan Plumbum Q7/Q, 40000 Shah Alam, Selangor, Malaysia",
            "• S7 Shah Alam, 24-G, Jalan Plumbum N 7/N, 40000 Shah Alam, Selangor, Malaysia",
            "• Blok K, Pusat Komersial, Seksyen 7, 40000 Shah Alam, Selangor, Malaysia",
            "• XG 23, Blok J, Jalan Plumbum X 7/X, Pusat Komersial Seksyen 7, Shah Alam, Malaysia"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (view == null) {
            Log.e(TAG, "Failed to inflate layout!");
            return null;
        }

        try {
            // Initialize CardViews
            cardViews[0] = view.findViewById(R.id.cardRestaurant1);
            cardViews[1] = view.findViewById(R.id.cardRestaurant2);
            cardViews[2] = view.findViewById(R.id.cardRestaurant3);
            cardViews[3] = view.findViewById(R.id.cardRestaurant4);
            cardViews[4] = view.findViewById(R.id.cardRestaurant5);

            // Initialize Location Icons
            locationIcons[0] = view.findViewById(R.id.iconLocation1);
            locationIcons[1] = view.findViewById(R.id.iconLocation2);
            locationIcons[2] = view.findViewById(R.id.iconLocation3);
            locationIcons[3] = view.findViewById(R.id.iconLocation4);
            locationIcons[4] = view.findViewById(R.id.iconLocation5);

            // Initialize TextViews
            tvNames[0] = view.findViewById(R.id.tvRestaurant1Name);
            tvNames[1] = view.findViewById(R.id.tvRestaurant2Name);
            tvNames[2] = view.findViewById(R.id.tvRestaurant3Name);
            tvNames[3] = view.findViewById(R.id.tvRestaurant4Name);
            tvNames[4] = view.findViewById(R.id.tvRestaurant5Name);

            tvRatings[0] = view.findViewById(R.id.tvRestaurant1Rating);
            tvRatings[1] = view.findViewById(R.id.tvRestaurant2Rating);
            tvRatings[2] = view.findViewById(R.id.tvRestaurant3Rating);
            tvRatings[3] = view.findViewById(R.id.tvRestaurant4Rating);
            tvRatings[4] = view.findViewById(R.id.tvRestaurant5Rating);

            tvPhones[0] = view.findViewById(R.id.tvRestaurant1Phone);
            tvPhones[1] = view.findViewById(R.id.tvRestaurant2Phone);
            tvPhones[2] = view.findViewById(R.id.tvRestaurant3Phone);
            tvPhones[3] = view.findViewById(R.id.tvRestaurant4Phone);
            tvPhones[4] = view.findViewById(R.id.tvRestaurant5Phone);

            tvHours[0] = view.findViewById(R.id.tvRestaurant1Hours);
            tvHours[1] = view.findViewById(R.id.tvRestaurant2Hours);
            tvHours[2] = view.findViewById(R.id.tvRestaurant3Hours);
            tvHours[3] = view.findViewById(R.id.tvRestaurant4Hours);
            tvHours[4] = view.findViewById(R.id.tvRestaurant5Hours);

            // Initialize Location Views
            tvAddresses[0] = view.findViewById(R.id.tvRestaurant1Address);
            tvAddresses[1] = view.findViewById(R.id.tvRestaurant2Address);
            tvAddresses[2] = view.findViewById(R.id.tvRestaurant3Address);
            tvAddresses[3] = view.findViewById(R.id.tvRestaurant4Address);
            tvAddresses[4] = view.findViewById(R.id.tvRestaurant5Address);

            locationViews[0] = view.findViewById(R.id.locationView1);
            locationViews[1] = view.findViewById(R.id.locationView2);
            locationViews[2] = view.findViewById(R.id.locationView3);
            locationViews[3] = view.findViewById(R.id.locationView4);
            locationViews[4] = view.findViewById(R.id.locationView5);

            btnHideLocations[0] = view.findViewById(R.id.btnHideLocation1);
            btnHideLocations[1] = view.findViewById(R.id.btnHideLocation2);
            btnHideLocations[2] = view.findViewById(R.id.btnHideLocation3);
            btnHideLocations[3] = view.findViewById(R.id.btnHideLocation4);
            btnHideLocations[4] = view.findViewById(R.id.btnHideLocation5);

            // Set restaurant data to views
            setupStaticRestaurantViews();

            // Load ratings from Firebase
            loadDynamicRatings();

            setupCardClickListeners();
            setupLocationToggleListeners();

            // Initialize About icon
            ImageView iconAbout = view.findViewById(R.id.iconAbout);
            if (iconAbout != null) {
                iconAbout.setOnClickListener(v -> {
                    if (isAdded() && getActivity() != null) {
                        startActivity(new Intent(requireActivity(), AboutActivity.class));
                    }
                });
            } else {
                Log.w(TAG, "About icon not found in layout");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
        }

        return view;
    }

    private void setupStaticRestaurantViews() {
        for (int i = 0; i < 5; i++) {
            // Set static data only
            if (tvNames[i] != null) {
                tvNames[i].setText(restaurantNames[i]);
            }
            if (tvPhones[i] != null) {
                tvPhones[i].setText(restaurantPhones[i]);
            }
            if (tvHours[i] != null) {
                tvHours[i].setText(restaurantHours[i]);
            }
            if (tvAddresses[i] != null) {
                tvAddresses[i].setText(restaurantAddresses[i]);
            }
            if (tvRatings[i] != null) {
                tvRatings[i].setText("Loading...");
            }

            if (locationViews[i] != null) {
                locationViews[i].setVisibility(View.GONE);
            }
        }
    }

    private void loadDynamicRatings() {
        Log.d(TAG, "Loading dynamic ratings from Firebase");

        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        for (int i = 0; i < 5; i++) {
            int restaurantId = i + 1;
            final int index = i;

            reviewsRef.child(String.valueOf(restaurantId))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long reviewCount = snapshot.getChildrenCount();
                            float totalRating = 0;
                            int ratedReviews = 0;

                            Log.d(TAG, "Restaurant " + restaurantId + " has " + reviewCount + " reviews");

                            // Calculate total rating from all reviews
                            for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                                Review review = reviewSnapshot.getValue(Review.class);
                                if (review != null) {
                                    totalRating += review.getRating();
                                    ratedReviews++;
                                    Log.d(TAG, "  - Review rating: " + review.getRating());
                                }
                            }

                            String ratingText = formatRatingText(totalRating, ratedReviews, reviewCount);

                            if (isAdded() && getActivity() != null && tvRatings[index] != null) {
                                getActivity().runOnUiThread(() -> {
                                    tvRatings[index].setText(ratingText);
                                    Log.d(TAG, "Updated restaurant " + restaurantId + " rating: " + ratingText);
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to load reviews for restaurant " + restaurantId + ": " + error.getMessage());

                            // Show error message
                            if (isAdded() && getActivity() != null && tvRatings[index] != null) {
                                getActivity().runOnUiThread(() -> {
                                    tvRatings[index].setText("Error loading reviews");
                                });
                            }
                        }
                    });
        }
    }

    private String formatRatingText(float totalRating, int ratedReviews, long reviewCount) {
        if (ratedReviews == 0) {
            return "No reviews yet";
        }

        // Calculate average rating
        float averageRating = totalRating / ratedReviews;

        // Format to 1 decimal place
        String formattedRating = String.format("%.1f", averageRating);

        // Get stars based on average rating
        String stars = getStarsFromRating(averageRating);

        // Return formatted text
        return stars + " " + formattedRating + " (" + reviewCount + " reviews)";
    }

    private String getStarsFromRating(float rating) {
        // Convert numeric rating to star display
        if (rating >= 4.5) {
            return "⭐⭐⭐⭐⭐";
        } else if (rating >= 3.5) {
            return "⭐⭐⭐⭐☆";
        } else if (rating >= 2.5) {
            return "⭐⭐⭐☆☆";
        } else if (rating >= 1.5) {
            return "⭐⭐☆☆☆";
        } else if (rating >= 0.5) {
            return "⭐☆☆☆☆";
        } else {
            return "☆☆☆☆☆";
        }
    }

    private void setupCardClickListeners() {
        for (int i = 0; i < cardViews.length; i++) {
            int restaurantId = i + 1;
            if (cardViews[i] != null) {
                cardViews[i].setOnClickListener(v -> openRestaurantDetail(restaurantId));
            }
        }
    }

    private void setupLocationToggleListeners() {
        for (int i = 0; i < locationIcons.length; i++) {
            final int index = i;

            if (locationIcons[i] != null) {
                locationIcons[i].setOnClickListener(v -> {
                    if (locationViews[index] != null) {
                        if (locationViews[index].getVisibility() == View.VISIBLE) {
                            locationViews[index].setVisibility(View.GONE);
                        } else {
                            locationViews[index].setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            if (btnHideLocations[i] != null) {
                btnHideLocations[i].setOnClickListener(v -> {
                    if (locationViews[index] != null) {
                        locationViews[index].setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    private void openRestaurantDetail(int restaurantId) {
        if (isAdded() && getActivity() != null) {
            try {
                Intent intent = new Intent(requireActivity(), RestaurantDetailActivity.class);
                intent.putExtra("restaurant_id", restaurantId);
                intent.putExtra("restaurant_name", restaurantNames[restaurantId-1]);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error opening restaurant detail: " + e.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh ratings when fragment resumes
        Log.d(TAG, "Fragment resumed - refreshing ratings");
        loadDynamicRatings();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (int i = 0; i < 5; i++) {
            if (cardViews[i] != null) {
                cardViews[i].setOnClickListener(null);
            }
            if (locationIcons[i] != null) {
                locationIcons[i].setOnClickListener(null);
            }
            if (btnHideLocations[i] != null) {
                btnHideLocations[i].setOnClickListener(null);
            }
        }
    }
}