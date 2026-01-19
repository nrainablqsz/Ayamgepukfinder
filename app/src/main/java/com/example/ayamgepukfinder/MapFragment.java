package com.example.ayamgepukfinder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    // UI Components
    private GoogleMap mMap;
    private EditText etSearch;
    private Button btnSearch, btnCurrentLocation, btnViewAllRestaurants;
    private ProgressBar progressBar;
    private TextView tvError;

    // Location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final float DEFAULT_ZOOM = 15f;
    private LatLng currentLatLng;

    private final List<Restaurant> restaurantList = new ArrayList<>();

    private static class Restaurant {
        String name;
        LatLng location;
        float rating;
        String address;

        Restaurant(String name, LatLng location, float rating, String address) {
            this.name = name;
            this.location = location;
            this.rating = rating;
            this.address = address;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initializeRestaurants();
        initViews(view);
        initMap();
        initLocationClient();
        setupListeners();

        return view;
    }

    private void initializeRestaurants() {
        restaurantList.clear();

        // Case 1: Ayam Gepuk Jogja Shah Alam
        restaurantList.add(new Restaurant(
                "Ayam Gepuk Jogja Shah Alam",
                new LatLng(3.068499, 101.490867),
                4.5f,
                "15, Jalan Plumbum S7/S, Seksyen 7, 40000 Shah Alam"
        ));

        // Case 2: Ayam Gepuk Pak Gembus S7
        restaurantList.add(new Restaurant(
                "Ayam Gepuk Pak Gembus S7",
                new LatLng(3.068467, 101.48954),
                4.6f,
                "1, Jalan Plumbum Q7/Q, 40000 Shah Alam"
        ));

        // Case 3: Ayam Gepuk Top Global S7
        restaurantList.add(new Restaurant(
                "Ayam Gepuk Top Global S7",
                new LatLng(3.066825, 101.484778),
                4.4f,
                "24-G, Jalan Plumbum N 7/N, 40000 Shah Alam"
        ));

        // Case 4: Ayam Gepuk Pak Raden S7
        restaurantList.add(new Restaurant(
                "Ayam Gepuk Pak Raden S7",
                new LatLng(3.064984, 101.490241),
                4.3f,
                "BLOK K, Pusat Komersial Seksyen 7, 40000 Shah Alam"
        ));

        // Case 5: Ayam Gepuk Tok Mat S7
        restaurantList.add(new Restaurant(
                "Ayam Gepuk Tok Mat S7",
                new LatLng(3.064985, 101.489512),
                4.7f,
                "XG 23, BLOK J, Jalan Plumbum X 7/X, Seksyen 7, Shah Alam"
        ));
    }


    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation);
        btnViewAllRestaurants = view.findViewById(R.id.btnViewAllRestaurants);
        progressBar = view.findViewById(R.id.progressBar);
        tvError = view.findViewById(R.id.tvError);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            showLoading(true);
            mapFragment.getMapAsync(this);
        } else {
            tvError.setText("Map fragment not found");
            tvError.setVisibility(View.VISIBLE);
        }
    }

    private void initLocationClient() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(v -> searchLocation());
        btnCurrentLocation.setOnClickListener(v -> getCurrentLocation());
        btnViewAllRestaurants.setOnClickListener(v -> showAllRestaurants());

        // Search on Enter key
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            searchLocation();
            return true;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        showLoading(false);

        // Configure map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Add restaurant markers
        addRestaurantMarkers();

        // Move to default location (Shah Alam)
        LatLng defaultLocation = new LatLng(3.0733, 101.5185);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));

        // Check location permission
        checkLocationPermission();

        // Set marker click listener
        mMap.setOnMarkerClickListener(marker -> {
            String title = marker.getTitle();
            if (title != null && !title.equals("You are here")) {
                Toast.makeText(getContext(), "Selected: " + title, Toast.LENGTH_SHORT).show();
                // You can open restaurant details here
            }
            return false;
        });
    }

    private void addRestaurantMarkers() {
        if (mMap == null) return;

        mMap.clear(); // Clear existing markers

        for (Restaurant restaurant : restaurantList) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(restaurant.location)
                    .title(restaurant.name)
                    .snippet("Rating: " + restaurant.rating + " ★ | " + restaurant.address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            // Store restaurant data in marker tag
            if (marker != null) {
                marker.setTag(restaurant);
            }
        }
    }

    private void searchLocation() {
        String locationName = etSearch.getText().toString().trim();

        if (locationName.isEmpty()) {
            etSearch.setError("Please enter a location");
            etSearch.requestFocus();
            return;
        }

        showLoading(true);

        // Use Geocoder to convert address to coordinates
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                // Add marker for searched location
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(locationName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                // Move camera to searched location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                // Show address
                String fullAddress = address.getAddressLine(0);
                Toast.makeText(getContext(), "Found: " + fullAddress, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error searching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            showLoading(false);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (!hasLocationPermission()) {
            requestLocationPermission();
            return;
        }

        showLoading(true);

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            showLoading(false);

            if (location != null) {
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Clear previous "You are here" markers
                mMap.clear();

                // Add restaurant markers back
                addRestaurantMarkers();

                // Add current location marker
                mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                // Move camera to current location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));

                // Show nearest ayam gepuk
                findNearestRestaurant();

            } else {
                Toast.makeText(getContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(e -> {
            showLoading(false);
            Toast.makeText(getContext(), "Location error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void findNearestRestaurant() {
        if (currentLatLng == null || restaurantList.isEmpty()) return;

        Restaurant nearest = null;
        float minDistance = Float.MAX_VALUE;

        for (Restaurant restaurant : restaurantList) {
            float[] results = new float[1];
            Location.distanceBetween(
                    currentLatLng.latitude, currentLatLng.longitude,
                    restaurant.location.latitude, restaurant.location.longitude,
                    results
            );

            float distance = results[0] / 1000; // Convert to kilometers
            if (distance < minDistance) {
                minDistance = distance;
                nearest = restaurant;
            }
        }

        if (nearest != null) {
            String message = String.format(Locale.getDefault(),
                    "Nearest ayam gepuk: %s (%.1f km away)",
                    nearest.name, minDistance);
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void showAllRestaurants() {
        if (restaurantList.isEmpty() || mMap == null) return;

        // Calculate bounds to include all restaurants
        com.google.android.gms.maps.model.LatLngBounds.Builder builder = new com.google.android.gms.maps.model.LatLngBounds.Builder();

        for (Restaurant restaurant : restaurantList) {
            builder.include(restaurant.location);
        }

        try {
            // Add padding around bounds
            int padding = 100; // pixels
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), padding));

            Toast.makeText(getContext(),
                    "Showing " + restaurantList.size() + " ayam gepuk restaurants",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Fallback to first restaurant if bounds fail
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    restaurantList.get(0).location, 12f));
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkLocationPermission() {
        if (hasLocationPermission() && mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void requestLocationPermission() {
        requestPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
                getCurrentLocation();
            } else {
                Toast.makeText(getContext(),
                        "Location permission denied. Some features may not work.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up
        if (mMap != null) {
            mMap.clear();
        }
    }
}