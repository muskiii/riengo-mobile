package com.example.juanma.riengo.main.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juanma.riengo.R;
import com.example.juanma.riengo.main.APISDK;
import com.example.juanma.riengo.main.models.Bell;
import com.example.juanma.riengo.main.utils.BellAdapter;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListBellsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("testas");
        setContentView(R.layout.activity_list_bells);
        System.out.println("test");
        new GetBellsOperation().execute();

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
                Map<String, String> bells = Bell.bellsToMap(bellList);
                final ListView bellsListView = findViewById(R.id.bellsListView);

                BellAdapter bellAdapter = new BellAdapter(bells);
                bellsListView.setAdapter(bellAdapter);

                bellsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Map.Entry<String, String> item = (Map.Entry<String, String>) bellsListView.getItemAtPosition(position);
                        System.out.println("name " + item.getValue());
                        System.out.println("shortenURL " + item.getKey());
                    }
                });
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
