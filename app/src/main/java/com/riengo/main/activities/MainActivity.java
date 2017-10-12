package com.riengo.main.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.riengo.R;
import com.riengo.main.APISDK;
import com.riengo.main.models.RiengoUser;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Arrays;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.event.UserBuilder;

public class MainActivity extends FragmentActivity {
    public static RiengoUser riengoUser;
    private LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private ProfileTracker profileTracker;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restoreSession();
        registerOnesignal();

        callbackManager = CallbackManager.Factory.create();
        initializeControls();
        registerFacebookLogin();
        registerFirebase();
        registerSentry();
        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        OneSignal.syncHashedEmail(MainActivity.riengoUser.getUserEmail());

    }

    private void restoreSession() {
        Log.i("restoreSession", "Aspen Log");
        SharedPreferences sharedPref= getSharedPreferences("riengoPref", 0);
        if (sharedPref != null) {
            MainActivity.riengoUser  = new RiengoUser(sharedPref.getString("oneSignalUserId", "NOT_FOUND"));
            if(sharedPref.getBoolean("facebookOn", false)) {
                MainActivity.riengoUser.setUserName(sharedPref.getString("name", "NOT_FOUND"));
                MainActivity.riengoUser.setFbId(sharedPref.getString("fbId", "NOT_FOUND"));
                MainActivity.riengoUser.setUserEmail(sharedPref.getString("email", "NOT_FOUND"));
                MainActivity.riengoUser.setFacebookOn(sharedPref.getBoolean("facebookOn", true));
            }
        }
        Log.i("XOX",MainActivity.riengoUser.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("initializeControls", "Aspen Log");
        SharedPreferences sharedPref = getSharedPreferences("riengoPref", 0);
        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString("oneSignalUserId", MainActivity.riengoUser.getOneSignaluserId());
        if (MainActivity.riengoUser.isFacebookOn()) {
            editor.putBoolean("facebookOn", MainActivity.riengoUser.isFacebookOn());
            editor.putString("fbId", MainActivity.riengoUser.getFbId());
            editor.putString("name", MainActivity.riengoUser.getUserName());
            editor.putString("email", MainActivity.riengoUser.getUserEmail());
        }
        editor.commit();
    }

    private void initializeControls() {
        Log.i("initializeControls", "Aspen Log");
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
                new UserBuilder().setEmail(MainActivity.riengoUser.getUserEmail()).build());

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
                .setNotificationOpenedHandler(new NotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if (MainActivity.riengoUser == null) {
                    MainActivity.riengoUser = new RiengoUser(userId);
                    new CreateUserOperation().execute();
                    if (registrationId != null) {
                        Log.i("debug", "registrationId:" + registrationId);
                    }
                }else if(MainActivity.riengoUser.getOneSignaluserId() != userId){
                    MainActivity.riengoUser.setOneSignaluserId(userId);
                }
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
        mFirebaseAnalytics.setUserProperty("user_email", "Riengo@firebase");
        //yepes firebase
    }

    private void registerFacebookLogin() {

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("Facebook onSuccess", "Aspen Log");

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(final JSONObject object, GraphResponse response) {
                                Log.i("Facebook onComplete", "Aspen Log");
                                if (Profile.getCurrentProfile() == null) {
                                    profileTracker = new ProfileTracker() {
                                        @Override
                                        protected void onCurrentProfileChanged(Profile oldProfile,
                                                                               Profile currentProfile) {
                                            try {
                                                setRiengoUser(currentProfile, object.getString("email"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            new CreateUserOperation().execute();
                                            updateUI();
                                            Toast.makeText(getApplicationContext(),
                                                    "successfully logged in as "
                                                            + currentProfile.getFirstName(),
                                                    Toast.LENGTH_SHORT).show();
                                            profileTracker.stopTracking();
                                            Log.v("Facebook - profile", riengoUser.getUserName());
                                            Log.v("Facebook - id", riengoUser.getFbId());
                                            Log.v("Facebook - Email", riengoUser.getUserEmail());
                                        }
                                    };
                                } else {
                                    Profile profile = Profile.getCurrentProfile();
                                    try {
                                        setRiengoUser(profile, object.getString("email"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    new CreateUserOperation().execute();
                                    updateUI();
                                    Toast.makeText(getApplicationContext(),
                                            "successfully logged in as " + profile.getFirstName(),
                                            Toast.LENGTH_SHORT).show();

                                    Log.v("facebook - profile", riengoUser.getUserName());
                                    Log.v("userId logueado FB2", riengoUser.getFbId());
                                    Log.v("FB1 Email", riengoUser.getUserEmail());
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

    private void updateUI() {
        if (riengoUser.isFacebookOn()) {
            Log.i("updateUI SET", "Aspen Log");
            profilePictureView.setProfileId(riengoUser.getFbId());
            userNameView.setText(riengoUser.getUserName());
            userNameView.setVisibility(View.VISIBLE);
            profilePictureView.setVisibility(View.VISIBLE);
        } else {
            Log.i("updateUI UnSET", "Aspen Log");
            profilePictureView.setProfileId(null);
            userNameView.setText(MainActivity.riengoUser.getUserName());
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

    private void setRiengoUser(Profile profile, String email) {
        MainActivity.riengoUser.setFbId(profile.getId());
        MainActivity.riengoUser.setUserName(profile.getFirstName()+ " "+profile.getLastName());
        MainActivity.riengoUser.setUserEmail(email);
        MainActivity.riengoUser.setFacebookOn(true);
    }

    private class CreateUserOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = APISDK.createUser(riengoUser.getOneSignaluserId(),
                        riengoUser.getCurrentUser(),riengoUser.getUserEmail(),
                        riengoUser.getUserName());
                Log.i("---->API SDK call", result);
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
            Log.i("TokenChanged", "Aspen Log");
            SharedPreferences sharedPref = getSharedPreferences("riengoPref",0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.putString("oneSignalUserId", MainActivity.riengoUser.getOneSignaluserId());
            editor.commit();
            if (currentAccessToken == null) {
                Log.i("Facebook Logout", "Aspen Log");
                MainActivity.riengoUser.reset();
                updateUI();
            }
        }
    };

    public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String activityToBeOpened;
            String bellName = data.optString("bellName", null);
            System.out.println(bellName);
            Intent intent = new Intent(MainActivity.this, NotificationRingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bellName",bellName);
            startActivity(intent);

        }
    }
}
