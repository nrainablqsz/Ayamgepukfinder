package com.example.ayamgepukfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class RestaurantDetailActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantDetailActivity";

    // UI Components
    private ImageView ivBack, ivFavorite, ivRestaurant;
    private TextView tvRestaurantName, tvRating, tvLocation, tvHours, tvReviewStats;
    private Button btnDirection, btnViewMenu, btnWriteReview, btnCall;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    // Data variables
    private boolean isFavorite = false;
    private int restaurantId;
    private String restaurantName;
    private String phoneNumberToCall;

    // Firebase references
    private DatabaseReference databaseReference;
    private DatabaseReference favoritesReference;
    private DatabaseReference reviewsReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        initializeFirebase();
        initializeViews();
        getIntentData();
        setupViewPager();
        checkIfFavorite();
        loadRestaurantReviews();
        setupClickListeners();
    }

    private void initializeFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("restaurants");
        reviewsReference = firebaseDatabase.getReference("reviews");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            favoritesReference = firebaseDatabase.getReference("favorites")
                    .child(currentUser.getUid());
        }
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
        ivFavorite = findViewById(R.id.ivFavorite);
        ivRestaurant = findViewById(R.id.ivRestaurant);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRating = findViewById(R.id.tvRating);
        tvLocation = findViewById(R.id.tvLocation);
        tvHours = findViewById(R.id.tvHours);
        tvReviewStats = findViewById(R.id.tvReviewStats); // ✅ ADDED - You need to add this TextView in XML
        btnDirection = findViewById(R.id.btnDirection);
        btnViewMenu = findViewById(R.id.btnViewMenu);
        btnWriteReview = findViewById(R.id.btnWriteReview);
        btnCall = findViewById(R.id.btnCall);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        restaurantId = intent.getIntExtra("restaurant_id", -1);

        if (restaurantId == -1) {
            Toast.makeText(this, "Restaurant data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setRestaurantData(restaurantId);
        setRestaurantImage(restaurantId);
    }

    private void setRestaurantImage(int restaurantId) {
        int imageResource;

        switch (restaurantId) {
            case 1: // Ayam Gepuk Jogja Shah Alam
                imageResource = R.drawable.jogja;
                break;
            case 2: // Ayam Gepuk Pak Gembus
                imageResource = R.drawable.pakgembus;
                break;
            case 3: // Ayam Gepuk Top Global
                imageResource = R.drawable.topglobal;
                break;
            case 4: // Ayam Gepuk Pak Raden
                imageResource = R.drawable.pakraden;
                break;
            case 5: // Ayam Gepuk Tok Mat
                imageResource = R.drawable.tokmat;
                break;
            default:
                imageResource = R.drawable.pakgembus; // Default fallback
                break;
        }

        ivRestaurant.setImageResource(imageResource);
    }

    private void setRestaurantData(int restaurantId) {
        switch (restaurantId) {
            case 1:
                restaurantName = "Ayam Gepuk Jogja Shah Alam";
                tvRestaurantName.setText(restaurantName);
                tvLocation.setText("15, Jalan Plumbum S7/S, Seksyen 7, 40000 Shah Alam, Selangor, Malaysia");
                tvHours.setText("11:00 AM - 9:30 PM");
                phoneNumberToCall = "0123456789";
                break;
            case 2:
                restaurantName = "Ayam Gepuk Pak Gembus";
                tvRestaurantName.setText(restaurantName);
                tvLocation.setText("Shah Alam S7, 1, Jalan Plumbum Q7/Q, 40000 Shah Alam, Selangor, Malaysia");
                tvHours.setText("11:00 AM - 9:30 PM");
                phoneNumberToCall = "0139876543";
                break;
            case 3:
                restaurantName = "Ayam Gepuk Top Global";
                tvRestaurantName.setText(restaurantName);
                tvLocation.setText("S7 Shah Alam, 24-G, Jalan Plumbum N 7/N, 40000 Shah Alam, Selangor, Malaysia");
                tvHours.setText("11:00 AM - 9:30 PM");
                phoneNumberToCall = "01122334455";
                break;
            case 4:
                restaurantName = "Ayam Gepuk Pak Raden";
                tvRestaurantName.setText(restaurantName);
                tvLocation.setText("Blok K, Pusat Komersial, Seksyen 7, 40000 Shah Alam, Selangor, Malaysia");
                tvHours.setText("11:00 AM - 9:30 PM");
                phoneNumberToCall = "0355213344";
                break;
            case 5:
                restaurantName = "Ayam Gepuk Tok Mat";
                tvRestaurantName.setText(restaurantName);
                tvLocation.setText("XG 23, Blok J, Jalan Plumbum X 7/X, Pusat Komersial Seksyen 7, Shah Alam, Malaysia");
                tvHours.setText("11:00 AM - 9:30 PM");
                phoneNumberToCall = "0198765432";
                break;
        }

        tvRating.setText("Loading...");
        if (tvReviewStats != null) {
            tvReviewStats.setText("Loading reviews...");
        }
    }

    private void loadRestaurantReviews() {
        if (restaurantId == -1) {
            tvRating.setText("N/A");
            if (tvReviewStats != null) {
                tvReviewStats.setText("No reviews yet");
            }
            return;
        }

        reviewsReference.child(String.valueOf(restaurantId))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long totalReviews = snapshot.getChildrenCount();
                        float totalRating = 0;
                        int ratedReviews = 0;

                        Log.d(TAG, "Restaurant " + restaurantId + " has " + totalReviews + " reviews");

                        // Calculate total rating from all reviews
                        for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                            Review review = reviewSnapshot.getValue(Review.class);
                            if (review != null) {
                                totalRating += review.getRating();
                                ratedReviews++;
                                Log.d(TAG, "  - Review rating: " + review.getRating());
                            }
                        }

                        // Update UI with calculated ratings
                        updateRestaurantRatingUI(totalRating, ratedReviews, totalReviews);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to load reviews: " + error.getMessage());
                        tvRating.setText("N/A");
                        if (tvReviewStats != null) {
                            tvReviewStats.setText("Error loading reviews");
                        }
                    }
                });
    }

    private void updateRestaurantRatingUI(float totalRating, int ratedReviews, long totalReviews) {
        if (ratedReviews == 0) {
            tvRating.setText("N/A");
            if (tvReviewStats != null) {
                tvReviewStats.setText("No reviews yet");
            }
            return;
        }

        // Calculate average rating
        float averageRating = totalRating / ratedReviews;

        // Format to 1 decimal place
        String formattedRating = String.format("%.1f", averageRating);

        // Update rating TextView
        tvRating.setText(formattedRating);

        // Update review stats TextView
        if (tvReviewStats != null) {
            String reviewText;
            if (totalReviews == 1) {
                reviewText = "1 review";
            } else {
                reviewText = totalReviews + " reviews";
            }

            // Add star rating text
            String starRating = getStarRatingText(averageRating);
            tvReviewStats.setText(starRating + " • " + reviewText);
        }

        Log.d(TAG, "Updated restaurant " + restaurantId + " rating: " + formattedRating + " from " + totalReviews + " reviews");
    }

    private String getStarRatingText(float rating) {
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

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, restaurantId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Reviews");
                            break;
                    }
                }).attach();
    }

    /**
     * Check if current restaurant is in user's favorites
     */
    private void checkIfFavorite() {
        if (currentUser == null) return;

        favoritesReference.child(String.valueOf(restaurantId))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isFavorite = snapshot.exists();
                        updateFavoriteIcon();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RestaurantDetailActivity.this,
                                "Failed to check favorite status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFavoriteIcon() {
        ivFavorite.setImageResource(
                isFavorite ? R.drawable.ic_favourite : R.drawable.ic_favorite_border
        );
    }

    private void setupClickListeners() {
        // Back button
        ivBack.setOnClickListener(v -> finish());

        // Favorite button
        ivFavorite.setOnClickListener(v -> {
            if (currentUser == null) {
                Toast.makeText(this, "Please login to add favorites", Toast.LENGTH_SHORT).show();
                return;
            }
            toggleFavorite();
        });

        // Directions button
        btnDirection.setOnClickListener(v -> {
            double[] coords = getRestaurantCoordinates(restaurantId);
            openDirections(coords[0], coords[1]);
        });

        // Call button
        btnCall.setOnClickListener(v -> {
            if (phoneNumberToCall != null) {
                makePhoneCall(phoneNumberToCall);
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        // View Menu button
        btnViewMenu.setOnClickListener(v -> {
            String url = getMenuUrl(restaurantId);
            if (url == null) {
                Toast.makeText(this, "Menu link not available", Toast.LENGTH_SHORT).show();
                return;
            }
            openWebPage(url);
        });

        // Write Review button
        btnWriteReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("restaurant_id", restaurantId);
            intent.putExtra("restaurant_name", restaurantName);
            startActivity(intent);
        });
    }

    private void toggleFavorite() {
        if (isFavorite) {
            // Remove from favorites
            favoritesReference.child(String.valueOf(restaurantId)).removeValue()
                    .addOnSuccessListener(unused -> {
                        isFavorite = false;
                        updateFavoriteIcon();
                        Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to remove from favorites",
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            String currentRatingText = tvRating.getText().toString();

            // Add to favorites
            Favorite fav = new Favorite(
                    restaurantId,
                    restaurantName,
                    currentRatingText + " ⭐", // Include star rating
                    tvLocation.getText().toString(),
                    tvHours.getText().toString(),
                    System.currentTimeMillis()
            );

            favoritesReference.child(String.valueOf(restaurantId)).setValue(fav)
                    .addOnSuccessListener(unused -> {
                        isFavorite = true;
                        updateFavoriteIcon();
                        Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add to favorites",
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Make a phone call
     */
    private void makePhoneCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    /**
     * Open web page
     */
    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    /**
     * Get restaurant coordinates based on ID
     */
    private double[] getRestaurantCoordinates(int id) {
        switch (id) {
            case 1: // Ayam Gepuk Jogja Shah Alam
                return new double[]{3.068499, 101.490867};
            case 2: // Ayam Gepuk Pak Gembus S7
                return new double[]{3.068467, 101.48954};
            case 3: // Ayam Gepuk Top Global S7
                return new double[]{3.066825, 101.484778};
            case 4: // Ayam Gepuk Pak Raden S7
                return new double[]{3.064984, 101.490241};
            case 5: // Ayam Gepuk Tok Mat S7
                return new double[]{3.064985, 101.489512};
            default:
                return new double[]{0, 0};
        }
    }

    /**
     * Open Google Maps with directions
     */
    private void openDirections(double lat, double lng) {
        String uri = String.format(Locale.ENGLISH,
                "http://maps.google.com/maps?daddr=%f,%f", lat, lng);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    /**
     * Get menu URL based on restaurant ID
     */
    private String getMenuUrl(int id) {
        switch (id) {
            case 1: // Jogja
                return "https://www.facebook.com/ayamgepukjogjahq/";
            case 2: // Pak Gembus
                return "https://ayamgepukpakgembus.com.my/";
            case 3: // Top Global
                return "https://ayamgepuktopglobal.my/";
            case 4: // Pak Raden
                return "https://www.facebook.com/AYAMGEPUKPAKRADENHQ/";
            case 5: // Tok Mat
                return "https://www.ayamgepuktokmat.com/";
            default:
                return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh reviews when returning to activity
        loadRestaurantReviews();
    }

    public static class Favorite {
        public int restaurantId;
        public String name;
        public String rating;
        public String location;
        public String hours;
        public long timestamp;

        public Favorite() {
        }

        public Favorite(int restaurantId, String name, String rating,
                        String location, String hours, long timestamp) {
            this.restaurantId = restaurantId;
            this.name = name;
            this.rating = rating;
            this.location = location;
            this.hours = hours;
            this.timestamp = timestamp;
        }
    }
}