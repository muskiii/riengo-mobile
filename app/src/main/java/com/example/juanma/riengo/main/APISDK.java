package com.example.juanma.riengo.main;

import android.util.Base64;
import android.util.Log;

import com.example.juanma.riengo.main.activities.MainActivity;
import com.example.juanma.riengo.main.activities.StreamUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty ("Authorization", "Bearer "+token);
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        String result = StreamUtils.readStream(in,99999999);
        System.out.println("resultado api2: "+result);
        return result;

    }
    public static String getBellsByOwner(String idOwner) throws IOException {
        URL url = new URL("https://riengo-api.herokuapp.com/v1/ownerbells/"+MainActivity.onesignalPlayerId);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String token = createJwtToken();

        urlConnection.setDoOutput(true);
        urlConnection.setChunkedStreamingMode(0);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty ("Authorization", "Bearer "+token);
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

        return StreamUtils.readStream(in,1024);
    }

    private static void writeStream(OutputStream out, String string) {
        final PrintStream printStream = new PrintStream(out);
        printStream.print(string);
        printStream.close();
    }

    private static void writeStream(OutputStream out, String name, String expireTime) {
        String urlParameters = "name="+name+"&facebookId="+ MainActivity.onesignalPlayerId+"&hoursExpire="+expireTime;

       /* Map mapa = Maps.newHashMap();
        mapa.put("name",bell_name_edit.getText().toString());
        mapa.put("facebookId","74748383");
        mapa.put("hoursExpire","1");
        JSONObject obj=new JSONObject(mapa);*/

       Log.i("CallApiBell",urlParameters);

        final PrintStream printStream = new PrintStream(out);
        printStream.print(urlParameters);
        printStream.close();
    }
    public static String createBell(String name, String expireTime) throws IOException {
        String token = createJwtToken();
        URL url = new URL("https://riengo-api.herokuapp.com/v1/bell");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        System.out.println("el token: "+token);
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty ("Authorization", "Bearer "+token);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(out,name,expireTime);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return StreamUtils.readStream(in,102444);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    public static String createUser(String onesignalId, String email, String facebookId, String name) throws IOException {
        String token = createJwtToken();
        URL url = new URL("https://riengo-api.herokuapp.com/v1/user");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        System.out.println("el token: "+token);
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty ("Authorization", "Bearer "+token);

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            //email pushId facebookId name
            String params = "pushId="+onesignalId+"&facebookId="+facebookId+"&name="+name+"&email="+email;
            Log.i("debug",params);
            writeStream(out, params);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return StreamUtils.readStream(in,102444);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }
}
