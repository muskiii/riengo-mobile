package com.example.juanma.riengo.main.models;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Bell {
    public String name;
    public DateTime creation_time;
    public DateTime expiration_time;

    public static List<Bell> parseBells(String responseBells) throws JSONException {
        JSONArray bellsArray = new JSONArray(responseBells);
        List<Bell> bellsList = Lists.newArrayList();
        for (int i = 0; i < bellsArray.length(); i++) {
            JSONObject bellJson = bellsArray.getJSONObject(i);
            Bell bell = new Bell();
            bell.name = bellJson.get("name").toString();
            bell.creation_time = new DateTime(bellJson.get("creation_time").toString());
            bell.expiration_time = new DateTime(bellJson.get("expiration_time").toString());
            bellsList.add(bell);
        }
        return bellsList;
    }
}
