package com.example.messanger;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private OkHttpClient client = new OkHttpClient();
    private HttpClientManager httpClient = new HttpClientManager();
    private WebSocket webSocket;
    private MessagesManager messagesManager = new MessagesManager();
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Returned string is for error. If its no error, returns "".
     */
    public String connect(AuthManager auth) {
        if(!auth.isUserLogged()) return "User not logged";

        Request request = new Request.Builder()
                .url("wss://hen-adapting-penguin.ngrok-free.app/ws")
                .header("Authorization", "Bearer " + auth.getUser().token)
                .build();

        this.webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                try {
                    Map<String, String> res = mapper.readValue(text, Map.class);

                    String senderName = httpClient.getUsernameById(res.get("SenderId"));
                    String receiverName = auth.getUser().username;
                    String messageContext = res.get("MessageContent");

                    messagesManager.addMessage(new Message(senderName, receiverName, messageContext));

                    Log.d("myTag", messageContext);
                } catch (JsonProcessingException e) {
                    Log.d("myTag", "error: " + e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.d("myTag", response.toString());
                MainActivity.isWebSocketConnected = false;
            }
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                MainActivity.isWebSocketConnected = false;
            }
            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                MainActivity.isWebSocketConnected = false;
            }
        });

        MainActivity.isWebSocketConnected = true;
        return "";
    }
}
