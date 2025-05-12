package com.example.messanger;

import android.app.Activity;
import android.widget.TextView;

public class ChatManager {
    private String chatterName;
    private String chatterId;
    private AuthManager auth;
    private HttpClientManager httpClientManager;
    Activity uiActivity;
    MessagesManager messagesManager = new MessagesManager();

    ChatManager(String chatterName, String chatterId, AuthManager auth, HttpClientManager httpClientManager, Activity activity) {
        this.chatterName = chatterName;
        this.chatterId = chatterId;
        this.auth = auth;
        this.httpClientManager = httpClientManager;
        this.uiActivity = activity;

        openChatWithChatter();
        ((TextView) uiActivity.findViewById(R.id.chatterName)).setText("Chatting with " + chatterName);


    }

    private void openChatWithChatter() {
        uiActivity.setContentView(R.layout.chat_window);
    }
}
