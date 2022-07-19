package com.example.eduplanet_e_learningapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Modals.Post;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityCreatePostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class CreatePostActivity extends AppCompatActivity {
    private static final int IMAGE_PICKER_CODE = 74;
    Uri uri;
    Boolean isCaptionNull = true, isPostImageNull = true;
    String postingUsername, postingUserImage;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;

    ProgressDialog dialog;

    ActivityCreatePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);

        firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value.exists()) {
                    User user = value.toObject(User.class);
                    postingUsername = user.getUsername();
                    postingUserImage = user.getUserImage();

                    Glide.with(CreatePostActivity.this).load(postingUserImage).into(binding.userProfileImage);
                    binding.userName.setText(postingUsername);
                }
            }
        });

        binding.postCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String caption = binding.postCaption.getText().toString();

                if (!caption.isEmpty()) {
                    isCaptionNull = false;
                } else {
                    isCaptionNull = true;
                }
                enablePostBtn();


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICKER_CODE);
            }
        });

        binding.postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("Post Uploading");
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                final StorageReference reference = storage.getReference().child("User Posts Images")
                        .child(auth.getCurrentUser().getUid()).child(new Date().getTime() + "");

                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Post post = new Post();
                                post.setPostImage(uri.toString());
                                post.setPostCaption(binding.postCaption.getText().toString());
                                post.setPostedAt(new Date().getTime());
                                post.setPostedBy(auth.getCurrentUser().getUid());

                                String docId = firestore.collection("Post").document().getId();

                                firestore.collection("Post").document(docId).set(post)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                firestore.collection("User").document(auth.getUid())
                                                        .update("noOfPost", FieldValue.increment(1))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                dialog.dismiss();
                                                                finish();
                                                                Toast.makeText(CreatePostActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(CreatePostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Log.d("TAGG", e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d("TAGG", e.getMessage());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null && requestCode == IMAGE_PICKER_CODE) {
            uri = data.getData();
            Glide.with(this).load(uri).into(binding.postImage);
            binding.postImage.setVisibility(View.VISIBLE);

            isPostImageNull = false;
            enablePostBtn();

        }

    }

    private void enablePostBtn() {
        if (!isPostImageNull) {
            binding.postBtn.setBackgroundColor(getResources().getColor(R.color.blue1));
            binding.postBtn.setEnabled(true);
        } else {
            binding.postBtn.setBackgroundColor(getResources().getColor(R.color.light_grey));
            binding.postBtn.setEnabled(false);
        }
    }
}