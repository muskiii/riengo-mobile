package com.example.juanma.riengo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    TextView txtStatus;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initializeControls();
        loginWithFB();



    }
    private void initializeControls(){
        callbackManager = CallbackManager.Factory.create();
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        loginButton = (LoginButton) findViewById(R.id.login_button);
    }

    private void loginWithFB(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println(loginResult);
                txtStatus.setText("success "+ loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                txtStatus.setText("Log in Cancelled");

            }

            @Override
            public void onError(FacebookException error) {
                System.out.println(error);
                txtStatus.setText("Log in ERROR: " + error.getMessage());
            }
        });

    }
}
