package com.example.juanma.riengo.main.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.juanma.riengo.R;

public class CreateBellActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bell);
        Intent i = getIntent();
        TextView user_id = (TextView)findViewById(R.id.user_id);
        user_id.setText(i.getStringExtra("id"));
    }

    public void createBell(View view) {
        Intent intent = new Intent(this, CreatedBellActivity.class);

        EditText bell_name_edit = (EditText)findViewById(R.id.bell_name);
        EditText hours_left_edit = (EditText)findViewById(R.id.hours_left);





        intent.putExtra("bell_name", bell_name_edit.getText().toString());
        intent.putExtra("hours_left", hours_left_edit.getText().toString());

        startActivity(intent);
    }
}
