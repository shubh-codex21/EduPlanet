package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Activities.DoubtAnswersActivity;
import com.example.eduplanet_e_learningapp.Modals.Doubt;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.DoubtItemviewBinding;
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

import java.util.HashMap;
import java.util.List;

public class DoubtsAdapter extends RecyclerView.Adapter<DoubtsAdapter.ViewHolder> {
    List<Doubt> doubts;
    Context context;

    public DoubtsAdapter(List<Doubt> doubts, Context context) {
        this.doubts = doubts;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doubt_itemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Doubt doubt = doubts.get(position);
        holder.binding.title.setText(doubt.getTitle());
        holder.binding.description.setText(doubt.getDescription());
        holder.binding.noOfUpvotes.setText(doubt.getUpvotes() + "");
        int answers = doubt.getAnswers();
        if (answers == 0) {
            holder.binding.noOfAnswer.setText(doubt.getAnswers() + " Answers");
        } else if (answers == 1) {
            holder.binding.noOfAnswer.setText("View " + doubt.getAnswers() + " Answer");
        } else {
            holder.binding.noOfAnswer.setText("View all " + doubt.getAnswers() + " Answers");
        }

        FirebaseFirestore.getInstance().collection("User").document(doubt.getDoubtedBy())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                        if (value.exists()) {
                            User user = value.toObject(User.class);
                            Glide.with(context).load(user.getUserImage()).into(holder.binding.userImage);
                            holder.binding.userName.setText(user.getUsername());
                            String time = TimeAgo.using(doubt.getDoubtedAt());
                            holder.binding.doubtedAt.setText(time);
                        }
                    }
                });

        if (doubt.getNoOfAttachments() != 0) {
            holder.binding.doubtImageCarousel.setSize(doubt.getAttachments().size());
            holder.binding.doubtImageCarousel.setCarouselViewListener(new CarouselViewListener() {
                @Override
                public void onBindView(View view, int position) {
                    // Example here is setting up a full image carousel
                    ImageView carouselImage = view.findViewById(R.id.carouselImage);
                    Glide.with(context).load(doubt.getAttachments().get(position).getImageUrl())
                            .into(carouselImage);
                }
            });
            holder.binding.doubtImageCarousel.show();
            holder.binding.doubtImageCarousel.setVisibility(View.VISIBLE);

        } else {
            holder.binding.doubtImageCarousel.setVisibility(View.GONE);
        }

        FirebaseFirestore.getInstance().collection("Doubt").document(doubt.getDoubtId()).collection("upvotedUsers")
                .document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.binding.upvote.setImageDrawable(context.getResources().getDrawable(R.drawable.upvoted));
                } else {
                    holder.binding.upvote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("upvoted", true);
                            FirebaseFirestore.getInstance().collection("Doubt").document(doubt.getDoubtId()).collection("upvotedUsers")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("upvotes", FieldValue.increment(1));
                                            FirebaseFirestore.getInstance().collection("Doubt").document(doubt.getDoubtId()).update(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            holder.binding.upvote.setImageDrawable(context.getResources().getDrawable(R.drawable.upvoted));
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

        holder.binding.answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DoubtAnswersActivity.class);
                intent.putExtra("doubtId",doubt.getDoubtId());
                context.startActivity(intent);
            }
        });

        holder.binding.noOfAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DoubtAnswersActivity.class);
                intent.putExtra("doubtId",doubt.getDoubtId());
                context.startActivity(intent);
            }
        });

        holder.binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return doubts.size();
    }

    public void setFilteredList(List<Doubt> filteredList){
        this.doubts = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        DoubtItemviewBinding binding;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = DoubtItemviewBinding.bind(itemView);
        }
    }
}
