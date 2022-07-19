package com.example.eduplanet_e_learningapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivityChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    FirebaseUser user;

    ProgressDialog dialog;
    String email, oldPass;

    ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        dialog = new ProgressDialog(this);

        firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    binding.password.setText(value.getString("userPassword"));
                    email = value.getString("userEmail");
                    oldPass = value.getString("userPassword");
                }
            }
        });

//        binding.password.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                String pass = binding.password.getText().toString();
//
//                if (pass.length() < 8){
//                    binding.textInputLayout.setError("Password must be greater than 8 letters or digits");
//                } else if (pass.length() > 25){
//                    binding.textInputLayout.setError("Password must be less than 25 letters or digits");
//                } else if (pass.contains("*") || pass.contains("&") || pass.contains("^") || pass.contains("%")
//                        || pass.contains("$") || pass.contains("~") || pass.contains("`") || pass.contains(",")
//                        || pass.contains("'") || pass.contains(":") || pass.contains(";") || pass.contains("<")
//                        || pass.contains(">") ){
//                    binding.textInputLayout.setError("Password must not be contain symbolic characters");
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this);
                alertDialog.setTitle("Are you sure you want to go back?");
                alertDialog.setMessage("Your password will not be change.");
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


        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePassword();
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this);
        alertDialog.setTitle("Are you sure you want to go back?");
        alertDialog.setMessage("Your password will not be change.");
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

    private void savePassword() {
        String newPass = binding.password.getText().toString();

        if (TextUtils.isEmpty(newPass)) {
            binding.password.setError("Password must not be empty");
            binding.password.requestFocus();
        } else if (newPass.length() < 8) {
            binding.password.setError("Password must be greater than 8 letters or digits");
            binding.password.requestFocus();
        } else if (newPass.length() > 25) {
            binding.password.setError("Password must be less than 25 letters or digits");
            binding.password.requestFocus();
        } else if (newPass.contains("*") || newPass.contains("&") || newPass.contains("^") || newPass.contains("%")
                || newPass.contains("$") || newPass.contains("~") || newPass.contains("`") || newPass.contains(",")
                || newPass.contains("'") || newPass.contains(":") || newPass.contains(";") || newPass.contains("<")
                || newPass.contains(">")) {
            binding.password.setError("Password must not be contain symbolic characters");
            binding.password.requestFocus();
        } else {
            dialog.setTitle("Changing Password");
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();

            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("userPassword", newPass);

                                    firestore.collection("User").document(auth.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ChangePasswordActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                            finish();
                                            dialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(ChangePasswordActivity.this, "Password changing failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(ChangePasswordActivity.this, "Password changing failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        dialog.dismiss();
                        Toast.makeText(ChangePasswordActivity.this, "Password changing failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }
}