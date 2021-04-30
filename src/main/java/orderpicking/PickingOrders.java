package orderpicking;

import arwdatastruct.Order;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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

//Não faz sentido a Order e as Picking Orders estarem na mesma package?
public class PickingOrders {

    //A Key será a etiqueta da Ordem, recebida do ERP
    //A Hashtable interna terá os diferentes Picks
    //Endereçados pelo nº Linhaordem
    private ArrayList<Order> orders;

    public PickingOrders() {
        orders = new ArrayList<>();
    }

    public Order getOrder(String orderId){
        Order order = null;
        for (Order existingOrder : orders)
            if (existingOrder.getId().equals(orderId)){
                order = existingOrder;
                break;
            }
        return order;
    }

    public void add(
            String orderId,
            String orderline,
            String product,
            String quantity,
            String origin,
            String destiny) {
        Order order = getOrder(orderId);
        if (order == null) {
            order = new Order();
            order.setId(orderId);
            orders.add(order);
        }
        order.addPick(new Pick(orderId, orderline, product, quantity, origin, destiny));
    }

    //A ideia é inserir a order no início
    //Este método não está a ser usado
    public ArrayList<Order> recover(Order order){
        ArrayList<Order> newOrders = new ArrayList<>();
        newOrders.add(order);
        newOrders.addAll(orders);
        orders = newOrders;
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
    //WMS == código que identifica a área de origem de um pick
    //Este método não está a ser usado
    public ArrayList<String> getWms(String orderId){
        Hashtable<String, Pick> picks = getOrder(orderId).getPicks();
        Set<String> pickKeys = picks.keySet();
        ArrayList<String> wms = new ArrayList<>();
        for (String pickKey : pickKeys) {
            Pick pick = picks.get(pickKey);
            wms.add(pick.getOrigin());
        }
        return wms;
    }

    public String toString(String orderId){
        Hashtable<String, Pick> picks = getOrder(orderId).getPicks();
        Set<String> pickKeys = picks.keySet();
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-16\"?>\n" +
                "<ArrayOfTarefa xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">";
        for (String pickKey : pickKeys) {
            Pick pick = picks.get(pickKey);
            xmlString = xmlString + "<Tarefa>\n\t<Ordem>" + orderId+"</Ordem>";
            xmlString = xmlString + "\t<LinhaOrdem>" + pickKey + "</LinhaOrdem>\n";
            xmlString = xmlString + pick.toString();
            xmlString = xmlString + "<Tarefa>\n";
        }
        xmlString = xmlString + "</ArrayOfTarefa>";
        return xmlString;
    }

    //Sugestão: parseTaskXML
    public void parseXMLERPRequest(String content) {
        try {
            System.out.println(content);
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(content));

            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            NodeList taskNodes = doc.getElementsByTagName("Tarefa");

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Element element = (Element) taskNodes.item(i);

                if (element.getNodeType() == Node.ELEMENT_NODE)
                    add(element.getElementsByTagName("Ordem").item(0).getTextContent(),
                        element.getElementsByTagName("LinhaOrdem").item(0).getTextContent(),
                        element.getElementsByTagName("Produto").item(0).getTextContent(),
                        element.getElementsByTagName("Quantidade").item(0).getTextContent(),
                        element.getElementsByTagName("Origem").item(0).getTextContent(),
                        element.getElementsByTagName("Destino").item(0).getTextContent());
            }
            System.out.println("Received " + taskNodes.getLength() + " products from ERP");
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

            for (Order order: orders){
                Iterator<String> itr = order.getPicks().keySet().iterator();
                while (itr.hasNext()) {
                    Pick pick = order.getPicks().get(itr.next());
                    Element product = document.createElement("Tarefa");
                    Element lineProd = document.createElement("Ordem");
                    lineProd.appendChild(document.createTextNode(pick.getOrder()));
                    product.appendChild(lineProd);
                    lineProd = document.createElement("Linhaordem");
                    lineProd.appendChild(document.createTextNode(pick.getOrderLine()));
                    product.appendChild(lineProd);
                    lineProd = document.createElement("Produto");
                    lineProd.appendChild(document.createTextNode(pick.getId()));
                    product.appendChild(lineProd);
                    lineProd = document.createElement("Quantidade");
                    lineProd.appendChild(document.createTextNode(pick.getQuantity()));
                    product.appendChild(lineProd);
                    lineProd = document.createElement("Origem");
                    lineProd.appendChild(document.createTextNode(pick.getOrigin()));
                    product.appendChild(lineProd);
                    lineProd = document.createElement("Destino");
                    lineProd.appendChild(document.createTextNode(pick.getDestiny()));
                    product.appendChild(lineProd);
                    root.appendChild(product);
                }
            }

            //file ou String?
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