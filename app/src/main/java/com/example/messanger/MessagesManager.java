package com.example.messanger;

import java.util.ArrayList;
import java.util.List;

public class MessagesManager {
    private static List<Message> messagesList = new ArrayList<Message>();

    public List<Message> getMessagesList() {
        return List.copyOf(messagesList);
    }

    public void addMessage(Message msg) {
        messagesList.add(msg);
    }

    public List<Message> getMessagesWithSpecificUser(String anotherUser) {
        List<Message> messagesWithThatUser = new ArrayList<Message>();
        for(Message msg : messagesList){
            if(msg.sender.equals(anotherUser) || msg.receiver.equals(anotherUser)) {
                messagesWithThatUser.add(msg);
            }
        }
        return messagesWithThatUser;
    }

    public List<String> getAllUsersThatHaveMessagesWith(AuthManager auth) {
        List<String> users = new ArrayList<String>();
        for(Message msg : messagesList) {
            if (!msg.sender.equals(auth.getUser().username) && !users.contains(msg.sender)){
                users.add(msg.sender);
            }
            if (!msg.receiver.equals(auth.getUser().username) && !users.contains(msg.receiver)){
                users.add(msg.receiver);
            }
        }
        return users;
    }

    public void clear() {
        messagesList.clear();
    }
}
