package arwdatastruct;


public class Pick {
    String order;
    String lineorder;
    String productid;
    String quantity;
    String origin;
    String destiny;

    public Pick(String order, String lineorder,String id, String quantity, String origin, String destiny) {
        this.order = order;
        this.lineorder = lineorder;
        this.productid = id;
        this.quantity = quantity;
        this.origin = origin;
        this.destiny = destiny;
    }

        /*
    List<String> products;
    <Ordem>Encomenda 199749</Ordem>
    <LinhaOrdem>1</LinhaOrdem>
    <Produto>50002020200050A</Produto>
    <Quantidade>4</Quantidade>
    <Origem>13.A.0.08</Origem>
    <Destino>Saída</Destino>
     */

    public String getOrder() {
            return order;
        }

    public String getLineorder() {
        return lineorder;
    }
    public String getProductid() {
        return productid;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestiny() {
        return destiny;
    }

    public String toString(){
        String xmlstring="\t<Produto>" + productid + "</Produto>\n"
                +"\t<Quantidade>" + quantity + "</Quantidade>\n"
                +"\t<Origem>"+origin+"</Origem>\n"
                +"\t<Destino>" + destiny + "</Destino>\n";
        return xmlstring;
    }
}
