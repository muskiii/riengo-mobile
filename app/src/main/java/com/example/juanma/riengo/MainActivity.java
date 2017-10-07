package com.example.juanma.riengo;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

public class MainActivity extends FragmentActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView userNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeControls();
        loginWithFB();
    }
    private void initializeControls(){
        callbackManager = CallbackManager.Factory.create();
        userNameView = (TextView) findViewById(R.id.user_name);
        loginButton = findViewById(R.id.login_button);
        profilePictureView =  findViewById(R.id.user_pic);
        profilePictureView.setCropped(true);
    }

    private void loginWithFB(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                updateUI();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        });

    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void updateUI() {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            profilePictureView.setProfileId(profile.getId());
            userNameView
                    .setText(String.format("%s %s",profile.getFirstName(), profile.getLastName()));
        } else {
            profilePictureView.setProfileId(null);
            userNameView.setText("welcome");
        }
    }
    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                updateUI();
                //write your code here what to do when user logout
            }
        }
    };
}
