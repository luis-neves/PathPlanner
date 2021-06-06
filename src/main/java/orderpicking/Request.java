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
import java.util.*;

public class Request {
    private Map<String, Order> orders;
    private int numberOfUnfinishedTasks;
    //Tem que ser um HashMap para no final o xml ficar organizado por orders
    private Map<Order, List<Pick>> solvedPicks;

    public Request() {
        orders = new HashMap<>();
        solvedPicks = new HashMap<>();
        this.numberOfUnfinishedTasks = 0;
    }

    public boolean isSolved(){
        return numberOfUnfinishedTasks == 0;
    }

    public void decNumberTasksUnfinished(){
        numberOfUnfinishedTasks--;
    }

    public void incNumberOfUnfinishedTasks(){
        numberOfUnfinishedTasks++;
    }

    public void addPick(Pick pick) {
        String orderID = pick.getOrderID();
        Order order = orders.get(orderID);
        if (order == null) {
            order = new Order(orderID, this);
            orders.put(orderID, order);
        }
        order.addPick(pick);
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());
    }

    public void addSolvedPicks(List<Pick> newSolvedPicks){
        for (Pick pick : newSolvedPicks) {
            Order order = orders.get(pick.getOrderID());
            if (!solvedPicks.containsKey(order)) {
                solvedPicks.put(order, new LinkedList<Pick>());
            }
            List<Pick> orderPicks = solvedPicks.get(order);
            orderPicks.add(pick);
        }
    }

    public void parseXMLERPRequest(String content) {
        try {

//            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//            System.out.println(content);
//            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(content));

            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            NodeList taskNodes = doc.getElementsByTagName("Tarefa");

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Element element = (Element) taskNodes.item(i);
                if (element.getNodeType() == Node.ELEMENT_NODE) {
                    Pick pick = new Pick(
                            element.getElementsByTagName("Ordem").item(0).getTextContent(),
                            element.getElementsByTagName("LinhaOrdem").item(0).getTextContent(),
                            element.getElementsByTagName("Produto").item(0).getTextContent(),
                            element.getElementsByTagName("Quantidade").item(0).getTextContent(),
                            element.getElementsByTagName("Origem").item(0).getTextContent(),
                            element.getElementsByTagName("Destino").item(0).getTextContent());
                    addPick(pick);
                }
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

    public String concludedPicksToXML(){
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("ArrayofTarefa");

            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            document.appendChild(root);

            for (List<Pick> orderPicks: solvedPicks.values()){
                for (Pick pick : orderPicks) {
                    Element product = document.createElement("Tarefa");
                    Element lineProd = document.createElement("Ordem");
                    lineProd.appendChild(document.createTextNode(pick.getOrderID()));
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

//    public String toXML(){
//        try {
//            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
//            Document document = documentBuilder.newDocument();
//
//            // root element
//            Element root = document.createElement("ArrayofTarefa");
//
//            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
//            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
//            document.appendChild(root);
//
//            for (Order order: orders.values()){
//                Iterator<String> itr = order.getPicks().keySet().iterator();
//                while (itr.hasNext()) {
//                    Pick pick = order.getPicks().get(itr.next());
//                    Element product = document.createElement("Tarefa");
//                    Element lineProd = document.createElement("Ordem");
//                    lineProd.appendChild(document.createTextNode(pick.getOrderID()));
//                    product.appendChild(lineProd);
//                    lineProd = document.createElement("Linhaordem");
//                    lineProd.appendChild(document.createTextNode(pick.getOrderLine()));
//                    product.appendChild(lineProd);
//                    lineProd = document.createElement("Produto");
//                    lineProd.appendChild(document.createTextNode(pick.getId()));
//                    product.appendChild(lineProd);
//                    lineProd = document.createElement("Quantidade");
//                    lineProd.appendChild(document.createTextNode(pick.getQuantity()));
//                    product.appendChild(lineProd);
//                    lineProd = document.createElement("Origem");
//                    lineProd.appendChild(document.createTextNode(pick.getOrigin()));
//                    product.appendChild(lineProd);
//                    lineProd = document.createElement("Destino");
//                    lineProd.appendChild(document.createTextNode(pick.getDestiny()));
//                    product.appendChild(lineProd);
//                    root.appendChild(product);
//                }
//            }
//
//            //file ou String?
//            // create the xml file
//            //transform the DOM Object to an XML File
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformerFactory.setAttribute("indent-number", 2);
//            DOMSource domSource = new DOMSource(document);
//            StringWriter writer = new StringWriter();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.transform(new DOMSource(document), new StreamResult(writer));
//
//            String xmlString = writer.getBuffer().toString();
//            return xmlString;
//        } catch (ParserConfigurationException pce) {
//            pce.printStackTrace();
//        } catch (TransformerException tfe) {
//            tfe.printStackTrace();
//        }
//        return "";
//    }
//
//    public String toString(String orderId){
//        HashMap<String, Pick> picks = orders.get(orderId).getPicks();
//        Set<String> pickKeys = picks.keySet();
//        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-16\"?>\n" +
//                "<ArrayOfTarefa xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">";
//        for (String pickKey : pickKeys) {
//            Pick pick = picks.get(pickKey);
//            xmlString = xmlString + "<Tarefa>\n\t<Ordem>" + orderId+"</Ordem>";
//            xmlString = xmlString + "\t<LinhaOrdem>" + pickKey + "</LinhaOrdem>\n";
//            xmlString = xmlString + pick.toString();
//            xmlString = xmlString + "<Tarefa>\n";
//        }
//        xmlString = xmlString + "</ArrayOfTarefa>";
//        return xmlString;
//    }