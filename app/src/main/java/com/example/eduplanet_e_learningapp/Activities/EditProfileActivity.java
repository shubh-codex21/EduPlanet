package com.example.eduplanet_e_learningapp.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityEditProfileBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class EditProfileActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ActivityResultLauncher<Intent> launcher1, launcher2;
    ProgressDialog dialog;
    DatePickerDialog.OnDateSetListener datePickerListener;
    Calendar calendar;

    String[] classes;
    String coverFilePath, profileFilePath, birthDate, oldEmail;
    ArrayAdapter arrayAdapter;
    boolean isGoogleProvider;

    ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);

        calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    User user = value.toObject(User.class);
                    binding.userName.setText(user.getUsername());
                    if (user.getUserImage() != null) {
                        Glide.with(getApplicationContext()).load(user.getUserImage()).into(binding.userProfileImage);
                    } else {
                        Glide.with(getApplicationContext()).load(R.drawable.avatar2).into(binding.userProfileImage);
                    }
                    if (user.getUserCover() != null) {
                        Glide.with(getApplicationContext()).load(user.getUserCover()).into(binding.userCover);
                    } else {
                        Glide.with(getApplicationContext()).load(R.color.blue1).into(binding.userCover);
                    }
                    binding.userEmail.setText(user.getUserEmail());
                    oldEmail = user.getUserEmail();
                    if (!user.getSignUpProvider().equals("Google")) {
                        binding.userPassword.setText(user.getUserPassword());
                        binding.passwordCard.setVisibility(View.VISIBLE);
                        isGoogleProvider = false;
                    } else {
                        binding.passwordCard.setVisibility(View.GONE);
                        isGoogleProvider = true;
                    }
                    binding.userclass.setText(user.getUserClass());
                    if (user.getUserBio() != null) {
                        binding.userBio.setText(user.getUserBio());
                    }
                    if (user.getLocation() != null) {
                        binding.location1.setCountryForNameCode(user.getCountryCode());
                    }
                    if (user.getLink() != null) {
                        binding.userLink.setText(user.getLink());
                    }
                    if (user.getDateOfBirth() != null) {
                        binding.userDateOfBirth.setText(user.getDateOfBirth());
                    }
                }
            }
        });

        // Adding text change listeners --start
        binding.userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = binding.userName.getText().toString();

                if (!name.isEmpty() && name.length() >= 8) {
                    enableSaveBtn();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = binding.userEmail.getText().toString();

                if (!email.isEmpty() && email.contains("@gmail.com")) {
                    enableSaveBtn();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.userBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                enableSaveBtn();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.userclass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableSaveBtn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.userLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableSaveBtn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.userDateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableSaveBtn();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // Adding text change listeners --end

        binding.saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });

        binding.editCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(EditProfileActivity.this)
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
                                launcher1.launch(it);
                            }
                        }));
            }
        });

        launcher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null) {
                            if (result.getResultCode() == RESULT_OK) {
                                Uri selectedImage = result.getData().getData();

                                StorageReference reference = storage.getReference().child("User Cover Images").child(auth.getUid())
                                        .child(new Date().getTime() + "");

                                dialog.setCancelable(false);
                                dialog.setTitle("Changing Cover");
                                dialog.setMessage("Please wait...");
                                dialog.show();
                                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    coverFilePath = uri.toString();
                                                    Glide.with(getApplicationContext()).load(coverFilePath).into(binding.userCover);
                                                    dialog.dismiss();
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

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(EditProfileActivity.this)
                        .crop()
                        .cropOval()
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
            }
        });

        launcher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null) {
                            if (result.getResultCode() == RESULT_OK) {
                                Uri selectedImage = result.getData().getData();

                                StorageReference reference = storage.getReference().child("User Profile Images").child(auth.getUid())
                                        .child(new Date().getTime() + "");

                                dialog.setCancelable(false);
                                dialog.setTitle("Changing Profile Image");
                                dialog.setMessage("Please wait...");
                                dialog.show();
                                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    profileFilePath = uri.toString();
                                                    Glide.with(getApplicationContext()).load(profileFilePath).into(binding.userProfileImage);
                                                    dialog.dismiss();
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

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProfileActivity.this);
                alertDialog.setTitle("Are you sure you want to go back?");
                alertDialog.setMessage("Your changes will be discard.");
                alertDialog.setCancelable(true);

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                AlertDialog builder = alertDialog.create();
                builder.show();
            }
        });

        binding.userPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, ChangePasswordActivity.class));
            }
        });

        // Adding date of birth picker --start

        // Below code is of small date picker
//        binding.selectDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this,
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//                        datePickerListener,
//                        year, month, day);
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                datePickerDialog.show();
//            }
//        });
//
//        datePickerListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                month = month + 1;
//                String date = dayOfMonth + "/" + month + "/" + year;
//                binding.userDateOfBirth.setText(date);
//                birthDate = date;
//            }
//        };

        // Below code is of big date picker

        binding.selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                month = month + 1;
                                String date = dayOfMonth + "/" + month + "/" + year;
                                binding.userDateOfBirth.setText(date);
                            }
                        }, year, month, day);
                datePickerDialog.show();

            }
        });

        // Adding date of birth picker --end

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProfileActivity.this);
        alertDialog.setTitle("Are you sure you want to go back?");
        alertDialog.setMessage("Your changes will be discard.");
        alertDialog.setCancelable(true);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog builder = alertDialog.create();
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void enableSaveBtn() {
//        binding.saveChangesBtn.setImageTintList(getResources().getColorStateList(R.color.white));
    }

    @Override
    public void onResume() {
        super.onResume();
        classes = getResources().getStringArray(R.array.classes);
        arrayAdapter = new ArrayAdapter(EditProfileActivity.this, R.layout.doubt_dropdown_itemview, classes);
        binding.userclass.setAdapter(arrayAdapter);
    }

    public void saveChanges() {
        String name = binding.userName.getText().toString();
        String email = binding.userEmail.getText().toString();
        String Class = binding.userclass.getText().toString();
        String bio = binding.userBio.getText().toString();
        String location = binding.location1.getSelectedCountryName();
        String countryCode = binding.location1.getSelectedCountryNameCode();
        String link = binding.userLink.getText().toString();
        String dateOfBirth = binding.userDateOfBirth.getText().toString();

        if (TextUtils.isEmpty(name)) {
            binding.userName.setError("Username cannot be empty");
            binding.userName.requestFocus();
        } else if (name.length() < 8) {
            binding.userName.setError("Username length should greater than 8");
            binding.userName.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            binding.userEmail.setError("Email cannot be empty");
            binding.userEmail.requestFocus();
        } else if (!email.contains("@gmail.com")) {
            binding.userEmail.setError("Email length should greater than 8");
            binding.userEmail.requestFocus();
        } else {
            dialog.setTitle("Saving Changes");
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("username", name);
            hashMap.put("userEmail", email);
            hashMap.put("userClass", Class);
            hashMap.put("userBio", bio);
            hashMap.put("location", location);
            hashMap.put("countryCode", countryCode);
            hashMap.put("link", link);
            if (birthDate != null) {
                if (!birthDate.isEmpty()) {
                    hashMap.put("dateOfBirth", dateOfBirth);
                }
            } else {
                firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            String date = value.getString("dateOfBirth");
                            hashMap.put("dateOfBirth", date);
                        }
                    }
                });
            }
            if (profileFilePath != null) {
                if (!profileFilePath.isEmpty()) {
                    hashMap.put("userImage", profileFilePath);
                }
            } else {
                firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            String image = value.getString("userImage");
                            hashMap.put("userImage", image);
                        }
                    }
                });
            }
            if (coverFilePath != null) {
                if (!coverFilePath.isEmpty()) {
                    hashMap.put("userCover", coverFilePath);
                }
            } else {
                firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            String image = value.getString("userCover");
                            hashMap.put("userCover", image);
                        }
                    }
                });
            }

            if (!oldEmail.toLowerCase().trim().equals(email.toLowerCase().trim())) {
                auth.getCurrentUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firestore.collection("User").document(auth.getUid()).update(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            Toast.makeText(EditProfileActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                            finish();
                                            dialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Log.d("Fail", e.getMessage());
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(EditProfileActivity.this, "" + task.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                firestore.collection("User").document(auth.getUid()).update(hashMap)
                        .addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(EditProfileActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                finish();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d("Fail", e.getMessage());
                        dialog.dismiss();
                    }
                });

            }

        }

    }

}