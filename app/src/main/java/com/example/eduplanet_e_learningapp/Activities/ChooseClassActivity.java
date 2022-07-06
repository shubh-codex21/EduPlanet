package com.example.eduplanet_e_learningapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.eduplanet_e_learningapp.Adapters.ChooseClassAdapter;
import com.example.eduplanet_e_learningapp.Modals.ChooseClass;
import com.example.eduplanet_e_learningapp.databinding.ActivityChooseClassBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseClassActivity extends AppCompatActivity {
    ArrayList<ChooseClass> arrayList;
    ChooseClassAdapter adapter;
    FirebaseFirestore firestore;

    String Class;

    ActivityChooseClassBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        arrayList = new ArrayList<>();
        adapter = new ChooseClassAdapter(arrayList,this);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(gridLayoutManager);

        binding.recyclerView.setHasFixedSize(false);

        binding.recyclerView.setAdapter(adapter);

        addingClasses(arrayList);

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("application", Context.MODE_PRIVATE);
                Class = sharedPref.getString("Class","None");

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("userClass",Class);
                hashMap.put("classChosen",true);

                firestore.collection("User").document(FirebaseAuth.getInstance().getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(ChooseClassActivity.this,IntroQuizActivity.class);

                        if(Class.equals("Class 1") || Class.equals("Class 2") || Class.equals("Class 3") ||
                                Class.equals("Class 4") || Class.equals("Class 5")) {

                            intent.putExtra("ClassLevel",1);

                        }
                        else if (Class.equals("Class 6") || Class.equals("Class 7") || Class.equals("Class 8")){

                            intent.putExtra("ClassLevel",2);

                        }
                        else if (Class.equals("Class 9") || Class.equals("Class 10") || Class.equals("Class 11") ||
                                Class.equals("Class 12")){

                            intent.putExtra("ClassLevel",3);

                        }
                        else {
                            startActivity(new Intent(ChooseClassActivity.this,MainActivity.class));
                        }

                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d("TAG",e.getMessage());
                    }
                });

            }
        });
    }

    private void addingClasses(ArrayList<ChooseClass> arrayList) {
        arrayList.add(new ChooseClass("Class 1"));
        arrayList.add(new ChooseClass("Class 2"));
        arrayList.add(new ChooseClass("Class 3"));
        arrayList.add(new ChooseClass("Class 4"));
        arrayList.add(new ChooseClass("Class 5"));
        arrayList.add(new ChooseClass("Class 6"));
        arrayList.add(new ChooseClass("Class 7"));
        arrayList.add(new ChooseClass("Class 8"));
        arrayList.add(new ChooseClass("Class 9"));
        arrayList.add(new ChooseClass("Class 10"));
        arrayList.add(new ChooseClass("Class 11"));
        arrayList.add(new ChooseClass("Class 12"));
        arrayList.add(new ChooseClass("IIT-JEE"));
        arrayList.add(new ChooseClass("NEET"));
        arrayList.add(new ChooseClass("UPSC"));
        arrayList.add(new ChooseClass("Other"));
    }
}