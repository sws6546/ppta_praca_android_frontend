package com.example.messanger;

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
