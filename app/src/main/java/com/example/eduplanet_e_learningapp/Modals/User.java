package com.example.eduplanet_e_learningapp.Modals;

public class User {
    String userId ,username, userEmail, userPassword, userImage, userCover ,userClass, userBio, signUpProvider,
            location, countryCode, dateOfBirth, link;
    Boolean isClassChosen, isIntroQuizTaken, isProfileCreated;
    int noOfPost, noOfDoubts, followers, following;

    public User() {

    }

    public User(String userName, String userEmail, String userPassword, String userImage) {
        this.username = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userImage = userImage;
    }

    public User(String username, String userEmail, String userPassword, String userImage, String userCover, String userClass,
                String userBio, String location, String countryCode, String dateOfBirth, String link, int noOfPost, int noOfDoubts,
                int followers, int following) {
        this.username = username;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userImage = userImage;
        this.userCover = userCover;
        this.userClass = userClass;
        this.userBio = userBio;
        this.location = location;
        this.countryCode = countryCode;
        this.dateOfBirth = dateOfBirth;
        this.link = link;
        this.noOfPost = noOfPost;
        this.noOfDoubts = noOfDoubts;
        this.followers = followers;
        this.following = following;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getNoOfDoubts() {
        return noOfDoubts;
    }

    public void setNoOfDoubts(int noOfDoubts) {
        this.noOfDoubts = noOfDoubts;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getProfileCreated() {
        return isProfileCreated;
    }

    public void setProfileCreated(Boolean profileCreated) {
        isProfileCreated = profileCreated;
    }

    public String getUserCover() {
        return userCover;
    }

    public void setUserCover(String userCover) {
        this.userCover = userCover;
    }
}
