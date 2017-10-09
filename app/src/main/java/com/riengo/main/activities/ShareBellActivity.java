package com.riengo.main.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.riengo.R;

public class ShareBellActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_bell);

        Intent intent = getIntent();

        String bell_name = intent.getStringExtra("bell_name");
        String short_URL = intent.getStringExtra("short_URL");

        TextView bell_name_view = (TextView)findViewById(R.id.bell_name);
        EditText short_URL_view = (EditText) findViewById(R.id.short_URL);

        bell_name_view.setText(bell_name);
        short_URL_view.setText(short_URL);
    }

    public void shareURL(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("shareMsg"));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
