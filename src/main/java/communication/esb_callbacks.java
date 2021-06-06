package communication;

import classlib.BusMessage;
import classlib.CommunicationManager;
import classlib.ICommunicationManagerCallbacks;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class esb_callbacks implements ICommunicationManagerCallbacks {
    CommunicationManager cm;
    BusMessage busMessage;
    private final PropertyChangeSupport support;

    public esb_callbacks(){
        support = new PropertyChangeSupport(this);
    }

    public esb_callbacks(CommunicationManager cm, PropertyChangeListener pcl){
        support = new PropertyChangeSupport(this);
        this.SetCommunicationManager(cm);
        this.addPropertyChangeListener(pcl);
    }

    @Override
    public CommunicationManager getCommunicator() {
        return cm;
    }

    @Override
    public void SetCommunicationManager(CommunicationManager communicationManager) {
        cm = communicationManager;
    }

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
        support.firePropertyChange("busMessage",this.busMessage, busMessage);
        this.busMessage = busMessage;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
}
