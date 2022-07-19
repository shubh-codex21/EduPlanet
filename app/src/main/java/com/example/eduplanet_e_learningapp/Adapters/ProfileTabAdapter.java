package com.example.eduplanet_e_learningapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eduplanet_e_learningapp.Fragments.UserDoubtFragment;
import com.example.eduplanet_e_learningapp.Fragments.UserFeedFragment;

import org.jetbrains.annotations.NotNull;

public class ProfileTabAdapter extends FragmentStateAdapter {

    public ProfileTabAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new UserDoubtFragment();
        }
        return new UserFeedFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
