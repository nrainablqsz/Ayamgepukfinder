package com.example.ayamgepukfinder;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {

    private TextView tvRestaurantName;
    private RatingBar ratingBar;
    private EditText etComment;
    private ImageView ivReviewPhoto;
    private Button btnSubmit, btnTakePhoto, btnChoosePhoto, btnDeletePhoto, btnBack;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference reviewsRef;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private String currentPhotoPath;

    private int restaurantId;
    private String restaurantName;

    private static final String TAG = "ReviewActivity";
    private static final String CHANNEL_ID = "review_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize Firebase FIRST
        initFirebase();

        // RECEIVE INT SAFELY
        restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        restaurantName = getIntent().getStringExtra("restaurant_name");

        if (restaurantId == -1 || restaurantName == null) {
            Toast.makeText(this, "Restaurant data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        createNotificationChannel();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize FirebaseDatabase with error handling
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            reviewsRef = database.getReference("reviews");

            if (reviewsRef == null) {
                Log.e(TAG, "Firebase DatabaseReference is null!");
            } else {
                Log.d(TAG, "Firebase DatabaseReference initialized successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase: " + e.getMessage());
            Toast.makeText(this, "Firebase error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Log Firebase initialization
        Log.d(TAG, "Firebase initialized");
        Log.d(TAG, "Current user: " + (currentUser != null ? currentUser.getEmail() : "null"));
        Log.d(TAG, "Restaurant ID: " + restaurantId);
        Log.d(TAG, "Restaurant Name: " + restaurantName);
    }

    private void initViews() {
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        ivReviewPhoto = findViewById(R.id.ivReviewPhoto);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnDeletePhoto = findViewById(R.id.btnDeletePhoto);
        btnBack = findViewById(R.id.btnBack);

        tvRestaurantName.setText("Review for " + restaurantName);
        btnDeletePhoto.setVisibility(View.GONE);
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> submitReview());

        btnTakePhoto.setOnClickListener(v -> takePhoto());

        btnChoosePhoto.setOnClickListener(v -> choosePhoto());

        btnDeletePhoto.setOnClickListener(v -> {
            ivReviewPhoto.setImageResource(R.drawable.ic_add_photo);
            btnDeletePhoto.setVisibility(View.GONE);
            currentPhotoPath = null;
        });
    }

    private void submitReview() {

        if (currentUser == null) {
            Toast.makeText(this, "Login required", Toast.LENGTH_SHORT).show();
            return;
        }

        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (rating == 0 || TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if reviewsRef is null before using it
        if (reviewsRef == null) {
            Log.e(TAG, "reviewsRef is null! Trying to reinitialize...");
            initFirebase();

            if (reviewsRef == null) {
                Toast.makeText(this, "Database error. Please restart the app.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String reviewId = reviewsRef.push().getKey();

        // Check if reviewId is null
        if (reviewId == null) {
            Toast.makeText(this, "Failed to generate review ID. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String userEmail = currentUser.getEmail();
        String userName = userEmail;

        String photoBase64 = convertImageToBase64();

        // Log what we're saving
        Log.d(TAG, "Submitting review for restaurant ID: " + restaurantId);
        Log.d(TAG, "Review ID: " + reviewId);
        Log.d(TAG, "Firebase path will be: reviews/" + restaurantId + "/" + reviewId);

        Review review = new Review(
                reviewId,
                userId,
                userName,
                userEmail,
                String.valueOf(restaurantId),
                restaurantName,
                rating,
                comment,
                photoBase64,
                String.valueOf(System.currentTimeMillis()),
                new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date())
        );

        // Save under restaurant-specific path
        reviewsRef.child(String.valueOf(restaurantId))
                .child(reviewId)
                .setValue(review)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Review saved successfully at: reviews/" +
                            restaurantId + "/" + reviewId);
                    showNotification();
                    Toast.makeText(this, "Review submitted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save review: " + e.getMessage());
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String convertImageToBase64() {
        try {
            BitmapDrawable drawable = (BitmapDrawable) ivReviewPhoto.getDrawable();
            if (drawable == null) return null;

            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            return android.util.Base64.encodeToString(baos.toByteArray(),
                    android.util.Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_GALLERY) {
                ivReviewPhoto.setImageURI(data.getData());
                btnDeletePhoto.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ivReviewPhoto.setImageBitmap(photo);
                btnDeletePhoto.setVisibility(View.VISIBLE);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID,
                            "Review Notifications",
                            NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Review Submitted")
                        .setContentText("Thank you for reviewing " + restaurantName)
                        .setAutoCancel(true);

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Migration method - can call this from a button
    private void migrateOldReviews() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("reviews");

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Starting migration of old reviews");
                int migratedCount = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.hasChild("restaurantId")) {
                        Review review = ds.getValue(Review.class);
                        if (review != null) {
                            String newPath = "reviews/" + review.getRestaurantId() + "/" + ds.getKey();
                            FirebaseDatabase.getInstance().getReference(newPath).setValue(review);

                            // Remove from old location
                            ds.getRef().removeValue();
                            migratedCount++;
                            Log.d(TAG, "Migrated review: " + ds.getKey() + " to restaurant " + review.getRestaurantId());
                        }
                    }
                }

                Log.d(TAG, "Migration complete. Migrated " + migratedCount + " reviews");
                Toast.makeText(ReviewActivity.this,
                        "Migrated " + migratedCount + " reviews", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Migration failed: " + error.getMessage());
            }
        });
    }
}