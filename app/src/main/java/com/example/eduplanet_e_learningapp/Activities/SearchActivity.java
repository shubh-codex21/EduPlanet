package com.example.eduplanet_e_learningapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.eduplanet_e_learningapp.Adapters.SearchUsersAdapter;
import com.example.eduplanet_e_learningapp.Modals.Doubt;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.ActivitySearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    List<User> users;
    SearchUsersAdapter adapter;

    ActivitySearchBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        adapter = new SearchUsersAdapter(users,this);

        fetchUsers();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.usersRv.setLayoutManager(manager);
        binding.usersRv.setAdapter(adapter);

        binding.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchView.requestFocus();

            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.searchView.clearFocus();
                binding.searchView.setQueryHint("");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

    }

    private void filterList(String text) {
        List<User> filteredList = new ArrayList<>();
        for (User item: users){
            if (item.getUsername().toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()){
            binding.usersRv.setVisibility(View.GONE);
            binding.nothingTv.setVisibility(View.VISIBLE);
        } else {
            binding.nothingTv.setVisibility(View.GONE);
            binding.usersRv.setVisibility(View.VISIBLE);
            adapter.setFilteredList(filteredList);
        }
    }

    private void fetchUsers() {
        firestore.collection("User").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    users.clear();
                    for (DocumentSnapshot snapshot: value.getDocuments()){
                        if (!snapshot.getId().equals(auth.getUid())) {
                            User user = snapshot.toObject(User.class);
                            user.setUserId(snapshot.getId());
                            users.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}