package com.example.ayamgepukfinder;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        initializeFirebase();

        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ayam Gepuk Finder");
        }

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        // Show welcome notification when app starts
        showWelcomeNotification();
    }

    private void initializeFirebase() {
        try {
            // Initialize Firebase App if not already initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
                Toast.makeText(this, "Firebase initialized", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Firebase already initialized", Toast.LENGTH_SHORT).show();
            }

            // Enable Firebase Realtime Database persistence
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            // Optional: Configure database settings
            FirebaseDatabase.getInstance().setLogLevel(com.google.firebase.database.Logger.Level.DEBUG);

        } catch (Exception e) {
            Toast.makeText(this, "Firebase initialization failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showWelcomeNotification() {
        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Request permission but don't force it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
                return; // Don't show notification until permission granted
            }
        }

        // Create and show welcome notification
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.showWelcomeNotification();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show welcome notification
                NotificationHelper notificationHelper = new NotificationHelper(this);
                notificationHelper.showWelcomeNotification();
            } else {
                // Permission denied
                Toast.makeText(this,
                        "Notification permission denied. You can enable it in app settings.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final NavigationBarView.OnItemSelectedListener navListener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Ayam Gepuk Finder");
                        }
                    } else if (itemId == R.id.nav_favorite) {
                        selectedFragment = new FavoriteFragment();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Favorites");
                        }
                    } else if (itemId == R.id.nav_map) {
                        selectedFragment = new MapFragment();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Find Ayam Gepuk");
                        }
                    } else if (itemId == R.id.nav_profile) {
                        selectedFragment = new ProfileFragment();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Profile");
                        }
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}