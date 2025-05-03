package com.example.messanger;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    HttpClientManager httpClientManager;
    AuthManager auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        httpClientManager = new HttpClientManager();
        auth = new AuthManager(httpClientManager);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (auth.checkAuthFromLocalStorage(this)) {
            changeContentViewToHome();
        }
    }

    public void onRegister(View v) {
        String username = ((TextInputEditText) findViewById(R.id.registerUname)).getText().toString();
        String password = ((TextInputEditText) findViewById(R.id.registerPwd)).getText().toString();
        TextView registerErrorTextView = findViewById(R.id.registerErrTextView);

        String err = auth.register(username, password);
        if (!err.isEmpty()) {
            registerErrorTextView.setTextColor(0xFFBC0202);
            registerErrorTextView.setText(err);
        }
        else {
            registerErrorTextView.setTextColor(0xFF897BFF);
            registerErrorTextView.setText("Account succesfully created. You can log in now.");
        }
    }

    public void onLogin(View v) {
        String username = ((TextInputEditText) findViewById(R.id.loginUname)).getText().toString();
        String password = ((TextInputEditText) findViewById(R.id.loginPwd)).getText().toString();

        TextView loginErrorTextView = findViewById(R.id.loginErrTextView);
        String err = auth.login(username, password);

        if (!err.isEmpty()) {
            loginErrorTextView.setTextColor(0xFFBC0202);
            loginErrorTextView.setText(err);
        }
        else {
            changeContentViewToHome();
        }
        auth.saveAuthToLocalStorage(this);
    }

    public void changeContentViewToHome() {
        String err = auth.checkAuthToken();
        if (!err.isEmpty()) {
            return;
        }
        setContentView(R.layout.homepage_layout);
        ((TextView) findViewById(R.id.headerUname)).setText(auth.getUser().username);
    }

    public void onLogout(View v) {
        auth.logout();
        auth.clearLocalStorage(this);
        setContentView(R.layout.activity_main);
    }
}