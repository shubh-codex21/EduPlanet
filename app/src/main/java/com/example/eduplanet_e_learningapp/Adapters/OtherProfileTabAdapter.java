package com.example.eduplanet_e_learningapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eduplanet_e_learningapp.Fragments.OtherProfileDoubtFragment;
import com.example.eduplanet_e_learningapp.Fragments.OtherProfileFeedFragment;
import com.example.eduplanet_e_learningapp.Fragments.UserDoubtFragment;
import com.example.eduplanet_e_learningapp.Fragments.UserFeedFragment;

import org.jetbrains.annotations.NotNull;

public class OtherProfileTabAdapter extends FragmentStateAdapter {
    String profileId;

    public OtherProfileTabAdapter(@NonNull @NotNull FragmentManager fragmentManager, @NonNull @NotNull Lifecycle lifecycle,
                                  String profileId) {
        super(fragmentManager, lifecycle);
        this.profileId = profileId;
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new OtherProfileDoubtFragment(profileId);
        }
        return new OtherProfileFeedFragment(profileId);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
