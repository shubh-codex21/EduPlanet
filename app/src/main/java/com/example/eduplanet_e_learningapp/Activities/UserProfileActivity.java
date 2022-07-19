package com.example.eduplanet_e_learningapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Adapters.ProfileTabAdapter;
import com.example.eduplanet_e_learningapp.OtherClasses.MethodsClass;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.OtherClasses.ProfileBsDialog;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityUserProfileBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

public class UserProfileActivity extends AppCompatActivity {
    ProfileTabAdapter adapter;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ActivityUserProfileBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

//         Fetching User Information --start
            firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                    if (value.exists()){
                        User user = value.toObject(User.class);
                        if (user.getUserCover()!=null) {
                            Glide.with(getApplicationContext()).load(user.getUserCover()).into(binding.cover);
                        } else {
                            Glide.with(getApplicationContext()).load(R.color.blue1).into(binding.cover);
                        }
                        if (user.getUserImage()!=null) {
                            Glide.with(getApplicationContext()).load(user.getUserImage()).into(binding.profileImage);
                        } else {
                            Glide.with(getApplicationContext()).load(R.drawable.avatar2).into(binding.profileImage);
                        }
                        binding.userName.setText(user.getUsername());
                        binding.email.setText(user.getUserEmail());
                        String Class = user.getUserClass().substring(6);
                        binding.userClass.setText(Class);

                        if (user.getUserBio() != null && !TextUtils.isEmpty(user.getUserBio())){
                            binding.userBio.setVisibility(View.VISIBLE);
                            binding.userBio.setText(user.getUserBio());
                        } else {
                            binding.userBio.setVisibility(View.GONE);
                        }

                        String followers = MethodsClass.shortenNumber(user.getFollowers());
                        String following = MethodsClass.shortenNumber(user.getFollowing());
                        String posts = MethodsClass.shortenNumber(user.getNoOfPost());
                        String doubts = MethodsClass.shortenNumber(user.getNoOfDoubts());
                        binding.followers.setText(followers+"");
                        binding.following.setText(following+"");
                        binding.posts.setText(posts+"");
                        binding.doubt.setText(doubts+"");

                        if (user.getLocation() != null && !TextUtils.isEmpty(user.getLocation())){
                            binding.location.setText(user.getLocation());
                            binding.location.setVisibility(View.VISIBLE);
                        } else {
                            binding.location.setVisibility(View.GONE);
                        }
                        if (user.getDateOfBirth() != null && !TextUtils.isEmpty(user.getDateOfBirth())){
                            binding.dateOfBirth.setText(user.getDateOfBirth());
                            binding.dateOfBirth.setVisibility(View.VISIBLE);
                        } else {
                            binding.dateOfBirth.setVisibility(View.GONE);
                        }
                        if (user.getLink() != null && !TextUtils.isEmpty(user.getLink())){
                            binding.link.setText(user.getLink());
                            binding.link.setVisibility(View.VISIBLE);
                        } else {
                            binding.link.setVisibility(View.GONE);
                        }

                    }
                }
            });
//         Fetching User Information --end

//         Implementing tab layout --start
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
        // Implementing tab layout --end

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this, EditProfileActivity.class));
            }
        });

    }
}
