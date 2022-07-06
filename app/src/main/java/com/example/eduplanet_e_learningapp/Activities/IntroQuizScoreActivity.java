package com.example.eduplanet_e_learningapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eduplanet_e_learningapp.databinding.ActivityIntroQuizScoreBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class IntroQuizScoreActivity extends AppCompatActivity {
    int ScoreNoOfQuestions, ScoreCorrectAnswers;
    ArrayList<String> arrayList;
    Random random;
    String Rank;
    int index;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ActivityIntroQuizScoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroQuizScoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        arrayList = new ArrayList<>();
        random = new Random();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ScoreNoOfQuestions = getIntent().getIntExtra("noOfQuestions", 0);
        ScoreCorrectAnswers = getIntent().getIntExtra("correctAnswers", 0);

        binding.scoreProgress.setProgressMax((float) ScoreNoOfQuestions);
        binding.scoreProgress.setProgressWithAnimation((float) ScoreCorrectAnswers, (long) 1500);

        binding.score.setText(ScoreCorrectAnswers + "/" + ScoreNoOfQuestions);

        addNumbers(arrayList);

        Rank = arrayList.get(index);

        binding.rank.setText("You are in top " + Rank);

        binding.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });

    }

    private void addNumbers(ArrayList<String> arrayList) {
        arrayList.add("400");
        arrayList.add("500");
        arrayList.add("600");
        arrayList.add("900");
        arrayList.add("1000");
        arrayList.add("50%");
        arrayList.add("100%+");
        arrayList.add("200%+");
        arrayList.add("300%+");
        arrayList.add("500%+");
        arrayList.add("1000%+");

        index = random.nextInt(arrayList.size());
    }

    private void finishActivity() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Redirecting to Home Page");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("introQuizTaken", true);

        firestore.collection("User").document(auth.getCurrentUser().getUid()).update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(IntroQuizScoreActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                                progressDialog.dismiss();
                            }
                        }, 3000);
                        Log.d("TAG", "Intro quiz is taken");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("TAG", "Intro quiz error: " + e.getMessage());
            }
        });

    }
}