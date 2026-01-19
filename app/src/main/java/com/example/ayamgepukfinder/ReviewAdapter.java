package com.example.ayamgepukfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;
    private boolean showRestaurantName = true;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    public ReviewAdapter(Context context, List<Review> reviewList, boolean showRestaurantName) {
        this.context = context;
        this.reviewList = reviewList;
        this.showRestaurantName = showRestaurantName;
    }

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
        this.showRestaurantName = false;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        if (showRestaurantName) {
            holder.tvRestaurantName.setText(review.getRestaurantName());
            holder.tvRestaurantName.setVisibility(View.VISIBLE);
        } else {
            holder.tvRestaurantName.setVisibility(View.GONE);
        }

        holder.tvComment.setText(review.getComment());
        holder.tvDate.setText(review.getDate());
        holder.tvUserName.setText(review.getUserName());
        holder.ratingBar.setRating(review.getRating());

        if (review.getPhotoData() != null && !review.getPhotoData().isEmpty()) {
            try {
                byte[] decodedString = android.util.Base64.decode(
                        review.getPhotoData(), android.util.Base64.DEFAULT);

                Glide.with(holder.itemView.getContext())
                        .load(decodedString)
                        .placeholder(R.drawable.ic_add_photo)
                        .into(holder.ivReviewPhoto);

                holder.ivReviewPhoto.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                holder.ivReviewPhoto.setVisibility(View.GONE);
            }
        } else {
            holder.ivReviewPhoto.setVisibility(View.GONE);
        }

        // Disable edit/delete if no context (public display)
        if (context != null) {
            holder.itemView.setOnClickListener(v -> showEditDialog(review));
            holder.itemView.setOnLongClickListener(v -> {
                showDeleteDialog(review);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    private void showEditDialog(Review review) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Edit Review")
                .setMessage("Edit functionality coming soon!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showDeleteDialog(Review review) {
        new android.app.AlertDialog.Builder(context)
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete", (dialog, which) -> deleteReview(review))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReview(Review review) {
        if (review.getReviewId() == null || review.getReviewId().isEmpty()) {
            android.widget.Toast.makeText(context,
                    "Cannot delete: Review ID missing",
                    android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        com.google.firebase.database.FirebaseDatabase.getInstance()
                .getReference("reviews")
                .child(review.getRestaurantId())
                .child(review.getReviewId())
                .removeValue();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRestaurantName, tvComment, tvDate, tvUserName;
        RatingBar ratingBar;
        ImageView ivReviewPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ivReviewPhoto = itemView.findViewById(R.id.ivReviewPhoto);
        }
    }
}
