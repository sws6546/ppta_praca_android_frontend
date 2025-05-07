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

    public void clear() {
        messagesList.clear();
    }
}
