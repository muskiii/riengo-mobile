package com.example.juanma.riengo.main.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.TextView;

import com.example.juanma.riengo.R;
import com.example.juanma.riengo.main.APISDK;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import java.io.IOException;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;

public class MainActivity extends FragmentActivity {
    CallbackManager callbackManager;
    LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private String id ;
    //Mejorar que guarde el estado en otro lado más lindo que acá
    private boolean userCreated;


    private FirebaseAnalytics mFirebaseAnalytics;

    public String userEmail = "marianoyepes@gmail.com";
    public static String onesignalPlayerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerOnesignal();
        registerFirebase();
        registerSentry();

        setContentView(R.layout.activity_main);
        initializeControls();
        updateUI();
        loginWithFB();
    }
    private void initializeControls(){
        callbackManager = CallbackManager.Factory.create();
        userNameView = (TextView) findViewById(R.id.user_name);
        loginButton = findViewById(R.id.login_button);
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
                onesignalPlayerId = userId;
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
                updateUI();
                id = Profile.getCurrentProfile().getId();

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
                    .setText(String.format("%s %s", profile.getFirstName(), profile.getLastName()));
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
                id = "";

            }
        }
    };
    public void createBell(View view) {
        Intent intent = new Intent(this, CreateBellActivity.class);
        intent.putExtra("id", id);

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
                if(!userCreated){
                    result = APISDK.createUser(onesignalPlayerId,"email@gmail.com","facebookId","Yepeto");
                    userCreated=true;
                }
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

}
