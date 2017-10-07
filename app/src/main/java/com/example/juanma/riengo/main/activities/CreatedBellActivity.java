package com.example.juanma.riengo.main.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.juanma.riengo.R;

public class CreatedBellActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_bell);

        Intent intent = getIntent();

        String bell_name = intent.getStringExtra("bell_name");
        String hours_left = intent.getStringExtra("hours_left");

        TextView bell_name_view = (TextView)findViewById(R.id.bell_name);
        TextView hours_left_view = (TextView)findViewById(R.id.hours_left);

        bell_name_view.setText(bell_name);
        hours_left_view.setText(hours_left);
    }
}
