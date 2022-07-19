package com.example.eduplanet_e_learningapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Adapters.OtherProfileTabAdapter;
import com.example.eduplanet_e_learningapp.Adapters.ProfileTabAdapter;
import com.example.eduplanet_e_learningapp.Modals.Follow;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.OtherClasses.MethodsClass;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityShowingProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;

public class ShowingProfileActivity extends AppCompatActivity {
    OtherProfileTabAdapter adapter;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;

    String profileId;

    ActivityShowingProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowingProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        profileId = getIntent().getStringExtra("profileId");

//         Fetching User Information --start
        firestore.collection("User").document(profileId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value.exists()) {
                    User user = value.toObject(User.class);
                    Glide.with(getApplicationContext()).load(user.getUserImage()).into(binding.profileImage);
                    binding.userName.setText(user.getUsername());
                    String Class = user.getUserClass().substring(6);
                    binding.userClass.setText(Class);

                    if (user.getUserBio() != null && !TextUtils.isEmpty(user.getUserBio())) {
                        binding.userBio.setVisibility(View.VISIBLE);
                        binding.userBio.setText(user.getUserBio());
                    } else {
                        binding.userBio.setVisibility(View.GONE);
                    }

                    String followers = MethodsClass.shortenNumber(user.getFollowers());
                    String following = MethodsClass.shortenNumber(user.getFollowing());
                    String posts = MethodsClass.shortenNumber(user.getNoOfPost());
                    String doubts = MethodsClass.shortenNumber(user.getNoOfDoubts());
                    binding.followers.setText(followers + "");
                    binding.following.setText(following + "");
                    binding.posts.setText(posts + "");
                    binding.doubt.setText(doubts + "");

                    if (user.getLocation() != null && !TextUtils.isEmpty(user.getLocation())) {
                        binding.location.setText(user.getLocation());
                        binding.location.setVisibility(View.VISIBLE);
                    } else {
                        binding.location.setVisibility(View.GONE);
                    }
                    if (user.getDateOfBirth() != null && !TextUtils.isEmpty(user.getDateOfBirth())) {
                        binding.dateOfBirth.setText(user.getDateOfBirth());
                        binding.dateOfBirth.setVisibility(View.VISIBLE);
                    } else {
                        binding.dateOfBirth.setVisibility(View.GONE);
                    }
                    if (user.getLink() != null && !TextUtils.isEmpty(user.getLink())) {
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
        adapter = new OtherProfileTabAdapter(fragmentManager, getLifecycle(), profileId);
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

        firestore.collection("User").document(profileId).collection("followers")
                .document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    binding.followBtn.setCardBackgroundColor(getResources().getColorStateList(R.color.dark_grey));
                    binding.followTv.setText("Following");
                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("TAG", e.getMessage());
            }
        });

        binding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("User").document(profileId).collection("followers")
                        .document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            firestore.collection("User").document(profileId).collection("followers")
                                    .document(auth.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("followers", FieldValue.increment(-1));
                                    firestore.collection("User").document(profileId).update(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("following", FieldValue.increment(-1));
                                                    firestore.collection("User").document(auth.getUid()).update(hashMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    binding.followBtn.setCardBackgroundColor(getResources().getColorStateList(R.color.blue1));
                                                                    binding.followTv.setText("Follow");
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull @NotNull Exception e) {
                                                            Log.d("TAG", e.getMessage());
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Log.d("TAG", e.getMessage());
                                        }
                                    });
                                }
                            });

                        } else {
                            Follow follow = new Follow(auth.getUid(), new Date().getTime());
                            firestore.collection("User").document(profileId)
                                    .collection("followers")
                                    .document(auth.getUid())
                                    .set(follow)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("followers", FieldValue.increment(1));
                                            firestore.collection("User").document(profileId).update(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                            hashMap.put("following", FieldValue.increment(1));
                                                            firestore.collection("User").document(auth.getUid()).update(hashMap)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            binding.followBtn.setCardBackgroundColor(getResources().getColorStateList(R.color.dark_grey));
                                                                            binding.followTv.setText("Following");
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                                    Log.d("TAG", e.getMessage());
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    Log.d("TAG", e.getMessage());
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Log.d("TAG", e.getMessage());
                                }
                            });
                        }

                    }

                });
            }
        });

    }
}