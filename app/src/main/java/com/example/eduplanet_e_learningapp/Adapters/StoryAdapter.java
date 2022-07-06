package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Modals.Story;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.Modals.UserStories;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.StoryRvItemviewBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    ArrayList<Story> stories;
    Context context;

    public StoryAdapter(ArrayList<Story> stories, Context context) {
        this.stories = stories;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.story_rv_itemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Story story = stories.get(position);


//        if (story.getStories().size() != 0) {
//            UserStories lastStory = story.getStories().get(story.getStories().size() - 1);
//            Glide.with(context).load(lastStory.getImage()).into(holder.binding.storyImage);
//            holder.binding.storiesCircle.setPortionsCount(story.getStories().size());
//        }

            FirebaseFirestore.getInstance().collection("User").document(story.getStoryBy()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                    if (value.exists()) {
                        holder.binding.storiesCircle.setPortionsCount(story.getStories().size());
                        User user = value.toObject(User.class);
                        holder.binding.storyUserName.setText(user.getUsername());
                        Glide.with(context).load(user.getUserImage()).into(holder.binding.storyImage);

                        holder.binding.storyImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<MyStory> myStories = new ArrayList<>();

                                for (UserStories stories : story.getStories()) {
                                    myStories.add(new MyStory(
                                            stories.getImage(),
                                            new Date(stories.getStoryAt())
                                    ));
                                }

                                new StoryView.Builder(((AppCompatActivity) context).getSupportFragmentManager())
                                        .setStoriesList(myStories) // Required
                                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                        .setTitleText(user.getUsername()) // Default is Hidden
                                        .setSubtitleText("") // Default is Hidden
                                        .setTitleLogoUrl(user.getUserImage()) // Default is Hidden
                                        .setStoryClickListeners(new StoryClickListeners() {
                                            @Override
                                            public void onDescriptionClickListener(int position) {
                                                //your action
                                            }

                                            @Override
                                            public void onTitleIconClickListener(int position) {
                                                //your action
                                            }
                                        }) // Optional Listeners
                                        .build() // Must be called before calling show method
                                        .show();
                            }
                        });

                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        StoryRvItemviewBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StoryRvItemviewBinding.bind(itemView);
        }
    }
}
