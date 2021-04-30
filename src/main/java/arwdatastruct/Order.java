package arwdatastruct;

import orderpicking.Pick;

import java.util.Hashtable;

public class Order {
    private String id;
    private Hashtable<String, Pick> picks;

    public Order() {
        this.id = "";
        this.picks = new Hashtable<>();
    }

    public void addPick(Pick pick){
        if (!picks.containsKey(pick.getOrderLine())) {
            picks.put(pick.getOrderLine(), pick);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Hashtable<String, Pick> getPicks() {
        return picks;
    }

    public void setPicks(Hashtable<String, Pick> picks) {
        this.picks = picks;
    }
}
