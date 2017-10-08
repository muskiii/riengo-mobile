package com.example.juanma.riengo.main.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.juanma.riengo.R;

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
}
