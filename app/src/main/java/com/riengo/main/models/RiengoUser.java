package com.riengo.main.models;

import com.facebook.Profile;
import com.riengo.main.activities.MainActivity;

/**
 * Created by aspen on 11/10/17.
 */

public final class RiengoUser {
    private String currentUser;
    private String fbId;
    private String oneSignaluserId;
    private String userName;
    private String userEmail;
    private boolean facebookOn;

    public RiengoUser(String oneSignaluserId) {
        this.fbId = "";
        this.userName = "Riengo";
        this.currentUser = oneSignaluserId;
        this.oneSignaluserId = oneSignaluserId;
        this.userEmail = "Aspen@Test";
        this.facebookOn = false;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.currentUser = fbId;
        this.fbId = fbId;
        this.facebookOn = true;
    }

    public String getOneSignaluserId() {
        return oneSignaluserId;
    }

    public void setOneSignaluserId(String oneSignaluserId) {
        this.oneSignaluserId = oneSignaluserId;
        this.currentUser = oneSignaluserId;
        this.facebookOn = false;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isFacebookOn() {
        return facebookOn;
    }

    public void setFacebookOn(boolean facebookOn) {
        if (facebookOn){
            currentUser = fbId;
        }else {
            currentUser = oneSignaluserId;
        }
        this.facebookOn = facebookOn;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    @Override
    public String toString() {
        return "RiengoUser{" +
                "currentUser='" + currentUser + '\'' +
                ", fbId='" + fbId + '\'' +
                ", oneSignaluserId='" + oneSignaluserId + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", facebookOn=" + facebookOn +
                '}';
    }

    public void reset() {
        this.fbId = "";
        this.userName = "Riengo";
        this.currentUser = oneSignaluserId;
        this.userEmail = "Aspen@Test";
        this.facebookOn = false;
    }
    public void fullReset(String oneSignaluserId) {
        this.fbId = "";
        this.userName = "Riengo";
        this.oneSignaluserId = oneSignaluserId;
        this.currentUser = oneSignaluserId;
        this.userEmail = "Aspen@Test";
        this.facebookOn = false;
    }
    public void addFacebook(Profile profile, String email) {
        MainActivity.riengoUser.setFbId(profile.getId());
        MainActivity.riengoUser.setUserName(profile.getFirstName()+ " "+profile.getLastName());
        MainActivity.riengoUser.setUserEmail(email);
    }
}