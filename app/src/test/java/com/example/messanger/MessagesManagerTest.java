package com.example.messanger;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class MessagesManagerTest {
    @Test
    public void MessagesManager_checkAddingAndGettingAllMessagesList() {
        MessagesManager mm = new MessagesManager();
        List<Message> testingMessages = new ArrayList<Message>();
        for(int i = 0; i < 30; i++) {
            String testingCredentials = "test_" + System.currentTimeMillis();
            testingMessages.add(new Message(testingCredentials, testingCredentials, testingCredentials));
        }
        for(Message testingMsg : testingMessages) {
            mm.addMessage(testingMsg);
        }
        assertEquals(testingMessages, mm.getMessagesList());
    }
}
