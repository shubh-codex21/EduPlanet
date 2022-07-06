package com.example.eduplanet_e_learningapp.Modals;

public class UserStories {
    String image;
    long storyAt;

    public UserStories() {
    }

    public UserStories(String image, long storyAt) {
        this.image = image;
        this.storyAt = storyAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}
