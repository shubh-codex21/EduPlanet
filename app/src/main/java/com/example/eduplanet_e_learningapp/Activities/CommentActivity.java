package com.example.eduplanet_e_learningapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Adapters.CommentAdapter;
import com.example.eduplanet_e_learningapp.Modals.Comment;
import com.example.eduplanet_e_learningapp.Modals.Post;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.databinding.ActivityCommentBinding;
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
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    Intent intent;
    String postId, postedBy;

    ArrayList<Comment> arrayList = new ArrayList<>();
    CommentAdapter commentAdapter;

    ActivityCommentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        fetchPosts();
        fetchComments();

        commentAdapter = new CommentAdapter(arrayList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.commentsRv.setLayoutManager(layoutManager);
        binding.commentsRv.setAdapter(commentAdapter);

        binding.commentPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentBody = binding.commentEt.getText().toString();
                Comment comment = new Comment();
                comment.setCommentBody(commentBody);
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(auth.getUid());

                String docId = firestore.collection("Post").document(postId)
                        .collection("comments").document().getId();

                firestore.collection("Post").document(postId)
                        .collection("comments").document(docId).set(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firestore.collection("Post").document(postId)
                                .update("comments", FieldValue.increment(1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        binding.commentEt.setText("");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Log.d("TAGGG", e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d("TAGGG", e.getMessage());
                    }
                });
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void fetchPosts() {
        firestore.collection("Post").document(postId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value.exists()){
                    Post post = value.toObject(Post.class);

                    Glide.with(getApplicationContext()).load(post.getPostImage()).into(binding.postImage);

                    String caption = post.getPostCaption();
                    if (caption.isEmpty() || caption.equals("")) {
                        binding.postCaption.setVisibility(View.GONE);
                        binding.view3.setVisibility(View.GONE);
                    } else {
                        binding.postCaption.setText(post.getPostCaption());
                        binding.postCaption.setVisibility(View.VISIBLE);
                        binding.view3.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        firestore.collection("User").document(postedBy).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value.exists()){
                    User user = value.toObject(User.class);
                    Glide.with(CommentActivity.this).load(user.getUserImage()).into(binding.postUserImage);
                    binding.postUserName.setText(user.getUsername());
                }
            }
        });
    }

    private void fetchComments() {
        firestore.collection("Post").document(postId).collection("comments")
                .orderBy("commentedAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    arrayList.clear();
                    for (DocumentSnapshot snapshot: value.getDocuments()){
                        Comment modal = snapshot.toObject(Comment.class);
                        arrayList.add(modal);
                    }
                    commentAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}