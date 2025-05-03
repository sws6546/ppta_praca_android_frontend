package com.example.messanger;

import androidx.annotation.NonNull;

public class User {
    User(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String toString() {
        return "username: " + this.username + ", token: " + this.token;
    }

    public String username;
    public String token;
}
