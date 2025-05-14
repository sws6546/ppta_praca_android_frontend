package com.example.messanger;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientManager {
    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json");

    public String getBackendUrl() {
        return backendUrl;
    }
    private String backendUrl = "https://hen-adapting-penguin.ngrok-free.app/";

    private ObjectMapper mapper = new ObjectMapper();

    private boolean haveConnection = false;

    HttpClientManager() {
        checkConnection();
    }

    public Boolean isConnected() {
        return haveConnection;
    }

    public void checkConnection() {
        Request request = new Request.Builder()
                .url(backendUrl + "ping")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String jsonRes = response.body().string();
            Map<String, String> res = mapper.readValue(jsonRes, Map.class);
            haveConnection = Objects.equals(res.get("message"), "pong");
        }
        catch (Exception e) {
            System.out.println(e.toString());
            haveConnection = false;
        }
    }

    public SimpleEntry<Boolean, String> register(String username, String pwd) {
        String formattedJsonString = "{\"username\":\"" + username + "\",\"password\":\"" + pwd + "\"}";
        RequestBody body = RequestBody.create(formattedJsonString, JSON);
        Request request = new Request.Builder()
                .url(backendUrl +  "auth/register")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            Map<String, String> res = mapper.readValue(json, Map.class);
            if (!res.containsKey("err"))
                return new SimpleEntry<>(false, res.get("Name"));
            else
                return new SimpleEntry<>(true, res.get("err"));
        }
        catch (Exception e) {
            System.out.println(e.toString());
            return new SimpleEntry<>(true, e.toString());
        }
    }

    public SimpleEntry<String, User> login(String username, String pwd) {
        String formattedJsonString = "{\"username\":\"" + username + "\",\"password\":\"" + pwd + "\"}";
        RequestBody body = RequestBody.create(formattedJsonString, JSON);
        Request request = new Request.Builder()
                .url(backendUrl +  "auth/login")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            Map<String, String> res = mapper.readValue(json, Map.class);
            if (res.containsKey("err")) {
                return new SimpleEntry<String, User>(res.get("err"), null);
            }
            else {
                return new SimpleEntry<String, User>("", new User(res.get("username"), res.get("token")));
            }
        } catch (Exception e) {
            return new SimpleEntry<String, User>(e.toString(), null);
        }
    }

    /**
     * Returned string is for error. If its no error, returns "".
     */
    public String checkAuthorizationToken(String token) {
        Request request = new Request.Builder()
                .url(backendUrl +  "auth/checkToken")
                .header("Authorization", "Bearer " + token)
                .build();
        try(Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            Map<String, String> res = mapper.readValue(json, Map.class);
            return res.getOrDefault("err", "");
        }
        catch (Exception e) {
            return e.toString();
        }
    }

    public String getUsernameById(String userId) {
        Request request = new Request.Builder()
                .url(backendUrl + "users/getById/" + userId)
                .build();
        try(Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            Map<String, String> res = mapper.readValue(json, Map.class);
            return res.get("Name");
        }
        catch (Exception e) {
            return "";
        }
    }
    public String getUserIdByUsername(String username) {
        Request request = new Request.Builder()
                .url(backendUrl + "users/getByName/" + username)
                .build();
        try(Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            Map<String, String> res = mapper.readValue(json, Map.class);
            return res.get("ID");
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns map of users <username, userId>
     */
    public Map<String, String> getAllUsers() {
        Request request = new Request.Builder()
                .url(backendUrl + "users/all")
                .build();

        try(Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            List<Map<String, String>> res = mapper.readValue(json, new TypeReference<List<Map<String, String>>>() {});
            Map<String, String> allUsers = new HashMap<String, String>();
            res.forEach((usr) -> {
                allUsers.put(usr.get("Name"), usr.get("ID"));
            });
            return allUsers;
        }
        catch (Exception e) {
            Log.d("myTag", e.toString());
            return new HashMap<>();
        }
    }
}
