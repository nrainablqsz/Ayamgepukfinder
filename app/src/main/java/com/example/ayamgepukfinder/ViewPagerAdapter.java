package com.example.ayamgepukfinder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final int restaurantId;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int restaurantId) {
        super(fragmentActivity);
        this.restaurantId = restaurantId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ReviewsFragment.newInstance(restaurantId);
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}