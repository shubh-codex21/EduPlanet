package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Activities.ShowingProfileActivity;
import com.example.eduplanet_e_learningapp.Modals.Doubt;
import com.example.eduplanet_e_learningapp.Modals.Follow;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.SearchUsersItemviewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.ViewHolder> {
    List<User> users;
    Context context;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public SearchUsersAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_users_itemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        User user = users.get(position);
        Glide.with(context.getApplicationContext()).load(user.getUserImage()).into(holder.binding.profileImage);
        holder.binding.userName.setText(user.getUsername());
        holder.binding.userClass.setText(user.getUserClass());

        firestore.collection("User").document(user.getUserId()).collection("followers")
                .document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    holder.binding.followBtn.setCardBackgroundColor(context.getResources().getColorStateList(R.color.dark_grey));
                    holder.binding.followTv.setText("Following");
                }
            }
        });

        holder.binding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("User").document(user.getUserId()).collection("followers")
                        .document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            firestore.collection("User").document(user.getUserId()).collection("followers")
                                    .document(auth.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("followers", FieldValue.increment(-1));
                                    firestore.collection("User").document(user.getUserId()).update(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("following", FieldValue.increment(-1));
                                                    firestore.collection("User").document(auth.getUid()).update(hashMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.followBtn.setCardBackgroundColor(context.getResources().getColorStateList(R.color.blue1));
                                                                    holder.binding.followTv.setText("Follow");
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
                            Follow follow = new Follow();
                            follow.setFollowedBy(auth.getUid());
                            follow.setFollowedAt(new Date().getTime());

                            firestore.collection("User")
                                    .document(user.getUserId())
                                    .collection("followers")
                                    .document(auth.getUid())
                                    .set(follow)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("followers", FieldValue.increment(1));
                                            firestore.collection("User").document(user.getUserId()).update(hashMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            HashMap<String, Object> hashMap = new HashMap<>();
                                                            hashMap.put("following", FieldValue.increment(1));
                                                            firestore.collection("User").document(auth.getUid()).update(hashMap)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            holder.binding.followBtn.setCardBackgroundColor(context.getResources().getColorStateList(R.color.dark_grey));
                                                                            holder.binding.followTv.setText("Following");
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowingProfileActivity.class);
                intent.putExtra("profileId",user.getUserId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setFilteredList(List<User> filteredList){
        this.users = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SearchUsersItemviewBinding binding;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = SearchUsersItemviewBinding.bind(itemView);
        }
    }
}
