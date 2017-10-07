package com.example.juanma.riengo.main.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.juanma.riengo.R;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {


    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("OneSignalExample","Yepes lOG!");
        //yepes agregado OneSignal
        OneSignal.startInit(this)
                //.inFocusDisplaying(OneSignal.OSInFocusDisplayOption.InAppAlert)  //in app
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                //.inFocusDisplaying(OneSignal.OSInFocusDisplayOption.None)  //
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        OneSignal.syncHashedEmail("marianoyepes@gmail.com");
        //yepes agregado OneSignal


        //yepes firebase
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_inicio");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name_inicio");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "inicio");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        mFirebaseAnalytics.setUserProperty("favorite_food", "pescado");
        mFirebaseAnalytics.setUserProperty("user_email", "marianoyepes@gmail.com");
        //yepes firebase


        setContentView(R.layout.activity_main);
    }

    public void createBell(View view) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_ejemplo");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name_ejemplo");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        Intent intent = new Intent(this, CreateBellActivity.class);
        startActivity(intent);
    }

    public void listBells(View view) {
        Intent intent = new Intent(this, ListBellsActivity.class);
        startActivity(intent);
    }
}
