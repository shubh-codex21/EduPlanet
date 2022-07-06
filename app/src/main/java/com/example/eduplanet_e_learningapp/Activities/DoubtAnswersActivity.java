package com.example.eduplanet_e_learningapp.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Adapters.DoubtAnswerAdapter;
import com.example.eduplanet_e_learningapp.Modals.Doubt;
import com.example.eduplanet_e_learningapp.Modals.DoubtAnswer;
import com.example.eduplanet_e_learningapp.Modals.DoubtAttachments;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityDoubtShowAnswersBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class DoubtAnswersActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ArrayList<DoubtAnswer> answers;
    DoubtAnswerAdapter adapter;
    String doubtId;

    ActivityDoubtShowAnswersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoubtShowAnswersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        answers = new ArrayList<>();
        adapter = new DoubtAnswerAdapter(answers, this);

        doubtId = getIntent().getStringExtra("doubtId");

        firestore.collection("Doubt").document(doubtId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    Doubt doubt = value.toObject(Doubt.class);
                    binding.title.setText(doubt.getTitle());
                    binding.description.setText(doubt.getDescription());
                    binding.noOfUpvotes.setText(doubt.getUpvotes() + "");
                    binding.noOfAnswer.setText(doubt.getAnswers() + " Answers");
                    String time = TimeAgo.using(doubt.getDoubtedAt());
                    binding.doubtedAt.setText(time);

                    firestore.collection("User").document(doubt.getDoubtedBy()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                            if (value.exists()) {
                                User user = value.toObject(User.class);
                                binding.userName.setText(user.getUsername());
                                Glide.with(DoubtAnswersActivity.this).load(user.getUserImage()).into(binding.userImage);
                            }
                        }
                    });

//                    if (doubt.getNoOfAttachments() != 0) {
//                        binding.carousel.setSize(doubt.getAttachments().size());
//                        binding.carousel.setCarouselViewListener(new CarouselViewListener() {
//                            @Override
//                            public void onBindView(View view, int position) {
//                                // Example here is setting up a full image carousel
//                                ImageView carouselImage = view.findViewById(R.id.carouselImage);
//                                Glide.with(DoubtAnswersActivity.this).load(doubt.getAttachments().get(position).getImageUrl())
//                                        .into(carouselImage);
//                            }
//                        });
//                        binding.carousel.show();
//                        binding.carousel.setVisibility(View.VISIBLE);
//
//                    } else {
//                        binding.carousel.setVisibility(View.GONE);
//                    }


                }
            }
        });

        fetchAnswers();

        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.answersRv.setLayoutManager(manager);
        binding.answersRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FirebaseFirestore.getInstance().collection("Doubt").document(doubtId).collection("upvotedUsers")
                .document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    binding.upvote.setImageDrawable(getResources().getDrawable(R.drawable.upvoted));
                } else {
                    binding.upvote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("upvoted", true);
                            FirebaseFirestore.getInstance().collection("Doubt").document(doubtId).collection("upvotedUsers")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("upvotes", FieldValue.increment(1));
                                            FirebaseFirestore.getInstance().collection("Doubt").document(doubtId).update(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            binding.upvote.setImageDrawable(getResources().getDrawable(R.drawable.upvoted));
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull @NotNull Exception e) {
                                                    Log.d("DOUBT", e.getMessage());
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Log.d("DOUBT", e.getMessage());
                                }
                            });
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("DOUBT", e.getMessage());
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DoubtAnswersActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.giveAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoubtAnswersActivity.this,GiveAnswerActivity.class);
                intent.putExtra("doubtId",doubtId);
                Toast.makeText(DoubtAnswersActivity.this, "Intent DAA: "+doubtId, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

    }

    private void fetchAnswers() {
        firestore.collection("Doubt").document(doubtId).collection("Answers")
                .orderBy("likes", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    answers.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        DoubtAnswer answer = snapshot.toObject(DoubtAnswer.class);
                        answer.setAnswerId(snapshot.getId());
                        answer.setAnsweredBy(snapshot.getString("answeredBy"));
                        answer.setDoubtId(doubtId);
                        answer.setAnsweredAt(snapshot.getLong("answeredAt"));

                        ArrayList<DoubtAttachments> AnswerAttachments = new ArrayList<>();
                        snapshot.getReference().collection("Attachments")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                        for (DocumentSnapshot snapshot1 : value.getDocuments()) {
                                            DoubtAttachments doubtAttachments = snapshot1.toObject(DoubtAttachments.class);
                                            AnswerAttachments.add(doubtAttachments);
                                        }
                                    }
                                });
                        answer.setAnswerAttachments(AnswerAttachments);
                        answers.add(answer);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}