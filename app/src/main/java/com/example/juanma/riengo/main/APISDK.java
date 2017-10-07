package com.example.juanma.riengo.main;

import android.util.Base64;

import com.example.juanma.riengo.main.activities.StreamUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

/**
 * Created by rakuraku on 07/10/17.
 */

public class APISDK {
    private static String createJwtToken() throws UnsupportedEncodingException {
        String key = "client";
        byte[] data = key.getBytes("UTF-8");
        String keyB64 = Base64.encodeToString(data, Base64.DEFAULT);

        String token = Jwts.builder().setIssuer("riengo-mobile").signWith(SignatureAlgorithm.HS256, keyB64).compact();
        return token;
    }

    public static String testApi() throws IOException {
        String token = createJwtToken();
        URL url = new URL("https://riengo-api.herokuapp.com/v1/bell");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setChunkedStreamingMode(0);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty ("Authorization", "Bearer "+token);
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        String result = StreamUtils.readStream(in,1024);
        System.out.println("resultado api: "+result);
        return result;

    }
    public static String getBellsByOwner(String idOwner) throws IOException {
        URL url = new URL("https://riengo-api.herokuapp.com/v1/ownerbells/"+"[hash]");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String token = createJwtToken();

        urlConnection.setDoOutput(true);
        urlConnection.setChunkedStreamingMode(0);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty ("Authorization", "Bearer "+token);
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        return StreamUtils.readStream(in,1024);
    }
}
