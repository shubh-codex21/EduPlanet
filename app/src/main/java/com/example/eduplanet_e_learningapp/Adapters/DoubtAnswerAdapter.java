package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Modals.DoubtAnswer;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.DoubtAnswersItemviewBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jama.carouselview.CarouselViewListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class DoubtAnswerAdapter extends RecyclerView.Adapter<DoubtAnswerAdapter.ViewHolder> {
    ArrayList<DoubtAnswer> answers;
    Context context;

    public DoubtAnswerAdapter(ArrayList<DoubtAnswer> answers, Context context) {
        this.answers = answers;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doubt_answers_itemview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        DoubtAnswer modal = answers.get(position);

        holder.binding.answer.setText(modal.getAnswer());
        holder.binding.noOfLikes.setText(modal.getLikes()+"");
        String time = TimeAgo.using(modal.getAnsweredAt());
        holder.binding.answeredAt.setText(time);

        FirebaseFirestore.getInstance().collection("User").document(modal.getAnsweredBy())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                        if (value.exists()) {
                            User user = value.toObject(User.class);
                            Glide.with(context).load(user.getUserImage()).into(holder.binding.userImage);
                            holder.binding.userName.setText(user.getUsername());
                        }
                    }
                });

        if (modal.getNoOfAnswerAttachments() != 0) {
            holder.binding.answerCarousel.setSize(modal.getAnswerAttachments().size());
            holder.binding.answerCarousel.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    // Example here is setting up a full image carousel
                    ImageView carouselImage = view.findViewById(R.id.carouselImage);
                    Glide.with(context).load(modal.getAnswerAttachments().get(position).getImageUrl())
                            .into(carouselImage);
                }
            });
            holder.binding.answerCarousel.show();
            holder.binding.answerCarousel.setVisibility(View.VISIBLE);

        } else {
            holder.binding.answerCarousel.setVisibility(View.GONE);
        }

        FirebaseFirestore.getInstance().collection("Doubt").document(modal.getDoubtId()).collection("Answers").document(modal.getAnswerId()).collection("likedUsers")
                .document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.binding.like.setImageDrawable(context.getResources().getDrawable(R.drawable.liked2));
                } else {
                    holder.binding.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("liked", true);
                            FirebaseFirestore.getInstance().collection("Doubt").document(modal.getDoubtId()).collection("Answers")
                                    .document(modal.getAnswerId()).collection("likedUsers")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("likes", FieldValue.increment(1));
                                            FirebaseFirestore.getInstance().collection("Doubt").document(modal.getDoubtId())
                                                    .collection("Answers").document(modal.getAnswerId()).update(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            holder.binding.like.setImageDrawable(context.getResources().getDrawable(R.drawable.liked2));
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

    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        DoubtAnswersItemviewBinding binding;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = DoubtAnswersItemviewBinding.bind(itemView);
        }
    }
}
