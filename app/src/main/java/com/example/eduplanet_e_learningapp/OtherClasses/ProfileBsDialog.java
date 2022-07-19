package com.example.eduplanet_e_learningapp.OtherClasses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eduplanet_e_learningapp.Activities.AskDoubtActivity;
import com.example.eduplanet_e_learningapp.Modals.DoubtAttachments;
import com.example.eduplanet_e_learningapp.Modals.Story;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.Modals.UserStories;
import com.example.eduplanet_e_learningapp.R;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

import static android.app.Activity.RESULT_OK;

public class ProfileBsDialog extends BottomSheetDialogFragment {
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ActivityResultLauncher<Intent> launcher1, launcher2;
    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());

        View v = inflater.inflate(R.layout.profile_bs_view,
                container, false);

        TextView editCover = v.findViewById(R.id.editCover);
        TextView editProfile = v.findViewById(R.id.editProfile);

        editCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ImagePicker.Companion.with(getActivity())
                        .crop()
                        .cropFreeStyle()
                        .crop(1080,720)
                        .maxResultSize(1080, 1080, true)
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher1.launch(it);
                            }
                        }));

                dialog.setTitle("Changing Cover");
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);

                launcher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result != null) {
                                    if (result.getResultCode() == RESULT_OK) {
                                        Uri selectedImage = result.getData().getData();

                                        StorageReference reference = storage.getReference().child("User Covers").child(auth.getUid())
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

                                                            HashMap<String,Object> hashMap = new HashMap<>();
                                                            hashMap.put("userCover",filepath);

                                                            firestore.collection("User").document(auth.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(getContext(), "Cover changed", Toast.LENGTH_SHORT).show();
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
                dismiss();
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ImagePicker.Companion.with(getActivity())
                        .crop()
                        .cropFreeStyle()
                        .crop(1080,720)
                        .maxResultSize(1080, 1080, true)
                        .createIntentFromDialog((Function1) (new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher2.launch(it);
                            }
                        }));

                dialog.setTitle("Changing Profile Image");
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);

                launcher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result != null) {
                                    if (result.getResultCode() == RESULT_OK) {
                                        Uri selectedImage = result.getData().getData();

                                        StorageReference reference = storage.getReference().child("User Profile Images").child(auth.getUid())
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

                                                            HashMap<String,Object> hashMap = new HashMap<>();
                                                            hashMap.put("userImage",filepath);

                                                            firestore.collection("User").document(auth.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(getContext(), "Profile image changed", Toast.LENGTH_SHORT).show();
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
                dismiss();
            }
        });
        return v;
    }
}
