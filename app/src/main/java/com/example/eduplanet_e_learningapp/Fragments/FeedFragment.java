package com.example.eduplanet_e_learningapp.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Activities.CreatePostActivity;
import com.example.eduplanet_e_learningapp.Adapters.PostsAdapter;
import com.example.eduplanet_e_learningapp.Adapters.StoryAdapter;
import com.example.eduplanet_e_learningapp.Modals.Post;
import com.example.eduplanet_e_learningapp.Modals.Story;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.Modals.UserStories;
import com.example.eduplanet_e_learningapp.databinding.FragmentFeedBinding;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FeedFragment extends Fragment {
    ArrayList<Post> postArrayList;
    ArrayList<Story> storyArrayList;
    PostsAdapter postsAdapter;
    StoryAdapter storyAdapter;

    FirebaseFirestore firestore;
    FirebaseStorage storage;
    FirebaseAuth auth;
    ActivityResultLauncher<String> galleryLauncher;
    ProgressDialog storyUploadDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        postArrayList = new ArrayList<>();
        postsAdapter = new PostsAdapter(postArrayList, getContext());

        storyArrayList = new ArrayList<>();
        storyAdapter = new StoryAdapter(storyArrayList, getContext());

        storyUploadDialog = new ProgressDialog(getContext());

    }

    FragmentFeedBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater, container, false);

        binding.postRecyclerView.showShimmerAdapter();
        binding.storiesRv.showShimmerAdapter();

        fetchPosts();
        fetchStories();

        storyUploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        storyUploadDialog.setTitle("Story Uploading");
        storyUploadDialog.setMessage("Please wait...");
        storyUploadDialog.setCancelable(false);

        // Story Recycler View
        LinearLayoutManager manager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.storiesRv.setLayoutManager(manager1);
        binding.storiesRv.setAdapter(storyAdapter);
        storyAdapter.notifyDataSetChanged();

        // Post Recycler View
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.postRecyclerView.setLayoutManager(manager2);
        binding.postRecyclerView.setAdapter(postsAdapter);
        postsAdapter.notifyDataSetChanged();

        binding.createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreatePostActivity.class));
            }
        });

        binding.addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryLauncher.launch("image/*");
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        storyUploadDialog.show();
                        final StorageReference reference = storage.getReference().child("Stories").child(auth.getUid())
                                .child(new Date().getTime() + "");

                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Story story = new Story();
                                story.setStoryAt(new Date().getTime());

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("postedBy", story.getStoryAt());

                                        firestore.collection("Story").document(auth.getUid()).set(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        UserStories stories = new UserStories(uri.toString(), story.getStoryAt());

                                                        String docId = firestore.collection("Story").document(auth.getUid())
                                                                .collection("userStories").document().getId();

                                                        firestore.collection("Story").document(auth.getUid())
                                                                .collection("userStories").document(docId).set(stories)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        storyUploadDialog.dismiss();

                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        });
                        storyAdapter.notifyDataSetChanged();
                    }
                });

        return binding.getRoot();
    }

    private void fetchPosts() {

        firestore.collection("Post").orderBy("postedAt", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (!value.isEmpty()) {
                            postArrayList.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Post modal = snapshot.toObject(Post.class);
                                modal.setPostId(snapshot.getId());
                                postArrayList.add(modal);
                            }
                            binding.postRecyclerView.setAdapter(postsAdapter);
                            binding.postRecyclerView.hideShimmerAdapter();
                            postsAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    private void fetchStories() {
        firestore.collection("User").document(auth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                if (value.exists()) {
                    User user = value.toObject(User.class);
                    Glide.with(getContext().getApplicationContext()).load(user.getUserImage()).into(binding.imageView);
                }
            }
        });

        firestore.collection("Story").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (!value.isEmpty()) {
                    storyArrayList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Story story = new Story();
                        story.setStoryBy(snapshot.getId());
                        story.setStoryAt(snapshot.getLong("postedBy"));

                        ArrayList<UserStories> Stories = new ArrayList<>();
                        snapshot.getReference().collection("userStories")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                                        for (DocumentSnapshot snapshot1 : value.getDocuments()) {
                                            UserStories userStories = snapshot1.toObject(UserStories.class);
                                            Stories.add(userStories);
                                        }
                                    }
                                });
                        story.setStories(Stories);
                        storyArrayList.add(story);
                    }
                    binding.storiesRv.setAdapter(storyAdapter);
                    binding.storiesRv.hideShimmerAdapter();
                    storyAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}