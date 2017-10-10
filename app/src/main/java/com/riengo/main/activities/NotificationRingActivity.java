package com.riengo.main.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.riengo.R;

public class NotificationRingActivity extends AppCompatActivity {

    TextView txtBellName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_ring);
        Intent i = getIntent();
        String bellName = i.getStringExtra("bellName");
        txtBellName = (TextView)  (TextView)findViewById(R.id.bellName);
        txtBellName.setText(bellName);

    }
}
