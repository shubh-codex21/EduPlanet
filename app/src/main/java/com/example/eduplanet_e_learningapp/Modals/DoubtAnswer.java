package com.example.eduplanet_e_learningapp.Modals;

import java.util.ArrayList;

public class DoubtAnswer {
    String answerId, answer, answeredBy, doubtId;
    int likes, noOfAnswerAttachments;
    long answeredAt;

    ArrayList<DoubtAttachments> answerAttachments;

    public DoubtAnswer() {
    }

    public DoubtAnswer(String answer, String answeredBy, long answeredAt) {
        this.answer = answer;
        this.answeredBy = answeredBy;
        this.answeredAt = answeredAt;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnsweredBy() {
        return answeredBy;
    }

    public void setAnsweredBy(String answeredBy) {
        this.answeredBy = answeredBy;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public long getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(long answeredAt) {
        this.answeredAt = answeredAt;
    }

    public ArrayList<DoubtAttachments> getAnswerAttachments() {
        return answerAttachments;
    }

    public void setAnswerAttachments(ArrayList<DoubtAttachments> answerAttachments) {
        this.answerAttachments = answerAttachments;
    }

    public int getNoOfAnswerAttachments() {
        return noOfAnswerAttachments;
    }

    public void setNoOfAnswerAttachments(int noOfAnswerAttachments) {
        this.noOfAnswerAttachments = noOfAnswerAttachments;
    }

    public String getDoubtId() {
        return doubtId;
    }

    public void setDoubtId(String doubtId) {
        this.doubtId = doubtId;
    }
}
