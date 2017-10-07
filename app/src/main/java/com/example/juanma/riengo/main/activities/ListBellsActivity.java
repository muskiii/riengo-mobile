package com.example.juanma.riengo.main.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.juanma.riengo.R;
import com.example.juanma.riengo.main.APISDK;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListBellsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bells);
        new GetBellsOperation().execute();
    }


    public String readContent() throws IOException {

        String bells  = APISDK.getBellsByOwner("owner");
        return bells;

    }

    private class GetBellsOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            //return readContent();
            try {
                return readContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  return postContent(params[0]);
            return "error";
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
