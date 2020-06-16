package communication;

import classlib.BusMessage;
import classlib.ICommunicationManagerCallbacks;
import classlib.Util;
import ga.GASingleton;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class MyCallbacks implements ICommunicationManagerCallbacks {

    @Override
    public void InitializationDoneEvent(boolean b) {
        if (b) {
            System.out.println("\nInitialization completed with success.");
        } else {
            System.out.println("\nAn error occurred during the initialization.");
        }
    }

    @Override
    public void MessageSentEvent(boolean b, String s) {
        if (b) {
            System.out.println("MESSAGE SENT WITH SUCCESS. ID: " + s + ".");
        } else {
            System.out.println("A PROBLEM OCCUR WHILE TRYING TO SEND THE MESSAGE. ID: " + s + ".");
        }
    }

    @Override
    public void StreamMessageSentEvent(boolean b, String s, String s1) {

    }

    @Override
    public void ContentSubscribedEvent(boolean b, String s, String s1) {
        if (b) {
            System.out.println("Subscribed to '" + s + "' with " + s1);
        } else {
            System.out.println("Fail to subscribe to '" + s + "' with " + s1);
        }
    }

    @Override
    public void ContentUnsubscribedEvent(boolean b, String s, String s1) {
        if (b) {
            System.out.println("Unsubscribed from '" + s + "' with " + s1);
        } else {
            System.out.println("Fail to unsubscribe from '" + s + "' with " + s1);
        }
    }

    @Override
    public void MessageToProcessEvent(BusMessage busMessage) {
        System.out.println("\nMESSAGE TO BE PROCESSED");
        System.out.println("------------------------------------------");
        System.out.println("\tId: " + busMessage.getId());
        System.out.println("\tMessage type: " + busMessage.getMessageType());
        System.out.println("\tInfo identifier: " + busMessage.getInfoIdentifier());
        System.out.println("\tTo topic: " + busMessage.getToTopic());
        System.out.println("\tFrom topic: " + busMessage.getFromTopic());
        System.out.println("\tData format: " + busMessage.getDataFormat());
        System.out.println("\tPriority: " + busMessage.getPriority());
        System.out.println("\tContent: " + busMessage.getContent());
        System.out.println("------------------------------------------");
        switch (busMessage.getMessageType()) {
            case "request":
                System.out.println("REQUEST message ready to be processed.");
                if (busMessage.getInfoIdentifier().equals("Disponivel")) {
                    String[] split = busMessage.getFromTopic().split("Topic");
                    Operator operator = GASingleton.getInstance().findOperator(split[0]);
                    if (operator == null) {
                        GASingleton.getInstance().addOperator(split[0], Boolean.parseBoolean(busMessage.getContent()));
                        System.out.println("New Operator " + split[0] + " " + busMessage.getContent());
                    } else {
                        operator.setAvailable(Boolean.parseBoolean(busMessage.getContent()));
                        System.out.println("Operator " + operator.getId() + " " + operator.isAvailable());
                    }
                    GASingleton.getInstance().getCm().SendMessageAsync((Integer.parseInt(busMessage.getId()) + 1) + "", "response", busMessage.getInfoIdentifier(), split[0], "plaintext", "OK", "1");
                }
                break;
            case "response":
                System.out.println("RESPONSE message ready to be processed.");
                if(busMessage.getInfoIdentifier().equals("getAllOrders") && busMessage.getDataFormat().equals("application/json")){
                    //GET ALL ORDERS FROM ERP
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        List<Product> products = mapper.readValue(busMessage.getContent(), List.class);
                        System.out.println("Received "+products.size() + " products from ERP");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //TODO: process message..
                break;
            case "stream":
                System.out.println("STREAM message ready to be processed.");
                //TODO: process message..
                break;
        }
    }
}
