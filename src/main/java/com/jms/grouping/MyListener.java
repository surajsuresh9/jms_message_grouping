package com.jms.grouping;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.Map;

public class MyListener implements MessageListener {
    private final String name;
    private final Map<String, String> receivedMessages;

    MyListener(String name, Map<String, String> receivedMessages) {
        this.name = name;
        this.receivedMessages = receivedMessages;
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            System.out.println("Message received: " + textMessage.getText());
            System.out.println("Listener name: " + name);
            receivedMessages.put(textMessage.getText(), name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}