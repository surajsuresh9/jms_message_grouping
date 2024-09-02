package com.jms.grouping;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.w3c.dom.Text;

import javax.jms.*;
import javax.naming.InitialContext;
import java.lang.IllegalStateException;
import java.util.HashMap;
import java.util.Map;

public class MessageGroupingDemo {
    public static void main(String[] args) throws Exception {
        InitialContext initialContext = new InitialContext();
        Map<String, String> receivedMessages = new HashMap<>();
        Queue queue = (Queue) initialContext.lookup("queue/myQueue");
        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(); JMSContext jmsContext = cf.createContext(); JMSContext jmsContext2 = cf.createContext();

        ) {
            JMSProducer producer = jmsContext.createProducer();
            JMSConsumer consumer1 = jmsContext2.createConsumer(queue);
            consumer1.setMessageListener(new MyListener("Consumer-1", receivedMessages));
            JMSConsumer consumer2 = jmsContext2.createConsumer(queue);
            consumer2.setMessageListener(new MyListener("Consumer-2", receivedMessages));

            int count = 10;
            TextMessage[] textMessages = new TextMessage[count];
            for (int i = 0; i < count; i++) {
                textMessages[i] = jmsContext.createTextMessage("group-0 message " + i);
                textMessages[i].setStringProperty("JMSXGroupID", "group-0");
                producer.send(queue, textMessages[i]);
            }

            Thread.sleep(2000);

            for (TextMessage message : textMessages) {
                if (!receivedMessages.get(message.getText()).equals("Consumer-1")) {
                    throw new IllegalStateException("Group Message " + message.getText() + " ahs gone to the wrong listener");
                }
            }
        }
    }
}