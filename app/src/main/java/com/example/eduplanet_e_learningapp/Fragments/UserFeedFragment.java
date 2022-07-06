package com.example.eduplanet_e_learningapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eduplanet_e_learningapp.Adapters.UserFeedAdapter;
import com.example.eduplanet_e_learningapp.Modals.Post;
import com.example.eduplanet_e_learningapp.databinding.FragmentUserFeedBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserFeedFragment extends Fragment {
    ArrayList<Post> posts;
    UserFeedAdapter adapter;

    FirebaseFirestore firestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        posts = new ArrayList<>();
        adapter = new UserFeedAdapter(posts, getContext());

        firestore = FirebaseFirestore.getInstance();
    }

    FragmentUserFeedBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserFeedBinding.inflate(inflater, container, false);

        binding.feedRv.showShimmerAdapter();

        fetchPosts();

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.feedRv.setLayoutManager(manager);
        binding.feedRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        return binding.getRoot();
    }

    private void fetchPosts() {
        firestore.collection("Post")
                .whereEqualTo("postedBy", FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .orderBy("postedAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            posts.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Post modal = snapshot.toObject(Post.class);
                                modal.setPostId(snapshot.getId());
                                posts.add(modal);
                            }
                            binding.feedRv.setAdapter(adapter);
                            binding.feedRv.hideShimmerAdapter();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

}