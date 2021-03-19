package ARWDataStruct;

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

public class Task {

    List <GraphNode> route;
    Graph<GNode> graph;
    Hashtable<Integer, ArrayList<Pick>> afetacao;

    public Task(List route, Graph<GNode> graph, Hashtable<Integer, ArrayList<Pick>> afetacao) {
        this.route = route;
        this.graph = graph;
        this.afetacao = afetacao;
    }


    public String XMLPath(String agentid, String taskid) {
        try {

                String xmlpath = "";

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

                Document document = documentBuilder.newDocument();

                // root element
                Element root = document.createElement("Task");

                root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
                root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                Attr attrAgent = document.createAttribute("agent-id");

                attrAgent.setValue(agentid);
                root.setAttributeNode(attrAgent);
                Attr attRoot = document.createAttribute("id");
                attRoot.setValue(taskid);
                root.setAttributeNode(attRoot);
                document.appendChild(root);

               //

                Element pathElement = document.createElement("Path");
                //
                 for (GraphNode node: route){
                     String nodeid=node.getId();
                     Attr nattr = document.createAttribute("id");
                     nattr.setValue(nodeid);
                     Element nodexml = document.createElement("Node");
                     nodexml.setAttributeNode(nattr);

                     Element position = document.createElement("Position");

                     Element pos= document.createElement("x");
                     pos.appendChild(document.createTextNode(String.format("%.2f",graph.getNode(nodeid).getX())));
                     position.appendChild(pos);

                     pos= document.createElement("y");
                     pos.appendChild(document.createTextNode(String.format("%.2f",graph.getNode(nodeid).getY())));
                     position.appendChild(pos);

                     pos= document.createElement("z");
                     pos.appendChild(document.createTextNode("0.0"));
                     position.appendChild(pos);
                     nodexml.appendChild(position);


                     if (afetacao.containsKey(Integer.parseInt(nodeid))) {
                         ArrayList<Pick> picks1 = afetacao.get(Integer.parseInt(nodeid));

                         for (Pick pick : picks1) {
                             Element product = document.createElement("Tarefa");
                             Element linprod = document.createElement("Ordem");
                             linprod.appendChild(document.createTextNode(pick.getOrder()));
                             product.appendChild(linprod);
                             linprod = document.createElement("LinhaOrdem");
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
                             nodexml.appendChild(product);

                         }

                     }
                     pathElement.appendChild(nodexml);

                }
                root.appendChild(pathElement);


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
