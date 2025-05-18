package com.example.messanger;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

public class ChatManager {
    private String chatterName;
    private String chatterId;
    private AuthManager auth;
    private HttpClientManager httpClientManager;
    public static boolean shouldListenMessages;
    private Activity uiActivity;
    private MessagesManager messagesManager = new MessagesManager();
    private WebSocketManager webSocketManager;

    ChatManager(String chatterName, String chatterId, AuthManager auth, HttpClientManager httpClientManager, Activity activity, WebSocketManager webSocketManager) {
        ChatManager.shouldListenMessages = true;
        this.chatterName = chatterName;
        this.chatterId = chatterId;
        this.auth = auth;
        this.httpClientManager = httpClientManager;
        this.uiActivity = activity;
        this.webSocketManager = webSocketManager;

        MainActivity.isWebSocketConnected = false;

        openChatWithChatter();
        ((TextView) uiActivity.findViewById(R.id.chatterName)).setText(chatterName);

        runReadingMessagesThread();

        ((Button) uiActivity.findViewById(R.id.sendMsgBtn)).setOnClickListener((l) -> {onSendMsg();});
    }

    private void onSendMsg() {
        String messageCtx = ((TextInputEditText) uiActivity.findViewById(R.id.messageContextInput)).getText().toString();
        String err = webSocketManager.sendMessage(this.chatterId, messageCtx);
        messagesManager.addMessage(new Message(this.auth.getUser().username, this.chatterName, messageCtx));
        ((TextInputEditText) uiActivity.findViewById(R.id.messageContextInput)).setText("");
    }

    private void runReadingMessagesThread() {
        new Thread(() -> {
            List<Message> messages = List.copyOf(messagesManager.getMessagesWithSpecificUser(chatterName));
            renderMessages(messages);
            while(shouldListenMessages) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    Log.d("myTag", e.toString());
                }

                if (!messages.equals(List.copyOf(messagesManager.getMessagesWithSpecificUser(chatterName)))) {
                    messages = List.copyOf(messagesManager.getMessagesWithSpecificUser(chatterName));
                    renderMessages(messages);
                }
            }
        }).start();
    }

    private void renderMessages(List<Message> messages) {
        uiActivity.runOnUiThread(() -> {
            ((LinearLayout) uiActivity.findViewById(R.id.msgContainer)).removeAllViews();
            messages.forEach(this::addMessage);
        });
    }

    private void addMessage(Message msg) {
        TextView newMessage = new TextView(uiActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);
        if (Objects.equals(msg.sender, auth.getUser().username)) {
            params.gravity = Gravity.END;
            newMessage.setBackgroundColor(0xFFfd9a00);
        } else {
            params.gravity = Gravity.START;
            newMessage.setBackgroundColor(0xFFB0C6FF);
        }
        newMessage.setLayoutParams(params);

        newMessage.setText(msg.messageContent);
        newMessage.setPadding(16,16,16,16);
        ((LinearLayout) uiActivity.findViewById(R.id.msgContainer)).addView(newMessage);
    }

    private void openChatWithChatter() {
        uiActivity.setContentView(R.layout.chat_window);
    }
}
