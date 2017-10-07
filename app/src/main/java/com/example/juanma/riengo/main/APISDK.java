package com.example.juanma.riengo.main;

import com.example.juanma.riengo.main.activities.StreamUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static String createJwtToken() {
        Key key = MacProvider.generateKey();
        String token = Jwts.builder().setSubject("client").signWith(SignatureAlgorithm.HS256, key).compact();
        return token;
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
