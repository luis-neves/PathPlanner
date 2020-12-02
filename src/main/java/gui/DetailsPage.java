package gui;

import com.sun.org.apache.xpath.internal.objects.XString;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import picking.Picking;
import utils.Graphs.*;
import utils.warehouse.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DetailsPage extends JFrame {

    private static final int GRID_TO_PANEL_GAP = 20;
    private static final int MAX_WIDTH = 1600;
    private static final int MAX_HEIGHT = 800;
    private static final int LINE_PIXEL_SENSIBILITY = 20;
    private static final int NODE_SIZE = 5;
    public Line_Type line_type = Line_Type.SIMPLE;
    public Node_Action node_action = Node_Action.DRAW;
    public PaintSurface surface;
    PrefabManager prefabManager;
    Graph graph = new Graph();

    //Where the GUI is created:
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;

    public DetailsPage(PrefabManager prefabManager) throws HeadlessException {
        this.prefabManager = prefabManager;
        this.setTitle("Details");
        prefabManager.fixSizesToInteger();

        this.setSize(new Dimension(Math.round(prefabManager.config.getWidth()) + (int) prefabManager.config.getWidth() / 2, Math.round(prefabManager.config.getDepth()) + (int) prefabManager.config.getDepth() / 2));
        //prefabManager.changeAxis();
        //prefabManager.fixRotation(360);
        HashMap<Integer, LinkedList<Shape>> shapes = prefabManager.generateShapes();
        setLayout(new BorderLayout());
        setupMenuBar(graph);
        surface = new PaintSurface(shapes, prefabManager, graph);

        JPanel panelButtonsNorth = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel product_text = new JLabel("Line Type");
        panelButtonsNorth.add(product_text, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JButton buttonSimpleLine = new JButton("Simple");
        panelButtonsNorth.add(buttonSimpleLine, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        JButton buttonProductLine = new JButton("Product");
        panelButtonsNorth.add(buttonProductLine, gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        JButton buttonExitLine = new JButton("Exit");
        panelButtonsNorth.add(buttonExitLine, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel node_action_label = new JLabel("Node Action");
        panelButtonsNorth.add(node_action_label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JButton button_draw = new JButton("Draw");
        panelButtonsNorth.add(button_draw, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        JButton button_remove = new JButton("Remove");
        panelButtonsNorth.add(button_remove, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        JButton button_generateProduct = new JButton("Generate Product");
        panelButtonsNorth.add(button_generateProduct, gbc);


        gbc.gridwidth = 2;

        buttonSimpleLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                line_type = Line_Type.SIMPLE;
            }
        });
        buttonProductLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                line_type = Line_Type.PRODUCT;
            }
        });
        buttonExitLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                line_type = Line_Type.EXIT;
            }
        });

        button_draw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                node_action = Node_Action.DRAW;
            }
        });
        button_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                node_action = Node_Action.REMOVE;
            }
        });

        button_generateProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                node_action = Node_Action.GENERATE_PRODUCT;
            }
        });

        add(panelButtonsNorth, BorderLayout.NORTH);
        add(surface, BorderLayout.CENTER);
        this.setVisible(true);

        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    graph.removeNode(graph.getLastNode());
                    surface.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });

    }

    private void setupMenuBar(Graph graph) {
        menuBar = new JMenuBar();

//Build the first menu.
        menu = new JMenu("Graph");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "File options");
        menuBar.add(menu);

//a group of JMenuItems
        menuItem = new JMenuItem("Export",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Export graph");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //SAVE GRAPH XML
                exportGraph(graph);
            }
        });
        menuItem = new JMenuItem("Import",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Import graph");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Load GRAPH XML
                LoadGraph();
                surface.repaint();
            }
        });
        menu.add(menuItem);
        this.setJMenuBar(menuBar);
    }

    private void LoadGraph() {

        JFileChooser fc = new JFileChooser(new java.io.File("."));
        fc.setSelectedFile(new File("graph.xml"));
        int returnVal = fc.showOpenDialog(this);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                readGraphFile(file);
            }
        } catch (NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }


    }

    public void readGraphFile(File file) {
        List<GraphNode> graphNodes = new ArrayList<>();
        List<Edge> edgeList = new ArrayList<>();
        graph.clear();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            Element graphElement = doc.getDocumentElement();
            for (int i = 0; i < graphElement.getChildNodes().getLength(); i++) {
                if (graphElement.getChildNodes().item(i).getNodeName().equals("Nodes")) {
                    graphNodes = parseNodes(graphElement.getChildNodes().item(i));
                }
            }
            graph.setgraphNodes(graphNodes);
            for (int i = 0; i < graphElement.getChildNodes().getLength(); i++) {
                if (graphElement.getChildNodes().item(i).getNodeName().equals("Edges")) {
                    edgeList = parseEdges(graphElement.getChildNodes().item(i));
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private List<Edge> parseEdges(Node item) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            if (item.getChildNodes().item(i).getNodeName().equals("Edge")) {
                GraphNode end = graph.findNode(Integer.parseInt(item.getChildNodes().item(i).getAttributes().item(0).getNodeValue()));
                Boolean product_line = Boolean.parseBoolean(item.getChildNodes().item(i).getAttributes().item(1).getNodeValue());
                GraphNode start = graph.findNode(Integer.parseInt(item.getChildNodes().item(i).getAttributes().item(2).getNodeValue()));
                graph.makeNeighbors(start, end, product_line);
            }
        }
        return graph.getEdges();
    }

    private List<GraphNode> parseNodes(Node item) {
        List<GraphNode> nodes = new ArrayList<>();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            if (item.getChildNodes().item(i).getNodeName().equals("Node")) {
                String[] loc = item.getChildNodes().item(i).getAttributes().getNamedItem("loc").getNodeValue().split(",");

                GraphNode node = new GraphNode(Integer.parseInt(item.getChildNodes().item(i).getAttributes().getNamedItem("id").getNodeValue()),
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

    private void exportGraph(Graph graph) {
        JFileChooser fc = new JFileChooser(new java.io.File("."));
        fc.setSelectedFile(new File("graph.xml"));
        int returnVal = fc.showSaveDialog(this);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                FileWriter fw = new FileWriter(fc.getSelectedFile());
                fw.write(generateXMLGraphString(graph));
                fw.close();
            }
        } catch (NoSuchElementException | IOException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateXMLGraphString(Graph graph) {
        String xmlString = "";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Graph");
            Attr attr_x = doc.createAttribute("amplify_x");
            attr_x.setValue(PrefabManager.AMPLIFY_X + "");
            rootElement.setAttributeNode(attr_x);

            Attr attr_y = doc.createAttribute("amplify_y");
            attr_y.setValue(PrefabManager.AMPLIFY_Y + "");
            rootElement.setAttributeNode(attr_y);

            doc.appendChild(rootElement);
            //Node List
            Element nodes = doc.createElement("Nodes");
            for (GraphNode node : graph.getGraphNodes()) {
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
            for (Edge edge : graph.getEdges()) {
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


    private class PaintSurface extends JComponent {
        private HashMap<Integer, LinkedList<Shape>> shapes;
        private LinkedList<Shape> drawables;
        private Graph graph;

        Point startDrag, endDrag;
        GraphNode start_node;
        GraphNode end_node;

        public PaintSurface(HashMap<Integer, LinkedList<Shape>> shapes, PrefabManager prefabManager, Graph graph) {
            this.shapes = shapes;
            drawables = new LinkedList<>();
            this.graph = graph;
            //WALLS
            Shape r = makeLine(0, Math.round(prefabManager.config.getDepth()), Math.round(prefabManager.config.getWidth()), Math.round(prefabManager.config.getDepth()));
            drawables.add(r);
            r = makeLine(Math.round(prefabManager.config.getWidth()), 0, Math.round(prefabManager.config.getWidth()), Math.round(prefabManager.config.getDepth()));
            drawables.add(r);


            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startDrag = new Point(e.getX(), e.getY());
                    endDrag = startDrag;
                    if (node_action == Node_Action.REMOVE) {
                        GraphNode node = graph.findClosestNode(e.getX(), e.getY(), LINE_PIXEL_SENSIBILITY);
                        if (node != null) {
                            graph.removeNode(node);
                        }
                    } else if (node_action == Node_Action.GENERATE_PRODUCT) {
                        find_rack_gen_product(e.getX(), e.getY());
                    }
                    repaint();
                }

                public void mouseReleased(MouseEvent e) {
                    if (startDrag.x == e.getX() && startDrag.y == e.getY() || node_action == Node_Action.REMOVE || node_action == Node_Action.GENERATE_PRODUCT) {
                        if (line_type == Line_Type.PRODUCT && startDrag.x == e.getX() && startDrag.y == e.getY()) {
                            GraphNode node = graph.findClosestNode(startDrag.x, startDrag.y, LINE_PIXEL_SENSIBILITY * 2);
                            if (node != null)
                                node.setContains_product(true);
                        }
                    } else {
                        GraphNode node = graph.findClosestNode(startDrag.x, startDrag.y, LINE_PIXEL_SENSIBILITY * 2);
                        if (node == null) {
                            graph.createGraphNode(startDrag.x, startDrag.y, GraphNodeType.SIMPLE);
                            start_node = graph.getLastNode();
                        } else {
                            start_node = node;
                            startDrag = new Point((int) start_node.getLocation().getX(), (int) start_node.getLocation().getY());
                        }

                        int x1 = startDrag.x;
                        int y1 = startDrag.y;
                        int x2 = e.getX();
                        int y2 = e.getY();
                        if (Math.abs(startDrag.x - e.getX()) < LINE_PIXEL_SENSIBILITY) {
                            x2 = x1;
                        }
                        if (Math.abs(y1 - y2) < LINE_PIXEL_SENSIBILITY) {
                            y2 = y1;
                        }
                        endDrag.x = x2;
                        endDrag.y = y2;

                        node = graph.findClosestNode(endDrag.x, endDrag.y, LINE_PIXEL_SENSIBILITY * 2);
                        if (node == null) {
                            graph.createGraphNode(endDrag.x, endDrag.y, line_type == Line_Type.EXIT ? GraphNodeType.EXIT : GraphNodeType.SIMPLE);
                            end_node = graph.getLastNode();
                        } else {
                            end_node = node;
                            endDrag.x = (int) end_node.getLocation().getX();
                            endDrag.y = (int) end_node.getLocation().getY();
                            node.setType(line_type == Line_Type.EXIT ? GraphNodeType.EXIT : GraphNodeType.SIMPLE);
                            endDrag = new Point((int) end_node.getLocation().getX(), (int) end_node.getLocation().getY());
                        }
                        graph.makeNeighbors(start_node, end_node, line_type == Line_Type.PRODUCT);
                        //Shape r = makeLine(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                        //drawables.add(r);
                        startDrag = null;
                        endDrag = null;
                        repaint();
                    }
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    endDrag = new Point(e.getX(), e.getY());
                    repaint();
                }
            });
        }

        private void find_rack_gen_product(int x, int y) {
            Rack rack = prefabManager.findRack(x, y, LINE_PIXEL_SENSIBILITY);
            if (rack != null) {
                int new_x = Math.round((int) rack.getShape().getBounds().getCenterX());
                int new_y = Math.round((int) rack.getShape().getBounds().getCenterY());
                int id = Integer.parseInt(rack.getCode().split("RC")[1]);
                graph.createGraphNodeOnClosestEdge(new GraphNode(id, new_x, new_y, GraphNodeType.PRODUCT));
            }
        }

        private void paintBackground(Graphics2D g2) {
            g2.setPaint(Color.LIGHT_GRAY);
            for (int i = 0; i < getSize().width; i += 10) {
                Shape line = new Line2D.Float(i, 0, i, getSize().height);
                g2.draw(line);
            }

            for (int i = 0; i < getSize().height; i += 10) {
                Shape line = new Line2D.Float(0, i, getSize().width, i);
                g2.draw(line);
            }


        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paintBackground(g2);
            Color[] colors = {Color.CYAN, Color.LIGHT_GRAY};
            int colorIndex = 0;

            g2.setStroke(new BasicStroke(2));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            for (Map.Entry<Integer, LinkedList<Shape>> entry : shapes.entrySet()) {
                Integer color_index = entry.getKey();
                LinkedList<Shape> shapes = entry.getValue();
                for (Shape s : shapes) {
                    g2.setPaint(Color.BLACK);
                    g2.draw(s);
                    g2.setPaint(colors[color_index]);
                    g2.fill(s);
                }
            }
            //width = x
            //depth = y

            for (Shape s : drawables) {
                g2.setPaint(Color.BLACK);
                g2.draw(s);
                //g2.setPaint(Color.RED);
                g2.fill(s);
            }
            for (GraphNode node : graph.getGraphNodes()) {
                g2.setPaint(Color.BLACK);
                if (node.contains_product())
                    g2.setPaint(Color.BLUE);
                g2.drawOval((int) node.getLocation().getX() - (NODE_SIZE / 2), (int) node.getLocation().getY() - (NODE_SIZE / 2), NODE_SIZE, NODE_SIZE);
                g2.drawString(node.printName(), (int) node.getLocation().getX() + (NODE_SIZE), (int) node.getLocation().getY() - (NODE_SIZE));
            }
            for (Edge e : graph.getEdges()) {
                Shape r = makeLine((int) e.getStart().getLocation().getX(), (int) e.getStart().getLocation().getY(), (int) e.getEnd().getLocation().getX(), (int) e.getEnd().getLocation().getY());
                g2.setPaint(e.isProduct_line() ? Color.BLUE : Color.DARK_GRAY);
                g2.draw(r);
            }

            if (startDrag != null && endDrag != null) {
                g2.setPaint(Color.DARK_GRAY);
                Shape r = makeLine(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                g2.draw(r);
            }
        }

        private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
            return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        }

        private Line2D.Float makeLine(int x1, int y1, int x2, int y2) {
            if (isSensibleX(x1, x2)) {
                x2 = x1;
            }
            if (isSensibleY(y1, y2)) {
                y2 = y1;
            }
            return new Line2D.Float(x1, y1, x2, y2);
        }

        private boolean isSensibleX(int x1, int x2) {
            return Math.abs(x1 - x2) < LINE_PIXEL_SENSIBILITY;
        }

        private boolean isSensibleY(int y1, int y2) {
            return Math.abs(y1 - y2) < LINE_PIXEL_SENSIBILITY;
        }

    }

    private enum Node_Action {
        DRAW, REMOVE, GENERATE_PRODUCT
    }
}
