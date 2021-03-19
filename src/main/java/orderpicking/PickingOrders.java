package orderpicking;


import ARWDataStruct.Order;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pathfinder.GraphNode;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import java.io.IOException;
import java.io.StringReader;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


public class PickingOrders {

    //A Key será a etiqueta da Ordem, recebida do ERP
    //A Hashtable interna terá os diferentes Picks
    //Endereçados pelo nº Linhaordem
    ArrayList<Order> orders;

    public PickingOrders() {
        orders = new ArrayList<>();
    }

    public Order getOrder(String orderid){
        Order order=null;
        for (Order existingorder: orders)
            if (existingorder.id.equals(orderid)){
                order=existingorder;
                break;
            }
        return order;
    }

    public void add(String orderid, String orderline, String product, String quantity, String origin, String destiny) {
        Order order=getOrder(orderid);
        if (order==null) {
            order = new Order();
            order.id=orderid;
            orders.add(order);
        }
        if (!order.lineorder.containsKey(orderline)) {
            order.lineorder.put(orderline, new Pick(orderid, orderline, product, quantity, origin, destiny));
            //orders.add(order);
        }
    }

    public ArrayList<Order> recover(Order order){
        ArrayList<Order> neworders = new ArrayList<>();
        neworders.add(order);
        neworders.addAll(orders);
        orders=neworders;
        return orders;
    }
    public ArrayList<Order> getOrders() {
        return orders;
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
    public ArrayList<String> getWms(String orderid){
        Hashtable<String, Pick> picks = getOrder(orderid).lineorder;
        Set<String> pickkeys = picks.keySet();
        ArrayList<String> wms=new ArrayList();
        for (String pickkey: pickkeys) {
            Pick pick = picks.get(pickkey);
            wms.add(pick.getOrigin());
        }
        return wms;

    }

    public String toString(String orderid){
        Hashtable<String, Pick> picks = getOrder(orderid).lineorder;
        Set<String> pickkeys = picks.keySet();
        String xmlstring="<?xml version=\"1.0\" encoding=\"utf-16\"?>\n" +
                "<ArrayOfTarefa xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">";
        for (String pickkey: pickkeys) {
            Pick pick = picks.get(pickkey);
            xmlstring=xmlstring+"<Tarefa>\n\t<Ordem>"+orderid+"</Ordem>";
            xmlstring=xmlstring+"\t<LinhaOrdem>"+pickkey+"</LinhaOrdem>\n";
            xmlstring=xmlstring+ pick.toString();
            xmlstring=xmlstring+"<Tarefa>\n";
        }
        xmlstring=xmlstring+"</ArrayOfTarefa>";
        return xmlstring;
    }

    public void parseTarefaXML(String content) {
        try {
            System.out.println(content);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(content));

            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            NodeList tarefas_nodes = doc.getElementsByTagName("Tarefa");

            for (int j = 0; j < tarefas_nodes.getLength(); j++) {
                Element element = (Element) tarefas_nodes.item(j);

                if (element.getNodeType()== Node.ELEMENT_NODE)
                    add(element.getElementsByTagName("Ordem").item(0).getTextContent(),
                        element.getElementsByTagName("LinhaOrdem").item(0).getTextContent(),
                        element.getElementsByTagName("Produto").item(0).getTextContent(),
                        element.getElementsByTagName("Quantidade").item(0).getTextContent(),
                        element.getElementsByTagName("Origem").item(0).getTextContent(),
                        element.getElementsByTagName("Destino").item(0).getTextContent());

            }
            System.out.println("Received " + tarefas_nodes.getLength() + " products from ERP");
        } catch(ParserConfigurationException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(SAXException e){
            e.printStackTrace();
        }



    }

    public String toXML(){
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("ArrayofTarefa");

            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            document.appendChild(root);
            //

            for (Order order: orders){

                    Iterator<String> itr=order.lineorder.keySet().iterator();
                    while (itr.hasNext()) {
                        Pick pick=order.lineorder.get(itr.next());
                        Element product = document.createElement("Tarefa");
                        Element linprod = document.createElement("Ordem");
                        linprod.appendChild(document.createTextNode(pick.getOrder()));
                        product.appendChild(linprod);
                        linprod = document.createElement("Linhaordem");
                        linprod.appendChild(document.createTextNode(pick.getLineorder()));
                        product.appendChild(linprod);
                        linprod = document.createElement("Produto");
                        linprod.appendChild(document.createTextNode(pick.getId()));
                        product.appendChild(linprod);
                        linprod = document.createElement("Quantidade");
                        linprod.appendChild(document.createTextNode(pick.getQuantity()));
                        product.appendChild(linprod);
                        linprod = document.createElement("Origem");
                        linprod.appendChild(document.createTextNode(pick.getOrigin()));
                        product.appendChild(linprod);
                        linprod = document.createElement("Destino");
                        linprod.appendChild(document.createTextNode(pick.getDestiny()));
                        product.appendChild(linprod);
                        root.appendChild(product);

                    }

            }

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformerFactory.setAttribute("indent-number", 2);
            DOMSource domSource = new DOMSource(document);
            StringWriter writer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(writer));

            String xmlString = writer.getBuffer().toString();
            return xmlString;
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
        return "";
    }

}