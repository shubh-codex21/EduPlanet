package com.example.eduplanet_e_learningapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Fragments.DiscussFragment;
import com.example.eduplanet_e_learningapp.Fragments.DoubtsFragment;
import com.example.eduplanet_e_learningapp.Fragments.FeedFragment;
import com.example.eduplanet_e_learningapp.Fragments.MaterialFragment;
import com.example.eduplanet_e_learningapp.Fragments.QuizFragment;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private ActionBarDrawerToggle toggle;
    View headerView;
    TextView email, name;
    CircleImageView image;
    ImageView cover;
    RelativeLayout header;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.open, R.string.close);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                binding.drawer.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.my_profile:
                        startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                        break;
                    case R.id.my_performance:
                        Toast.makeText(MainActivity.this, "My Performance", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.invite_friends:
                        Toast.makeText(MainActivity.this, "Invite Friends", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.wishlist:
                        Toast.makeText(MainActivity.this, "Wishlist", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.notes:
                        Toast.makeText(MainActivity.this, "Notes", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.downloads:
                        Toast.makeText(MainActivity.this, "Downloads", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.terms_and_conditions:
                        Toast.makeText(MainActivity.this, "Terms and Conditions", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.privacy_policy:
                        Toast.makeText(MainActivity.this, "Privacy Policy", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.feedback:
                        Toast.makeText(MainActivity.this, "Feedback", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.contact_us:
                        Toast.makeText(MainActivity.this, "Contact us", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.logout:
                        auth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });

        headerView = binding.navigation.getHeaderView(0);
        email = headerView.findViewById(R.id.userEmail);
        name = headerView.findViewById(R.id.userName);
        image = headerView.findViewById(R.id.userImage);
        cover = headerView.findViewById(R.id.cover);
        header = headerView.findViewById(R.id.header);

        firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value.exists()) {
                    if (value.getString("userImage") != null) {
                        Glide.with(getApplicationContext()).load(value.get("userImage")).into(image);
                    } else {
                        Glide.with(getApplicationContext()).load(R.drawable.avatar2).into(image);
                    }

                    if (value.getString("userCover") != null) {
                        Glide.with(getApplicationContext()).load(value.get("userCover")).into(cover);
                    } else {
                        Glide.with(getApplicationContext()).load(R.color.blue1).into(cover);
                    }

                    name.setText(value.getString("username"));
                    email.setText(value.getString("userEmail"));

                }
            }
        });

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            }
        });

        binding.pageTitle.setText("Material");
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MaterialFragment()).commit();

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.material:
                        binding.pageTitle.setText("Material");
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MaterialFragment()).commit();
                        break;

                    case R.id.feed:
                        binding.pageTitle.setText("Feed");
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FeedFragment()).commit();
                        break;

                    case R.id.discuss:
                        binding.pageTitle.setText("Discuss");
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new DiscussFragment()).commit();
                        break;

                    case R.id.doubts:
                        binding.pageTitle.setText("Ask Doubts");
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new DoubtsFragment()).commit();
                        break;

                    case R.id.quiz:
                        binding.pageTitle.setText("Quiz");
                        binding.search.setVisibility(View.GONE);
                        binding.messages.setVisibility(View.GONE);
                        binding.pointsCard.setVisibility(View.VISIBLE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new QuizFragment()).commit();
                        break;

                }
                return true;
            }
        });

        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}