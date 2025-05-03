package com.example.messanger;

import java.util.AbstractMap.SimpleEntry;

public class AuthManager {
    AuthManager(HttpClientManager httpClientManager) {
        this.httpClient = httpClientManager;
    }
    private HttpClientManager httpClient;
    private User user = new User(null, null);
    private boolean userLoggedStatus = false;

    public User getUser(){
        return this.user;
    }
    public boolean isUserLogged() {
        return this.userLoggedStatus;
    }

    /**
     * Returned string is for error. If its no error, returns "".
     */
    public String logout() {
        try {
            this.user = new User(null, null);
            this.userLoggedStatus = false;
            return "";
        } catch (Exception e) {
            return e.toString();
        }
    }

    /**
     * Returned string is for error. If its no error, returns "".
     */
    public String login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return "Username or password cannot be empty";
        }
        SimpleEntry <String, User> loggedUserEntry = httpClient.login(username, password);
        if(!loggedUserEntry.getKey().isEmpty()) {
            return loggedUserEntry.getKey();
        }
        this.user.username = loggedUserEntry.getValue().username;
        if (this.user.username.isEmpty()) {
            return "Server did not return an username";
        }
        this.user.token = loggedUserEntry.getValue().token;
        if (this.user.token.isEmpty()) {
            return "Server did not return a token";
        }
        this.userLoggedStatus = true;

        return "";
    }

    /**
     * Returned string is for error. If its no error, returns "".
     */
    public String register(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return "Username or password cannot be empty";
        }
        SimpleEntry<Boolean, String> res = httpClient.register(username, password);
        if (res.getKey()) {
            return res.getValue();
        }
        return "";
    }

    /**
     * Returned string is for error. If its no error, returns "".
     */
    public String checkAuthToken() {
        return httpClient.checkAuthorizationToken(this.user.token);
    }
}
