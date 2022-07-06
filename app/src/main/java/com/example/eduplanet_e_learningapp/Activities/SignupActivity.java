package com.example.eduplanet_e_learningapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivitySignupBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageRef;
    User user;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 78;

    private static final int PICK_IMAGE_REQUEST_CODE = 22;
    Uri filePath;
    String fileuri;

    boolean isClassChosen, isIntroQuizTaken;

    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        processRequest();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        binding.loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        binding.continueWithGoogleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        binding.continueWithFacebookCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, FacebookAuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

//        FirebaseFirestore.getInstance().collection("User").document(mAuth.getCurrentUser().getUid())
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
//                        if (value.exists()) {
//                            isClassChosen = value.getBoolean("isClassChosen");
//                            isIntroQuizTaken = value.getBoolean("isIntroQuizTaken");
//                        }
//                    }
//                });

        // Check if user is signed in (non-null) and update UI accordingly.
//        if (mAuth.getCurrentUser() != null || !isClassChosen || !isIntroQuizTaken) {
//            startActivity(new Intent(SignupActivity.this, ChooseClassActivity.class));
//            finish();
//        } else if (mAuth.getCurrentUser() != null || isClassChosen || !isIntroQuizTaken) {
//            startActivity(new Intent(SignupActivity.this, ChooseClassActivity.class));
//            finish();
//        } else if (mAuth.getCurrentUser() != null || isClassChosen || isIntroQuizTaken) {
//            startActivity(new Intent(SignupActivity.this, MainActivity.class));
//            finish();
//        }

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        }
    }

    private void processRequest() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        gsc = GoogleSignIn.getClient(this, gso);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Checking request code
        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK
                    && data != null
                    && data.getData() != null) {

                // Get the Uri of data
                filePath = data.getData();

                Glide.with(this).load(filePath).into(binding.profileImage);
            }
        } else if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Google_client_id", account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createUser() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Registering User");
        dialog.setCancelable(false);

        String Email = binding.email.getText().toString();
        String Password = binding.password.getText().toString();
        String Username = binding.username.getText().toString();

        if (filePath == null) {
            Toast.makeText(this, "Please choose a Profile Image", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(Username)) {
            binding.username.setError("Username cannot be empty");
            binding.username.requestFocus();
        } else if (TextUtils.isEmpty(Email)) {
            binding.email.setError("Email cannot be empty");
            binding.email.requestFocus();
        } else if (TextUtils.isEmpty(Password)) {
            binding.password.setError("Password cannot be empty");
            binding.password.requestFocus();
        } else {
            dialog.show();
            if (filePath != null) {
                dialog.setMessage("Uploading Profile Image");
                // Defining the child of storageReference
                StorageReference ref = storageRef.child("User Profile Images").child("images/" + UUID.randomUUID().toString());

                ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                fileuri = uri.toString();
                                dialog.setMessage("Registering User");

                                user = new User(Username, Email, Password, fileuri);
                                user.setSignUpProvider("Email and Password");

                                mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            String uid = task.getResult().getUser().getUid();
                                            firestore.collection("User").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();

                                                        Toast.makeText(SignupActivity.this, "Welcome to Eduplanet " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(SignupActivity.this, ChooseClassActivity.class));
                                                        finish();

                                                    } else {
                                                        dialog.dismiss();
                                                        Toast.makeText(SignupActivity.this, "" +
                                                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(SignupActivity.this, "" +
                                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });


                            }
                        });
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error, Image not uploaded
                        dialog.dismiss();
                        Toast.makeText(SignupActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

            } else {
                dialog.dismiss();
            }
        }
    }

    private void googleSignIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("GoogleCrendentialSignIn", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            User users = new User();
                            users.setUsername(user.getDisplayName());
                            users.setUserImage(user.getPhotoUrl().toString());
                            users.setUserEmail(user.getEmail());
                            users.setSignUpProvider("Google");

                            String uid = user.getUid();

                            firestore.collection("User").document(uid).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startActivity(new Intent(SignupActivity.this, ChooseClassActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(SignupActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            Log.w("GoogleCrendentialSignIn", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

}