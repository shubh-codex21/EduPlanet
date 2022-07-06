package com.example.eduplanet_e_learningapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eduplanet_e_learningapp.Activities.SendingImageActivity;
import com.example.eduplanet_e_learningapp.Adapters.DiscussAdapter;
import com.example.eduplanet_e_learningapp.Modals.Message;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.databinding.FragmentDiscussBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

import static android.app.Activity.RESULT_OK;

public class DiscussFragment extends Fragment {
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ActivityResultLauncher<String> galleryLauncher;
    ProgressDialog dialog;
    ActivityResultLauncher<Intent> launcher;

    ArrayList<Message> messages;
    DiscussAdapter discussAdapter;
    String senderRoom, Class;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());

        messages = new ArrayList<>();
        discussAdapter = new DiscussAdapter(messages, getContext());

    }

    FragmentDiscussBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDiscussBinding.inflate(inflater, container, false);

        firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value.exists()){
                    User user = value.toObject(User.class);
                    Class = user.getUserClass();
                }
            }
        });

        fetchMessages();

        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        binding.discussRv.setLayoutManager(manager);
        binding.discussRv.setAdapter(discussAdapter);
        discussAdapter.notifyDataSetChanged();

        senderRoom = auth.getUid();

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(binding.messageEt.getText().toString(), senderRoom, "text", new Date().getTime());
                binding.messageEt.setText("");

                String docId = firestore.collection("Discuss").document(Class).collection("SenderRoom")
                        .document(senderRoom).collection("Messages").document().getId();

                firestore.collection("Discuss").document(Class).collection("SenderRoom")
                        .document(senderRoom).collection("Messages").document(docId).set(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firestore.collection("Discuss").document(Class).collection("ReceiverRoom")
                                .document(docId).set(message);
                    }
                });
            }
        });

//Camera & Gallery Image Picker Work

        binding.galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(getActivity())
                        .crop()
                        .cropFreeStyle()
                        .maxResultSize(1080, 1080, true)
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher.launch(it);
                            }
                        }));
            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null) {
                            if (result.getResultCode() == RESULT_OK) {
                                Uri selectedImage = result.getData().getData();

                                StorageReference reference = storage.getReference().child("DiscussChats").child(auth.getUid())
                                        .child(new Date().getTime() + "");

                                dialog.show();
                                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                        dialog.dismiss();
                                        if (task.isSuccessful()) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String filepath = uri.toString();
                                                    Intent intent = new Intent(getContext(), SendingImageActivity.class);
                                                    intent.putExtra("selectedImage", filepath);
                                                    intent.putExtra("imageUri", selectedImage);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                });

                            } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                                // Use ImagePicker.Companion.getError(result.getData()) to show an error
                                Log.d("TAG", ImagePicker.Companion.getError(result.getData()));
                            }
                        }
                    }
                });

// Camera & Gallery Image Picker Work

        binding.emojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Coming soon...", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void fetchMessages() {
        firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot documentSnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    String Clas = user.getUserClass();

                    firestore.collection("Discuss").document(Clas).collection("ReceiverRoom")
                            .orderBy("messagedAt", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                            if (!value.isEmpty()) {
                                messages.clear();
                                for (DocumentSnapshot snapshot : value.getDocuments()) {
                                    Message modal = snapshot.toObject(Message.class);
                                    modal.setMessageId(snapshot.getId());
                                    messages.add(modal);
                                }
                                discussAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });

    }
}