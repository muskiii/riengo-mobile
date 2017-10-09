package com.riengo.main.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.riengo.R;
import com.riengo.main.APISDK;

import java.io.IOException;

public class CreateBellActivity extends AppCompatActivity {

    String bellName = "";
    String expireTime = "";
    Switch switchButton;
    LinearLayout expireBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bell);
        Intent i = getIntent();
        switchButton = (Switch) findViewById(R.id.switchexpires);
        expireBox = (LinearLayout) findViewById(R.id.expireBox);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expireBox.getVisibility() == View.GONE )
                    expireBox.setVisibility(View.VISIBLE);
                else
                    expireBox.setVisibility(View.GONE);
            }
        });
    }

    public void createBell(View view) {
        Intent intent = new Intent(this, CreatedBellActivity.class);

        EditText bell_name_edit = (EditText)findViewById(R.id.bell_name);
        EditText hours_left_edit = (EditText)findViewById(R.id.hours_left);
        bellName = bell_name_edit.getText().toString();
        expireTime = hours_left_edit.getText().toString();
        intent.putExtra("bell_name", bell_name_edit.getText().toString());
        intent.putExtra("hours_left", hours_left_edit.getText().toString());

        new CreateBellsOperation().execute();
    }

    public String postContent() throws IOException {
        String bells  = APISDK.createBell(bellName,expireTime);
        return bells;
    }


    private class CreateBellsOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = APISDK.createBell(bellName,expireTime);
                Log.i("ApiCallResult",result);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),"New bell created!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateBellActivity.this, ListBellsActivity.class);
            startActivity(intent);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
