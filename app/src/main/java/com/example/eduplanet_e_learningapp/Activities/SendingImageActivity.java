package com.example.eduplanet_e_learningapp.Activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eduplanet_e_learningapp.Modals.Message;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.databinding.ActivitySendingImageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;

public class SendingImageActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    ProgressDialog dialog;

    Uri imageUri;
    String selectedImage, senderRoom, Class;

    ActivitySendingImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendingImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        dialog.setMessage("Sending Image...");
        dialog.setCancelable(false);

        selectedImage = getIntent().getStringExtra("selectedImage");
        imageUri = getIntent().getParcelableExtra("imageUri");

        firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    User user = value.toObject(User.class);
                    Class = user.getUserClass();
                }
            }
        });


        binding.image.setImageURI(imageUri);

        senderRoom = auth.getUid();
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String messageTxt = binding.messageEt.getText().toString();
                Message message = new Message(messageTxt, senderRoom, "image", new Date().getTime());
                message.setImageUrl(selectedImage);
                binding.messageEt.setText("");

                String docId = firestore.collection("Discuss").document(Class).collection("SenderRoom")
                        .document(senderRoom).collection("Messages").document().getId();

                firestore.collection("Discuss").document(Class).collection("SenderRoom")
                        .document(senderRoom).collection("Messages").document(docId).set(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firestore.collection("Discuss").document(Class)
                                .collection("ReceiverRoom").document(docId).set(message)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                    }
                });
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

}