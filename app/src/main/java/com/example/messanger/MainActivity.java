package com.example.messanger;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    HttpClientManager httpClientManager;
    AuthManager auth;
    WebSocketManager webSocketManager;
    static Boolean isWebSocketConnected = false;

    Map<String, String> existingUsers = null;
    ChatManager chatManager;
    Activity uiActivity = this;

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
            webSocketManager = new WebSocketManager();
            webSocketManager.connect(auth);
            readNotification_Thread();
            setExistingUsers();
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
            auth.saveAuthToLocalStorage(this);
            changeContentViewToHome();
            webSocketManager = new WebSocketManager();
            webSocketManager.connect(auth);
            readNotification_Thread();
            setExistingUsers();
        }
    }

    public void changeContentViewToHome() {
        String err = auth.checkAuthToken();
        if (!err.isEmpty()) {
            return;
        }
        setContentView(R.layout.homepage_layout);
        ((TextView) findViewById(R.id.chatterName)).setText(auth.getUser().username);
    }

    public void onLogout(View v) {
        auth.logout();
        auth.clearLocalStorage(this);
        setContentView(R.layout.activity_main);
        webSocketManager = null;
    }

    public void readNotification_Thread() {
        Thread notificationListener = new Thread(() -> {
            MessagesManager messagesManager = new MessagesManager();
            List<Message> messages = messagesManager.getMessagesList();
            while(MainActivity.isWebSocketConnected) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.d("myTag", e.toString());
                }
                List<Message> newMessages = messagesManager.getMessagesList();
                if (!messages.equals(newMessages)) {
                    runOnUiThread(() -> {
                        ((LinearLayout) findViewById(R.id.msgNotificationsContainer)).removeAllViews();
                        for(Message msg : newMessages) {
                            addMessageNotification(msg.sender);
                        }
                    });
                    messages = List.copyOf(newMessages);
                }
            }
        });
        notificationListener.start();
    }

    public void addMessageNotification(String senderName) {
        TextView newNotification = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);
        newNotification.setLayoutParams(params);


        newNotification.setText(senderName);
        newNotification.setBackgroundColor(0xFFB0C6FF);
        newNotification.setPadding(16,16,16,16);
        ((LinearLayout) findViewById(R.id.msgNotificationsContainer)).addView(newNotification);
    }

    public void setExistingUsers() {
        Map<String, String> tempAllUsers = httpClientManager.getAllUsers();
        Map<String, String> allUsers = new LinkedHashMap<String, String>();
        allUsers.put("Select user", "");
        allUsers.putAll(tempAllUsers);

        Spinner allUsersSpinner = findViewById(R.id.allExistingUsers);
        allUsersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUser = parent.getItemAtPosition(position).toString();
                if(!selectedUser.equals("Select user")){
                    chatManager = new ChatManager(
                            selectedUser,
                            allUsers.get(selectedUser),
                            auth,
                            httpClientManager,
                            uiActivity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("myTag", "No user is selected");
            }
        });

        allUsersSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                allUsers.keySet().toArray()
        ));
    }

    public void backToHomepage(View v) {
        setContentView(R.layout.homepage_layout);
        setExistingUsers();
    }
}