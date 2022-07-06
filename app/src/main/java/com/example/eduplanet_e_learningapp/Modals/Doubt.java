package com.example.eduplanet_e_learningapp.Modals;


import java.util.ArrayList;

public class Doubt {
    String doubtId, title, description, doubtedBy;
    int upvotes, answers;
    long doubtedAt;
    int noOfAttachments;
    ArrayList<DoubtAttachments> attachments;

    public Doubt() {

    }

    public Doubt(String title, String description, int noOfAttachments ,long doubtedAt) {
        this.title = title;
        this.description = description;
        this.doubtedAt = doubtedAt;
        this.noOfAttachments = noOfAttachments;
    }

    public String getDoubtId() {
        return doubtId;
    }

    public void setDoubtId(String doubtId) {
        this.doubtId = doubtId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDoubtedBy() {
        return doubtedBy;
    }

    public void setDoubtedBy(String doubtedBy) {
        this.doubtedBy = doubtedBy;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getAnswers() {
        return answers;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public long getDoubtedAt() {
        return doubtedAt;
    }

    public void setDoubtedAt(long doubtedAt) {
        this.doubtedAt = doubtedAt;
    }

    public ArrayList<DoubtAttachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<DoubtAttachments> attachments) {
        this.attachments = attachments;
    }

    public int getNoOfAttachments() {
        return noOfAttachments;
    }

    public void setNoOfAttachments(int noOfAttachments) {
        this.noOfAttachments = noOfAttachments;
    }
}
