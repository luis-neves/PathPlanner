package arwdatastruct;

import orderpicking.GNode;
import orderpicking.Pick;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pathfinder.Graph;
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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

//Parece-nos que os atributos graph e afetação deviam estar numa classe Problem
public class Task {
    private List <GraphNode> route;
    Graph<GNode> graph;
    //Sugestão: picks ou assignment
    //Significado deste atributo
    Hashtable<Integer, ArrayList<Pick>> picksAtNode;

    public Task(List route, Graph<GNode> graph, Hashtable<Integer, ArrayList<Pick>> picksAtNode) {
        this.route = route;
        this.graph = graph;
        this.picksAtNode = picksAtNode;
    }

    public String XMLPath(String agentId, String taskId) {
        try {
            String xmlPath = "";

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("Task");
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
            root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            Attr attrAgent = document.createAttribute("agent-id");
            attrAgent.setValue(agentId);
            root.setAttributeNode(attrAgent);
            //Sugestao: task-id em vez de id
            Attr attRoot = document.createAttribute("id");
            attRoot.setValue(taskId);
            root.setAttributeNode(attRoot);
            document.appendChild(root);

            Element pathElement = document.createElement("Path");

            for (GraphNode node: route){
                String nodeId = node.getId();
                //Sugestão para name: "node-id"
                //Sugestão para nattr: nodeAttr
                Attr nattr = document.createAttribute("id");
                nattr.setValue(nodeId);
                Element nodeXML = document.createElement("Node");
                nodeXML.setAttributeNode(nattr);

                Element position = document.createElement("Position");

                Element pos = document.createElement("x");
                pos.appendChild(document.createTextNode(String.format("%.2f",graph.getNode(nodeId).getX())));
                position.appendChild(pos);

                pos = document.createElement("y");
                pos.appendChild(document.createTextNode(String.format("%.2f",graph.getNode(nodeId).getY())));
                position.appendChild(pos);

                pos = document.createElement("z");
                pos.appendChild(document.createTextNode("0.0"));
                position.appendChild(pos);

                nodeXML.appendChild(position);

                if (picksAtNode.containsKey(Integer.parseInt(nodeId))) {
                     ArrayList<Pick> picks1 = picksAtNode.get(Integer.parseInt(nodeId));

                     for (Pick pick : picks1) {
                         //Elementos todos em inglês
                         Element product = document.createElement("Tarefa");
                         Element lineProd = document.createElement("Ordem");
                         lineProd.appendChild(document.createTextNode(pick.getOrder()));
                         product.appendChild(lineProd);
                         lineProd = document.createElement("LinhaOrdem");
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
                         nodeXML.appendChild(product);
                    }

                }
                pathElement.appendChild(nodeXML);
            }
            root.appendChild(pathElement);

            // create the xml string
            //transform the DOM Object to an XML string
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
