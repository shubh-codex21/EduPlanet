package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Activities.CommentActivity;
import com.example.eduplanet_e_learningapp.Activities.ShowingProfileActivity;
import com.example.eduplanet_e_learningapp.Activities.UserProfileActivity;
import com.example.eduplanet_e_learningapp.Modals.Post;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.PostItemviewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    ArrayList<Post> postArraylist;
    Context context;

    FirebaseFirestore firestore;

    public PostsAdapter(ArrayList<Post> postArraylist, Context context) {
        this.postArraylist = postArraylist;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_itemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        firestore = FirebaseFirestore.getInstance();

        Post post = postArraylist.get(position);

        firestore.collection("User").document(post.getPostedBy()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value.exists()) {
                    User user = value.toObject(User.class);
                    holder.binding.profileName.setText(user.getUsername());
                    Glide.with(context).load(user.getUserImage())
                            .into(holder.binding.profileImage);
                }
            }
        });

        Glide.with(context).load(post.getPostImage()).into(holder.binding.post);
        String caption = post.getPostCaption();
        if (caption.isEmpty() || caption.equals("")) {
            holder.binding.caption.setVisibility(View.GONE);
        } else {
            holder.binding.caption.setText(post.getPostCaption());
            holder.binding.caption.setVisibility(View.VISIBLE);
        }
        holder.binding.noOfLikes.setText(post.getLikes() + "");
        holder.binding.noOfComments.setText(post.getComments() + "");

        firestore.collection("Post").document(post.getPostId()).collection("likedUsers")
                .document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.binding.like.setImageDrawable(context.getResources().getDrawable(R.drawable.liked));
                } else {
                    holder.binding.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("liked", true);
                            firestore.collection("Post").document(post.getPostId()).collection("likedUsers")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("likes", FieldValue.increment(1));
                                            firestore.collection("Post").document(post.getPostId()).update(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            holder.binding.like.setImageDrawable(context.getResources().getDrawable(R.drawable.liked));
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
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("TAG", e.getMessage());
            }
        });

//        binding.like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firestore.collection("Post").document(post.getPostId()).collection("likedUsers")
//                        .document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            binding.like.setImageDrawable(context.getResources().getDrawable(R.drawable.liked));
//                        } else {
//                            HashMap<String, Object> hashMap = new HashMap<>();
//                            hashMap.put("liked", true);
//                            firestore.collection("Post").document(post.getPostId()).collection("likedUsers")
//                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(hashMap)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            HashMap<String, Object> hashMap = new HashMap<>();
//                                            hashMap.put("likes", FieldValue.increment(1));
//                                            firestore.collection("Post").document(post.getPostId()).update(hashMap)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void unused) {
//                                                            binding.like.setImageDrawable(context.getResources().getDrawable(R.drawable.liked));
//                                                        }
//                                                    }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull @NotNull Exception e) {
//                                                    Log.d("TAG", e.getMessage());
//                                                }
//                                            });
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull @NotNull Exception e) {
//                                    Log.d("TAG", e.getMessage());
//                                }
//                            });
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull @NotNull Exception e) {
//                        Log.d("TAG", e.getMessage());
//                    }
//                });
//            }
//        });

        holder.binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("postedBy", post.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.binding.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("postedBy", post.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Coming very soon...", Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Coming very soon...", Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Coming very soon...", Toast.LENGTH_SHORT).show();
            }
        });

        holder.binding.profileLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getPostedBy().equals(FirebaseAuth.getInstance().getUid())) {
                    context.startActivity(new Intent(context, UserProfileActivity.class));

                } else {
                    Intent intent = new Intent(context, ShowingProfileActivity.class);
                    intent.putExtra("profileId", post.getPostedBy());
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return postArraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PostItemviewBinding binding;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = PostItemviewBinding.bind(itemView);

        }
    }
}
