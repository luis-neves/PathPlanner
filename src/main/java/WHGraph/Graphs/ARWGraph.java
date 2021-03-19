package WHGraph.Graphs;


import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import pathfinder.Graph;
import orderpicking.GNode;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ARWGraph {
    private List<ARWGraphNode> ARWGraphNodes;
    private List<Edge> edges;


    public ARWGraph() {
        ARWGraphNodes = new ArrayList<ARWGraphNode>();
        edges = new ArrayList<Edge>();
    }

    public List<Edge> getEdges() {
        return edges;
    }

    private int numberOfgraphNodes = 0;

    public int getNumberOfEdges() {
        return edges.size();//numberOfEdges;
    }

    public void createGraphNode(ARWGraphNode ARWGraphNode) {
        this.ARWGraphNodes.add(ARWGraphNode);
        this.numberOfgraphNodes++; // a GraphNode has been added
    }

    public void createEdge(Edge edge) {
        edge.getStart().addNeighbour(edge);
        edge.getEnd().addNeighbour(edge);
        this.edges.add(edge);
    }

    public int getNumberOfgraphNodes() {
        return this.ARWGraphNodes.size();
    }

    public List<ARWGraphNode> getGraphNodes() {
        return ARWGraphNodes;
    }

    public ARWGraphNode getTrueNode(ARWGraphNode ARWGraphNode) {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getGraphNodeId() == ARWGraphNode.getGraphNodeId()) {
                return getGraphNodes().get(i);
            }
        }
        return null;
    }


    @Override
    public WHGraph.Graphs.ARWGraph clone() {
        WHGraph.Graphs.ARWGraph ARWGraph = new WHGraph.Graphs.ARWGraph();
        for (ARWGraphNode node : getGraphNodes()) {
            ARWGraph.createGraphNode(node.clone());
        }
        for (Edge edge : this.edges) {
            ARWGraph.makeNeighbors(ARWGraph.findNode(edge.getStart().getGraphNodeId()),
                    ARWGraph.findNode(edge.getEnd().getGraphNodeId()), edge.isProduct_line());
        }
        return ARWGraph;
    }

    public void setgraphNodes(List<ARWGraphNode> ARWGraphNodes) {
        this.ARWGraphNodes = ARWGraphNodes;
    }

    public int getMaxIdNodes(){
        int maxid=0;
        for (ARWGraphNode node: ARWGraphNodes) {
            int id=node.getGraphNodeId();
            if (id>maxid)
                maxid=id;

        }
        return maxid;
    }

    public void removeNode(ARWGraphNode node) {
        if (node != null) {
            ARWGraphNodes.remove(node);
            List<Edge> toRemove = new ArrayList<>();
            for (Edge edge : edges) {
                if (edge.getEnd().equals(node) || edge.getStart().equals(node)) {
                    toRemove.add(edge);
                }
            }
            edges.removeAll(toRemove);
            numberOfgraphNodes--;
        }
    }


    public ARWGraphNode findNode(int node_id) {
        for (int i = 0; i < this.getGraphNodes().size(); i++) {
            if (this.getGraphNodes().get(i).getGraphNodeId() == node_id) {
                return getGraphNodes().get(i);
            }
        }
        return null;
    }


    public List<ARWGraphNode> getProducts() {
        List<ARWGraphNode> products = new ArrayList<>();
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT) {
                products.add(getGraphNodes().get(i));
            }
        }
        return products;
    }

    public List<ARWGraphNode> getProducts_Agents() {
        List<ARWGraphNode> products = new ArrayList<>();
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT || getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                products.add(getGraphNodes().get(i));
            }
        }
        return products;
    }


    public int getNumNodes() {
        if (ARWGraphNodes != null) {
            return this.ARWGraphNodes.size();
        } else {
            return 0;
        }
    }

    public ARWGraphNode findClosestNode(float x, float y) {
        ARWGraphNode closest = null;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            ARWGraphNode node = getGraphNodes().get(i);
            if (closest == null || node.getDistance(x, y) <= closest.getDistance(x, y)) {
                closest = node;
            }
        }
        return closest;
    }

    public ARWGraphNode findClosestNode(float x, float y, float sensibility) {
        for (int i = 0; i < getGraphNodes().size(); i++) {
            ARWGraphNode node = getGraphNodes().get(i);
            if (x - sensibility < node.getLocation().getX() && node.getLocation().getX() < x + sensibility) {
                if (y - sensibility < node.getLocation().getY() && node.getLocation().getY() < y + sensibility) {
                    return node;
                }
            }
        }
        return null;
    }

    public int getDimensionX() {
        ARWGraphNode mostRight = null;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (mostRight == null || mostRight.getLocation().getX() < getGraphNodes().get(i).getLocation().getX()) {
                mostRight = getGraphNodes().get(i);
            }
        }
        return Math.round(mostRight.getLocation().getX());
    }

    public int getDimensionY() {
        ARWGraphNode mostBottom = null;
        for (int i = 0; i < getGraphNodes().size(); i++) {
            if (mostBottom == null || mostBottom.getLocation().getY() < getGraphNodes().get(i).getLocation().getY()) {
                mostBottom = getGraphNodes().get(i);
            }
        }
        return Math.round(mostBottom.getLocation().getY());
    }

    public void createGraphNode(float x, float y, GraphNodeType type) {
        int index = -1;
        do {
            index++;
        } while (findNode(index) != null);
        this.ARWGraphNodes.add(new ARWGraphNode(index, x, y, type));
        this.numberOfgraphNodes++;
    }

    public ARWGraphNode getLastNode() {
        if (ARWGraphNodes.size() > 0) {
            return ARWGraphNodes.get(ARWGraphNodes.size() - 1);
        }
        return null;

    }


    public void makeNeighbors(ARWGraphNode start_node, ARWGraphNode end_node, boolean product_line) {
        try {
            Edge e = findEdge(start_node, end_node);
            if (e == null) {
                e = new Edge(start_node, end_node, start_node.getDistance(end_node), edges.size(), product_line);
                e.setEnd(start_node);
                e.setStart(end_node);
                createEdge(e);
            } else {
                e.setProduct_line(product_line);
            }
        } catch (Exception e) {
            System.out.println("Cant make neighbors");
        }
    }

    private Edge findEdge(ARWGraphNode start_node, ARWGraphNode end_node) {
        for (Edge edge : edges) {
            if (edge.getEnd() == end_node && edge.getStart() == start_node) {
                return edge;
            }
            if (edge.getEnd() == start_node && edge.getStart() == end_node) {
                return edge;
            }
        }
        return null;
    }

    public void clear() {
        ARWGraphNodes.clear();
        edges.clear();
        numberOfgraphNodes = 0;
    }

    public double distancetoNeighborEdge(float x, float y){
        double distance=1e6;
        double nx,ny,m;
        double size;
        Edge closesedge=null;
        for (Edge edge : edges) {
            double x1=edge.getStart().getLocation().getX();
            double y1=edge.getStart().getLocation().getY();
            double x2=edge.getEnd().getLocation().getX();
            double y2=edge.getEnd().getLocation().getY();
            if (Math.abs(x2-x1)>1e-3) {
                m = (y2 - y1) / (x2 - x1);
                nx = (2 * x - m * (y1 - y)) / (m * m + 2);
                ny=y1+m*nx;
            }
            else {
                nx=x2;
                ny=y;
            }
            nx=Math.max(Math.min(nx,Math.max(x1,x2)),Math.min(x1,x2));
            ny=Math.max(Math.min(ny,Math.max(y1,y2)),Math.min(y1,y2));
            size=Math.sqrt(Math.pow(nx-x,2)+Math.pow(ny-y,2));
            if (size<distance){
                distance=size;
                closesedge=edge;
            }
        }
        return distance;
    }

    public ARWGraphNode insertNode(ARWGraphNode node){
        double distance=1e6;
        double nx=0,ny=0,bestx=0,besty=0,m;
        double size;
        Edge closesedge=null;
        for (Edge edge : edges) {
            double x1=edge.getStart().getLocation().getX();
            double y1=edge.getStart().getLocation().getY();
            double x2=edge.getEnd().getLocation().getX();
            double y2=edge.getEnd().getLocation().getY();
            if (Math.abs(x2-x1)>1e-3) {
                m = (y2 - y1) / (x2 - x1);
                nx = (2 * node.getX() - m * (y1 - node.getY())) / (m * m + 2);
                ny=y1+m*nx;
            }
            else {
                nx=x2;
                ny=node.getY();
            }

            nx=Math.max(Math.min(nx,Math.max(x1,x2)),Math.min(x1,x2));
            ny=Math.max(Math.min(ny,Math.max(y1,y2)),Math.min(y1,y2));
            size=Math.sqrt(Math.pow(nx-node.getX(),2)+Math.pow(ny-node.getY(),2));

            if (size<distance){
                distance=size;
                closesedge=edge;
                bestx=nx;
                besty=ny;
            }
        }
        if (closesedge!=null) {
            node.setLocation(new Coordinates((float) bestx, (float) besty, 0));

            ARWGraphNode existente = findClosestNode(node.getX(), node.getY());

            if ((Math.abs(existente.getX() -node.getX())>50e-2) || (Math.abs(existente.getY() - node.getY())>50e-2)) {
                Edge novo1 = new Edge(closesedge.getStart(), node, 0, 1);
                createGraphNode(node);
                createEdge(novo1);
                novo1 = new Edge(node,closesedge.getEnd(), 0, 1);
                createEdge(novo1);
                edges.remove(closesedge);

            }
            else
                return existente;

        }
        return node;
    }

    public Edge findClosestEdge(ARWGraphNode ARWGraphNode) {
        Edge closest = null;
        float distance = Float.MAX_VALUE;
        Coordinates oblique_location = null;
        for (Edge edge : edges) {
            if (edge.isProduct_line()) {
                if (edge.isVertical_edge() &&
                        ((ARWGraphNode.getLocation().getY() < edge.getEnd().getLocation().getY() && ARWGraphNode.getLocation().getY() > edge.getStart().getLocation().getY()) ||
                                (ARWGraphNode.getLocation().getY() > edge.getEnd().getLocation().getY() && ARWGraphNode.getLocation().getY() < edge.getStart().getLocation().getY()))) {
                    if (ARWGraphNode.getDistance(edge.getLocation().getX(), ARWGraphNode.getLocation().getY()) < distance) {
                        closest = edge;
                        distance = ARWGraphNode.getDistance(edge.getLocation().getX(), ARWGraphNode.getLocation().getY());
                    }
                } else if (edge.isHorizontal_edge() && ((ARWGraphNode.getLocation().getX() < edge.getEnd().getLocation().getX() && ARWGraphNode.getLocation().getX() > edge.getStart().getLocation().getX()) ||
                        (ARWGraphNode.getLocation().getX() > edge.getEnd().getLocation().getX() && ARWGraphNode.getLocation().getX() < edge.getStart().getLocation().getX()))) {
                    if (ARWGraphNode.getDistance(ARWGraphNode.getLocation().getX(), edge.getLocation().getX()) < distance) {
                        closest = edge;
                        distance = ARWGraphNode.getDistance(edge.getLocation().getX(), ARWGraphNode.getLocation().getY());
                    }
                } else if (edge.isOblique_edge()) {
                    WHGraph.Graphs.ARWGraphNode node_on_line = put_node_in_obliqueLine(edge, ARWGraphNode);
                    if (node_on_line.getDistance(ARWGraphNode) < distance) {
                        closest = edge;
                        distance = node_on_line.getDistance(ARWGraphNode);
                        oblique_location = node_on_line.getLocation();
                    }
                }
            }
        }
        return closest;
    }

    public void createGraphNodeOnClosestEdge(ARWGraphNode ARWGraphNode) {
        Edge closest = null;
        WHGraph.Graphs.ARWGraphNode closest_product_node = null;
        float distance = Float.MAX_VALUE;
        Coordinates oblique_location = null;
        for (Edge edge : edges) {
            if (edge.isProduct_line()) {
                if (edge.isVertical_edge() &&
                        ((ARWGraphNode.getLocation().getY() < edge.getEnd().getLocation().getY() && ARWGraphNode.getLocation().getY() > edge.getStart().getLocation().getY()) ||
                                (ARWGraphNode.getLocation().getY() > edge.getEnd().getLocation().getY() && ARWGraphNode.getLocation().getY() < edge.getStart().getLocation().getY()))) {
                    if (ARWGraphNode.getDistance(edge.getLocation().getX(), ARWGraphNode.getLocation().getY()) < distance) {
                        closest = edge;
                        distance = ARWGraphNode.getDistance(edge.getLocation().getX(), ARWGraphNode.getLocation().getY());
                    }
                } else if (edge.isHorizontal_edge() && ((ARWGraphNode.getLocation().getX() < edge.getEnd().getLocation().getX() && ARWGraphNode.getLocation().getX() > edge.getStart().getLocation().getX()) ||
                        (ARWGraphNode.getLocation().getX() > edge.getEnd().getLocation().getX() && ARWGraphNode.getLocation().getX() < edge.getStart().getLocation().getX()))) {
                    if (ARWGraphNode.getDistance(ARWGraphNode.getLocation().getX(), edge.getLocation().getX()) < distance) {
                        closest = edge;
                        distance = ARWGraphNode.getDistance(edge.getLocation().getX(), ARWGraphNode.getLocation().getY());
                    }
                } else if (edge.isOblique_edge()) {
                    WHGraph.Graphs.ARWGraphNode node_on_line = put_node_in_obliqueLine(edge, ARWGraphNode);
                    if (node_on_line.getDistance(ARWGraphNode) < distance) {
                        closest = edge;
                        distance = node_on_line.getDistance(ARWGraphNode);
                        oblique_location = node_on_line.getLocation();
                    }
                }
            }
        }
        for (WHGraph.Graphs.ARWGraphNode node : getGraphNodes()) {
            if (node.contains_product()) {
                if (node.getDistance(ARWGraphNode) < distance) {
                    closest = null;
                    closest_product_node = node;
                }
            }
        }
        if (closest != null) {
            if (closest.isVertical_edge())
                ARWGraphNode.getLocation().setX(closest.getLocation().getX());
            if (closest.isHorizontal_edge()) {
                ARWGraphNode.getLocation().setY(closest.getLocation().getY());
            }
            if (closest.isOblique_edge()) {
                ARWGraphNode.setLocation(oblique_location);
            }
            this.ARWGraphNodes.add(ARWGraphNode);
            this.numberOfgraphNodes++;
        } else if (closest_product_node != null) {
            ARWGraphNode.setLocation(closest_product_node.getLocation());
            this.ARWGraphNodes.add(ARWGraphNode);
            this.numberOfgraphNodes++;
        }
    }

    private ARWGraphNode put_node_in_obliqueLine(Edge edge, ARWGraphNode ARWGraphNode) {
        float x1 = edge.getStart().getLocation().getX();
        float x2 = edge.getEnd().getLocation().getX();
        float y1 = edge.getStart().getLocation().getY();
        float y2 = edge.getEnd().getLocation().getY();

        if (x2 < x1) {
            float backup_x = x1;
            float backup_y = y1;
            x1 = x2;
            x2 = backup_x;
            y1 = y2;
            y2 = backup_y;
        }


        double m = (y2 - y1) / (x2 - x1);
        double b = y1 - m * (x1);


        float x = ARWGraphNode.getLocation().getX();
        float y = ARWGraphNode.getLocation().getY();

        double mi = (-1 / m);
        double bi = y - mi * (x);

        double new_x = (bi - b) / (m - mi);
        double new_y = mi * new_x + bi;

        if (x1 < new_x && new_x < x2) {
            return new ARWGraphNode(this.getNumNodes(), (float) new_x, (float) new_y, GraphNodeType.PRODUCT);
        } else {
            return null;
        }
    }

    public Graph getPathGraph(){


        Set<GNode> nos = new HashSet<>();
        Map<String, Set<String>> arestas = new HashMap<>();

        for (ARWGraphNode arwnode: getGraphNodes())
        {
            nos.add(new GNode(Integer.toString(arwnode.getGraphNodeId()), Integer.toString(arwnode.getGraphNodeId()),
                    arwnode.getX(), arwnode.getY()));
        }

        Set <String> conj;
        for  (Edge edge: edges){
            if ((conj=arestas.get(Integer.toString(edge.getIdOfStartGraphNode())))!=null)
                conj.add(Integer.toString(edge.getIdOfEndGraphNode()));

            else
                arestas.put(Integer.toString(edge.getIdOfStartGraphNode()),
                    Stream.of(Integer.toString(edge.getIdOfEndGraphNode())).collect(Collectors.toSet()));

            if ((conj=arestas.get(Integer.toString(edge.getIdOfEndGraphNode())))!=null)
                conj.add(Integer.toString(edge.getIdOfStartGraphNode()));
            else
                arestas.put(Integer.toString(edge.getIdOfEndGraphNode()),
                    Stream.of(Integer.toString(edge.getIdOfStartGraphNode())).collect(Collectors.toSet()));
        }

        return new Graph<>(nos, arestas);
    }


    public void readGraphFile(File file) {
        clear();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            Element graphElement = doc.getDocumentElement();
            for (int i = 0; i < graphElement.getChildNodes().getLength(); i++) {
                if (graphElement.getChildNodes().item(i).getNodeName().equals("Nodes")) {
                    ARWGraphNodes = parseNodes(graphElement.getChildNodes().item(i));
                }
            }
            setgraphNodes(ARWGraphNodes);
            for (int i = 0; i < graphElement.getChildNodes().getLength(); i++) {
                if (graphElement.getChildNodes().item(i).getNodeName().equals("Edges")) {
                    edges = parseEdges(graphElement.getChildNodes().item(i));
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    private List<ARWGraphNode> parseNodes(Node item) {
        List<ARWGraphNode> nodes = new ArrayList<>();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            if (item.getChildNodes().item(i).getNodeName().equals("Node")) {
                String[] loc = item.getChildNodes().item(i).getAttributes().getNamedItem("loc").getNodeValue().split(",");

                ARWGraphNode node = new ARWGraphNode(Integer.parseInt(item.getChildNodes().item(i).getAttributes().getNamedItem("id").getNodeValue()),
                        Float.parseFloat(loc[0]), Float.parseFloat(loc[1]),
                        Float.parseFloat(loc[2]),
                        GraphNodeType.valueOf(item.getChildNodes().item(i).getAttributes().getNamedItem("type").getNodeValue()));
                if (Boolean.parseBoolean(item.getChildNodes().item(i).getAttributes().getNamedItem("contains_product").getNodeValue())) {
                    node.setContains_product(true);
                }
                nodes.add(node);
            }
        }
        return nodes;
    }

    private List<Edge> parseEdges(Node item) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            if (item.getChildNodes().item(i).getNodeName().equals("Edge")) {
                ARWGraphNode end = findNode(Integer.parseInt(item.getChildNodes().item(i).getAttributes().item(0).getNodeValue()));
                Boolean product_line = Boolean.parseBoolean(item.getChildNodes().item(i).getAttributes().item(1).getNodeValue());
                ARWGraphNode start = findNode(Integer.parseInt(item.getChildNodes().item(i).getAttributes().item(2).getNodeValue()));
                makeNeighbors(start, end, product_line);
            }
        }
        return getEdges();
    }


    public String generateXMLGraphString() {
        String xmlString = "";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Graph");

            doc.appendChild(rootElement);
            //Node List
            Element nodes = doc.createElement("Nodes");
            for (ARWGraphNode node : getGraphNodes()) {
                Element nodeElement = doc.createElement("Node");
                // set attribute to node element
                Attr attr_id = doc.createAttribute("id");
                attr_id.setValue(node.getGraphNodeId() + "");
                nodeElement.setAttributeNode(attr_id);

                Attr attr_type = doc.createAttribute("type");
                attr_type.setValue(node.getType() + "");
                nodeElement.setAttributeNode(attr_type);
                Attr contains_product = doc.createAttribute("contains_product");
                contains_product.setValue(node.contains_product() + "");
                nodeElement.setAttributeNode(contains_product);
                Attr attr_loc = doc.createAttribute("loc");
                attr_loc.setValue(node.getLocation().printOnlyValues());
                nodeElement.setAttributeNode(attr_loc);
                nodes.appendChild(nodeElement);
            }
            rootElement.appendChild(nodes);

            Element edges = doc.createElement("Edges");
            for (Edge edge : getEdges()) {
                Element edgeElement = doc.createElement("Edge");

                Attr attr_start = doc.createAttribute("start");
                attr_start.setValue(edge.getStart().getGraphNodeId() + "");
                edgeElement.setAttributeNode(attr_start);

                Attr attr_end = doc.createAttribute("end");
                attr_end.setValue(edge.getEnd().getGraphNodeId() + "");
                edgeElement.setAttributeNode(attr_end);

                Attr attr_product_line = doc.createAttribute("product_line");
                attr_product_line.setValue(edge.isProduct_line() + "");
                edgeElement.setAttributeNode(attr_product_line);

                edges.appendChild(edgeElement);
            }
            rootElement.appendChild(edges);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = tf.newTransformer();

            StringWriter writer = new StringWriter();

            //transform document to string
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            xmlString = writer.getBuffer().toString();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return xmlString;

    }



}
