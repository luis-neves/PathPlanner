package communication;

import classlib.BusMessage;
import classlib.ICommunicationManagerCallbacks;

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

    }

    @Override
    public void ContentUnsubscribedEvent(boolean b, String s, String s1) {

    }

    @Override
    public void MessageToProcessEvent(BusMessage busMessage) {
        /*
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
        System.out.println("------------------------------------------");*/
        switch (busMessage.getMessageType()) {
            case "request":
                System.out.println("REQUEST message ready to be processed.");
                //TODO: process message..
                break;
            case "response":
                System.out.println("RESPONSE message ready to be processed.");
                //TODO: process message..
                break;
            case "stream":
                System.out.println("STREAM message ready to be processed.");
                //TODO: process message..
                break;
        }
    }
}
