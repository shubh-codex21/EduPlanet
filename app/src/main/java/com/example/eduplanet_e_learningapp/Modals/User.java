package com.example.eduplanet_e_learningapp.Modals;

public class User {
    String username, userEmail, userPassword, userImage, userClass, signUpProvider;
    Boolean isClassChosen, isIntroQuizTaken;
    int noOfPost;

    public User() {
    }

    public User(String userName, String userEmail, String userPassword, String userImage) {
        this.username = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public String getSignUpProvider() {
        return signUpProvider;
    }

    public void setSignUpProvider(String signUpProvider) {
        this.signUpProvider = signUpProvider;
    }

    public Boolean getClassChosen() {
        return isClassChosen;
    }

    public void setClassChosen(Boolean classChosen) {
        this.isClassChosen = classChosen;
    }

    public Boolean getIntroQuizTaken() {
        return isIntroQuizTaken;
    }

    public void setIntroQuizTaken(Boolean introQuizTaken) {
        this.isIntroQuizTaken = introQuizTaken;
    }

    public int getNoOfPost() {
        return noOfPost;
    }

    public void setNoOfPost(int noOfPost) {
        this.noOfPost = noOfPost;
    }
}
