package com.example.eduplanet_e_learningapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eduplanet_e_learningapp.Adapters.DoubtAttachmentAdapter;
import com.example.eduplanet_e_learningapp.Modals.Doubt;
import com.example.eduplanet_e_learningapp.Modals.DoubtAttachments;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityAskDoubtBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

public class AskDoubtActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;

    ActivityResultLauncher<Intent> launcher;
    Boolean isTitleNull = true, isDescriptionNull = true;
    ProgressDialog dialog;
    ArrayList<DoubtAttachments> attachments;
    DoubtAttachmentAdapter adapter;
    String docId;
    int noOfAttachments = 0;

    ActivityAskDoubtBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAskDoubtBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);
        attachments = new ArrayList<>();
        adapter = new DoubtAttachmentAdapter(attachments, this);

        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

        docId = firestore.collection("Doubt").document().getId();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.attachmentRv.setLayoutManager(manager);
        binding.attachmentRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.postDoubtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDoubt();
            }
        });

        binding.title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String title = binding.title.getText().toString();

                if (!title.isEmpty()) {
                    isTitleNull = false;
                } else {
                    isTitleNull = true;
                }
                enablePostBtn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String description = binding.description.getText().toString();

                if (!description.isEmpty()) {
                    isDescriptionNull = false;
                } else {
                    isDescriptionNull = true;
                }
                enablePostBtn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.addAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(AskDoubtActivity.this)
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

                                StorageReference reference = storage.getReference().child("DoubtAttachments").child(auth.getUid())
                                        .child(new Date().getTime() + "");

                                dialog.show();
                                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {

                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String filepath = uri.toString();

                                                    DoubtAttachments doubtAttachments = new DoubtAttachments();
                                                    doubtAttachments.setImageUrl(filepath);

                                                    String imageDocId = firestore.collection("Doubt").document(docId)
                                                            .collection("Attachments").document().getId();

                                                    firestore.collection("Doubt").document(docId).collection("Attachments")
                                                            .document(imageDocId).set(doubtAttachments).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            noOfAttachments++;
                                                            fetchAttachments();
                                                            dialog.dismiss();
                                                        }
                                                    });

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

    }

    private void fetchAttachments() {
        firestore.collection("Doubt").document(docId).collection("Attachments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            attachments.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                DoubtAttachments Attachments = snapshot.toObject(DoubtAttachments.class);
                                Attachments.setImageId(snapshot.getId());
                                attachments.add(Attachments);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void enablePostBtn() {
        if (!isTitleNull && !isDescriptionNull) {
            binding.postDoubtBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            binding.postDoubtBtn.setClickable(true);
        } else {
            binding.postDoubtBtn.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_grey)));
            binding.postDoubtBtn.setClickable(false);
        }
    }

    private void postDoubt() {
        dialog.setMessage("Uploading Doubt...");
        dialog.setCancelable(false);

        String title = binding.title.getText().toString();
        String description = binding.description.getText().toString();

        if (TextUtils.isEmpty(title)) {
            binding.title.setError("It must not be null.");
            binding.title.requestFocus();
        } else if (TextUtils.isEmpty(description)) {
            binding.description.setError("It must not be null.");
            binding.description.requestFocus();
        } else {
            Doubt doubt = new Doubt();
            doubt.setTitle(title);
            doubt.setDescription(description);
            doubt.setNoOfAttachments(noOfAttachments);
            doubt.setDoubtedBy(auth.getUid());
            doubt.setDoubtedAt(new Date().getTime());

            dialog.show();
            firestore.collection("Doubt").document(docId).set(doubt).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.hide();
                    Toast.makeText(AskDoubtActivity.this, "Doubt Posted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}