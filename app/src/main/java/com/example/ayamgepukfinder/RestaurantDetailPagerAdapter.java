package com.example.ayamgepukfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantDetailPagerAdapter
        extends RecyclerView.Adapter<RestaurantDetailPagerAdapter.ViewHolder> {

    private final Context context;
    private final int restaurantId;

    public RestaurantDetailPagerAdapter(Context context, int restaurantId) {
        this.context = context;
        this.restaurantId = restaurantId;
    }

    @Override
    public int getItemCount() {
        return 1; // Only reviews
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tab_container, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Check if fragment already exists to avoid recreation
        Fragment existingFragment = ((AppCompatActivity) context)
                .getSupportFragmentManager()
                .findFragmentById(holder.fragmentContainer.getId());

        if (existingFragment == null) {
            Fragment fragment = ReviewsFragment.newInstance(restaurantId);

            ((AppCompatActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(holder.fragmentContainer.getId(), fragment)
                    .commit();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View fragmentContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            fragmentContainer = itemView.findViewById(R.id.fragmentContainer);
        }
    }
}