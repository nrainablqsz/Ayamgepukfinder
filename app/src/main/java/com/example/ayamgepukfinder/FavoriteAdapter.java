package com.example.ayamgepukfinder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<Favorite> favoriteList;
    private Context context;

    public FavoriteAdapter(List<Favorite> favoriteList, Context context) {
        this.favoriteList = favoriteList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);

        holder.tvRestaurantName.setText(favorite.getName());
        holder.tvRating.setText(favorite.getRating());

        // Use consistent location from restaurant ID
        holder.tvLocation.setText(getRestaurantLocation(favorite.getRestaurantId()));

        // Use consistent hours
        holder.tvHours.setText("11:00 AM - 9:30 PM");

        // Set restaurant image based on ID
        int imageResource = getRestaurantImage(favorite.getRestaurantId());
        Glide.with(context)
                .load(imageResource)
                .placeholder(R.drawable.placeholder_restaurant)
                .into(holder.ivRestaurant);

        // Click listener
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RestaurantDetailActivity.class);
            intent.putExtra("restaurant_id", favorite.getRestaurantId());
            intent.putExtra("restaurant_name", favorite.getName());
            context.startActivity(intent);
        });
    }

    // Method to get consistent location
    private String getRestaurantLocation(int restaurantId) {
        switch (restaurantId) {
            case 1: // Ayam Gepuk Jogja Shah Alam
                return "15, Jalan Plumbum S7/S, Seksyen 7, Shah Alam";
            case 2: // Ayam Gepuk Pak Gembus
                return "Shah Alam S7, 1, Jalan Plumbum Q7/Q";
            case 3: // Ayam Gepuk Top Global
                return "S7 Shah Alam, 24-G, Jalan Plumbum N 7/N";
            case 4: // Ayam Gepuk Pak Raden
                return "Blok K, Pusat Komersial, Seksyen 7";
            case 5: // Ayam Gepuk Tok Mat
                return "XG 23, Blok J, Jalan Plumbum X 7/X";
            default:
                return "Shah Alam, Selangor";
        }
    }

    private int getRestaurantImage(int restaurantId) {
        switch (restaurantId) {
            case 1: return R.drawable.jogja;
            case 2: return R.drawable.pakgembus;
            case 3: return R.drawable.topglobal;
            case 4: return R.drawable.pakraden;
            case 5: return R.drawable.tokmat;
            default: return R.drawable.placeholder_restaurant;
        }
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivRestaurant;
        TextView tvRestaurantName, tvRating, tvLocation, tvHours;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            ivRestaurant = itemView.findViewById(R.id.ivRestaurant);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvHours = itemView.findViewById(R.id.tvHours);
        }
    }

}