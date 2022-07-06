package com.example.eduplanet_e_learningapp.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.databinding.ActivityShowAttachImgBinding;

public class ShowAttachImgActivity extends AppCompatActivity {
    String imageUrl;

    ActivityShowAttachImgBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAttachImgBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(ShowAttachImgActivity.this).load(imageUrl).into(binding.attachmentImage);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}