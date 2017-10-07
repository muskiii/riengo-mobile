package com.example.juanma.riengo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //yepes agregado OneSignal
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        OneSignal.syncHashedEmail("marianoyepes@gmail.com");
        //yepes agregado OneSignal


        setContentView(R.layout.activity_main);
    }

    public void createBell(View view) {
        Intent intent = new Intent(this, CreateBellActivity.class);
        startActivity(intent);
    }

    public void listBells(View view) {
        Intent intent = new Intent(this, ListBellsActivity.class);
        startActivity(intent);
    }
}
