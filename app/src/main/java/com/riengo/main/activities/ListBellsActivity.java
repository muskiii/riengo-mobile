package com.riengo.main.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.riengo.R;
import com.riengo.main.APISDK;
import com.riengo.main.models.Bell;
import com.riengo.main.utils.BellAdapter;
import com.google.common.base.Strings;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ListBellsActivity extends AppCompatActivity {

    String fbId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bells);
        Intent i = getIntent();
        fbId = i.getStringExtra("id");
        if (!Strings.isNullOrEmpty(fbId)) {
            MainActivity.oneSignaluserId = fbId;
        }

        new GetBellsOperation().execute();

    }

    public String readContent() throws IOException {
        String bells  = APISDK.getBellsByOwner();
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

                            String bell_name = item.getValue();
                            String short_URL_view = item.getKey();

                            Intent intent = new Intent(ListBellsActivity.this, ShareBellActivity.class);

                            intent.putExtra("bell_name", bell_name);
                            intent.putExtra("short_URL", short_URL_view);
                            intent.putExtra("shareMsg", "Cuando llegues, avisame con Riengo: " + short_URL_view);

                            startActivity(intent);
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

                        String short_URL_view = item.getKey();

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Cuando llegues, avisame con Riengo: " + short_URL_view);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
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
