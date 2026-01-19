package com.example.ayamgepukfinder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class FavoriteFragment extends Fragment {

    private static final String TAG = "FavoriteFragment";

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private FavoriteAdapter adapter;
    private List<Favorite> favoriteList;

    private DatabaseReference favoritesReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Add ValueEventListener as separate variable
    private ValueEventListener favoritesListener;

    public FavoriteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Return the inflated view
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated called");

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        // Check if views are properly initialized
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView is null! Check XML layout");
        }
        if (tvEmpty == null) {
            Log.e(TAG, "tvEmpty TextView is null! Check XML layout");
        }

        // Setup RecyclerView
        if (recyclerView != null) {
            // Use requireContext() for safe context access
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            favoriteList = new ArrayList<>();
            adapter = new FavoriteAdapter(favoriteList, requireContext()); // ✅ FIXED
            recyclerView.setAdapter(adapter);
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            if (tvEmpty != null) {
                tvEmpty.setText("Please login to view favorites");
                tvEmpty.setVisibility(View.VISIBLE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        favoritesReference = database.getReference("favorites")
                .child(currentUser.getUid());

        // Load favorites
        loadFavorites();
    }

    private void loadFavorites() {
        // Remove existing listener first
        removeFavoritesListener();

        favoritesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange called, snapshot exists: " + dataSnapshot.exists());

                if (favoriteList == null) {
                    favoriteList = new ArrayList<>();
                }
                favoriteList.clear();

                if (dataSnapshot.exists()) {
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Favorite favorite = snapshot.getValue(Favorite.class);
                        if (favorite != null) {
                            // If hours are empty or wrong, set default hours
                            if (favorite.getHours() == null || favorite.getHours().isEmpty()) {
                                favorite.setHours(getDefaultHours(favorite.getRestaurantId()));
                            }
                            favoriteList.add(favorite);
                            count++;
                            Log.d(TAG, "Added favorite: " + favorite.getName() + " (ID: " + favorite.getRestaurantId() + ")");
                        }
                    }

                    Log.d(TAG, "Total favorites loaded: " + count);

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                    // Show/hide empty state
                    updateEmptyState();
                } else {
                    Log.d(TAG, "No favorites found in database");
                    updateEmptyState();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load favorites: " + databaseError.getMessage());

                // Use requireActivity() for Toast context
                if (isAdded() && getActivity() != null) {
                    String errorMsg = "Failed to load favorites: " + databaseError.getMessage();
                    if (tvEmpty != null) {
                        tvEmpty.setText(errorMsg);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(requireActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }

                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        };

        // ✅ FIX: Attach the listener
        if (favoritesReference != null && favoritesListener != null) {
            favoritesReference.addValueEventListener(favoritesListener);
            Log.d(TAG, "Firebase listener attached");
        }
    }

    // ✅ FIX: Helper method to update empty state
    private void updateEmptyState() {
        if (isAdded() && getActivity() != null) {
            requireActivity().runOnUiThread(() -> {
                if (favoriteList == null || favoriteList.isEmpty()) {
                    if (tvEmpty != null) {
                        tvEmpty.setText("No favorites yet. Start adding your favorite restaurants!");
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                    if (recyclerView != null) {
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    if (tvEmpty != null) {
                        tvEmpty.setVisibility(View.GONE);
                    }
                    if (recyclerView != null) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    // 🔥 Helper method to get default hours based on restaurant ID
    private String getDefaultHours(int restaurantId) {
        return "11:00 AM - 9:30 PM"; // All restaurants have same hours
    }

    // ✅ FIX: Method to safely remove listener
    private void removeFavoritesListener() {
        if (favoritesReference != null && favoritesListener != null) {
            favoritesReference.removeEventListener(favoritesListener);
            Log.d(TAG, "Firebase listener removed");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ✅ FIX: Remove listener when fragment view is destroyed
        removeFavoritesListener();

        // Clean up
        if (favoriteList != null) {
            favoriteList.clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // ✅ FIX: Final cleanup
        removeFavoritesListener();
    }

    // Add this method to FavoriteFragment.java (optional - for fixing existing data)
    private void updateExistingFavorites() {
        if (favoritesReference == null || currentUser == null) return;

        favoritesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int updatedCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Favorite favorite = snapshot.getValue(Favorite.class);
                    if (favorite != null) {
                        // Update hours for all favorites
                        favorite.setHours("11:00 AM - 9:30 PM");
                        snapshot.getRef().setValue(favorite);
                        updatedCount++;
                    }
                }
                Log.d(TAG, "Updated " + updatedCount + " favorites with default hours");

                if (isAdded() && getActivity() != null) {
                    Toast.makeText(requireActivity(),
                            "Updated " + updatedCount + " favorites",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to update favorites: " + databaseError.getMessage());
            }
        });
    }
}