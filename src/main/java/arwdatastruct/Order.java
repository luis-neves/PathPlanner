package arwdatastruct;

import orderpicking.Pick;

import java.util.Hashtable;

public class Order {
    public String id;//orderid
    public Hashtable<String, Pick> lineorder;

    public Order() {
        id= "";
        lineorder=new Hashtable<>();

    }
}
