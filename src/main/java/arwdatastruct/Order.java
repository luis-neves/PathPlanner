package arwdatastruct;

import orderpicking.Pick;
import orderpicking.Request;

import java.util.*;

public class Order {
    private String id;
    private Request request;
    private List<Pick> picks;

    public Order(String id, Request request) {
        this.id = id;
        this.picks = new ArrayList<>();
        this.request = request;
    }

    public void addPick(Pick pick){
        picks.add(pick);
    }

    public String getId() {
        return id;
    }

    public List<Pick> getPicks() {
        return picks;
    }

    public Request getRequest() {
        return request;
    }
}
