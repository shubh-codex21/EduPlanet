package com.example.eduplanet_e_learningapp.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eduplanet_e_learningapp.Adapters.ProfileTabAdapter;
import com.example.eduplanet_e_learningapp.databinding.ActivityUserProfileBinding;
import com.google.android.material.tabs.TabLayout;

public class UserProfileActivity extends AppCompatActivity {
    ProfileTabAdapter adapter;

    ActivityUserProfileBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Feed"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Doubts"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new ProfileTabAdapter(fragmentManager,getLifecycle());
        binding.viewPager.setAdapter(adapter);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

    }
}
