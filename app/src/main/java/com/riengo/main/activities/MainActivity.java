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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.common.base.Strings;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.riengo.R;
import com.riengo.main.APISDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.UserBuilder;

public class MainActivity extends FragmentActivity {

    public static String userId = "";
    public static String oneSignaluserId = "";
    public static String userName = "";
    private static String userEmail = "";
    private LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView userNameView;

    //Mejorar que guarde el estado en otro lado más lindo que acá
    private boolean userCreated;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ProfileTracker profileTracker;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("ble","antes de registeronesignal");
        registerOnesignal();
        callbackManager = CallbackManager.Factory.create();
        initializeControls();
        loginWithFB();
        updateUI(null);
        registerFirebase();
        registerSentry();
    }

    private void initializeControls() {
        userNameView = findViewById(R.id.user_name);
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        profilePictureView = findViewById(R.id.user_pic);
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

        Log.i("OneSignalExample", "Yepes lOG!");
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
                System.out.println("userId logueado ONESIGNAL: " + MainActivity.userId);
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

    private void loginWithFB() {

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(final JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());


                                if (Profile.getCurrentProfile() == null) {
                                    profileTracker = new ProfileTracker() {
                                        @Override
                                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                            try {
                                                setUser(currentProfile, object.getString("email"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            new CreateUserOperation().execute();
                                            updateUI(currentProfile);
                                            Toast.makeText(getApplicationContext(), "successfully logged in as " + currentProfile.getFirstName(), Toast.LENGTH_SHORT).show();
                                            profileTracker.stopTracking();

                                            Log.v("facebook - profile", currentProfile.getFirstName());
                                            Log.v("userId logueado FB1", MainActivity.userId);
                                            Log.v("FB1 Email", MainActivity.userEmail);
                                        }
                                    };
                                } else {
                                    Profile profile = Profile.getCurrentProfile();
                                    try {
                                        setUser(profile, object.getString("email"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    updateUI(profile);
                                    new CreateUserOperation().execute();
                                    Toast.makeText(getApplicationContext(), "successfully logged in as " + profile.getFirstName(), Toast.LENGTH_SHORT).show();

                                    Log.v("facebook - profile", profile.getFirstName());
                                    Log.v("userId logueado FB2", MainActivity.userId);
                                    Log.v("FB1 Email", MainActivity.userEmail);
                                }
                            }
                        }
                );
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();

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
            userNameView.setVisibility(View.VISIBLE);
            profilePictureView.setVisibility(View.VISIBLE);
        } else {
            profilePictureView.setProfileId(null);
            userNameView.setText("");
            userNameView.setVisibility(View.GONE);
            profilePictureView.setVisibility(View.GONE);
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

    private void setUser(Profile profile, String email) {
        MainActivity.userId = profile.getId();
        MainActivity.userName = profile.getFirstName()+ " "+profile.getLastName();
        MainActivity.userEmail = email;
    }

    private class CreateUserOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                String mail ="default@default.com";
                String name = "default";
                if (!Strings.isNullOrEmpty(MainActivity.userEmail)) {
                    mail = MainActivity.userEmail;
                    name = MainActivity.userName;
                }
                result = APISDK.createUser(MainActivity.userId,mail, name);
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

}
