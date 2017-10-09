package com.riengo.main.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.riengo.R;
import com.riengo.main.APISDK;

import java.io.IOException;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.UserBuilder;

public class MainActivity extends FragmentActivity {

    private LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView userNameView;

    //Mejorar que guarde el estado en otro lado más lindo que acá
    private boolean userCreated;

    private FirebaseAnalytics mFirebaseAnalytics;

    public String userEmail = "marianoyepes@gmail.com";
    public static String userId = "";
    public static String oneSignaluserId = "";
    public static String fbId = "";
    public static String userName = "";

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                   AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                updateUI(null);
                userId = MainActivity.oneSignaluserId;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeControls();
        updateUI(null);
        registerOnesignal();
        registerFirebase();
        registerSentry();

        loginWithFB();
    }
    private void initializeControls(){
        callbackManager = CallbackManager.Factory.create();
        userNameView = (TextView) findViewById(R.id.user_name);
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        profilePictureView =  findViewById(R.id.user_pic);
        profilePictureView.setCropped(true);
    }

    private void registerSentry() {
        android.content.Context ctx = this.getApplicationContext();
        // Use the Sentry DSN (client key) from the Project Settings page on Sentry
        String sentryDsn = "https://db547300fe134efdb233940b72cc3500:6a58d01189124e10b2812c84ac45ab34@sentry.io/227253";
        Sentry.init(sentryDsn, new AndroidSentryClientFactory(ctx));

        // Set the user in the current context.
        Sentry.getContext().setUser(
                new UserBuilder().setEmail(userEmail).build()
        );

        try {
            throw new UnsupportedOperationException("Sentry yepes Test!");
        } catch (Exception e) {
            // This sends an exception event to Sentry using the statically stored instance
            // that was created in the ``main`` method.
            Sentry.capture(e);
        }
    }

    private void registerOnesignal() {

        Log.i("OneSignalExample","Yepes lOG!");
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        OneSignal.syncHashedEmail(userEmail);

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.i("debug", "User: " + userId);
                MainActivity.oneSignaluserId = userId;
                MainActivity.userId = userId;
                System.out.println("userId logueado ONESIGNAL: "+MainActivity.userId);
                new CreateUserOperation().execute();
                if (registrationId != null)
                    Log.i("debug", "registrationId:" + registrationId);
            }
        });
    }

    private void registerFirebase() {
        //yepes firebase
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_inicio");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name_inicio");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "inicio");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mFirebaseAnalytics.setUserProperty("favorite_food", "pescado");
        mFirebaseAnalytics.setUserProperty("user_email", userEmail);
        //yepes firebase
    }

    private void loginWithFB(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            Log.v("facebook - profile", currentProfile.getFirstName());
                            setUser(currentProfile);
                            System.out.println("userId logueado FB1: "+MainActivity.userId);
                            new CreateUserOperation().execute();
                            updateUI(currentProfile);
                            Toast.makeText(getApplicationContext(),"successfully logged in as " + currentProfile.getFirstName(),Toast.LENGTH_SHORT).show();
                            profileTracker.stopTracking();
                        }
                    };
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    setUser(profile);
                    updateUI(profile);
                    new CreateUserOperation().execute();

                    Toast.makeText(getApplicationContext(),"successfully logged in as " + profile.getFirstName(),Toast.LENGTH_SHORT).show();
                    Log.v("facebook - profile", profile.getFirstName());
                    Log.v("userId logueado FB2: ",MainActivity.userId);
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
    private void updateUI(Profile profile) {
        if (profile != null) {
            profilePictureView.setProfileId(profile.getId());
            userNameView
                    .setText(String.format("%s %s", profile.getFirstName(), profile.getLastName()));
        } else {
            profilePictureView.setProfileId(null);
            userNameView.setText("");
            userNameView.setVisibility(View.GONE);
        }
    }
    public void createBell(View view) {
        Intent intent = new Intent(this, CreateBellActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_ejemplo");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name_ejemplo");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        startActivity(intent);
    }

    public void listBells(View view) {
        Intent intent = new Intent(this, ListBellsActivity.class);
        startActivity(intent);
    }


    private class CreateUserOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = APISDK.createUser(MainActivity.oneSignaluserId,"email@gmail.com",MainActivity.fbId,MainActivity.userName);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void setUser(Profile profile){
        MainActivity.userId = profile.getId();
        MainActivity.fbId = profile.getId();
        MainActivity.userName = profile.getId();
    }

}
