package com.linktothe.ps5payloadsender;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PrimaryPayloadsFragment();
            case 1:
                return new SecondaryPayloadsFragment();
            default:
                return new PrimaryPayloadsFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 2; // We have 2 tabs
    }
}
