package com.example.juanma.riengo.main.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.juanma.riengo.R;
import com.example.juanma.riengo.main.APISDK;
import com.example.juanma.riengo.main.models.Bell;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListBellsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("testas");
        setContentView(R.layout.activity_list_bells);
        System.out.println("test");
        new TestAPIOperation().execute();

    }

    public String readContent() throws IOException {

        String bells  = APISDK.getBellsByOwner("owner");
        return bells;

    }

    private class GetBellsOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = readContent();
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
                try {
                    List<Bell> bellList = null;
                    bellList = Bell.parseBells(result);
                    List<String> bellsNames = Bell.bellsToListString(bellList);
                    ListView bellsListView = (ListView) findViewById(R.id.bellsListView);

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            ListBellsActivity.this,
                            android.R.layout.simple_list_item_1,
                            bellsNames );
                    bellsListView.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }




    private class TestAPIOperation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                result = APISDK.testApi();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            List<Bell> bellList = null;
            try {
                bellList = Bell.parseBells(result);
                List<String> bellsNames = Bell.bellsToListString(bellList);
                ListView bellsListView = (ListView) findViewById(R.id.bellsListView);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        ListBellsActivity.this,
                        android.R.layout.simple_list_item_1,
                        bellsNames );
                bellsListView.setAdapter(arrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }



}
