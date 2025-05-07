package com.example.messanger;

public class Message {
    public String sender;
    public String receiver;
    public String messageContent;

    Message(String sender, String receiver, String messageContent) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
    }
}
