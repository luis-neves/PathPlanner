package gui;

import armazem.AStar;
import armazem.Environment;
import armazem.EnvironmentListener;
import classlib.CommunicationManager;
import classlib.TopicsConfiguration;
import classlib.Util;
import clustering.Clustering;
import communication.CommunicationVariables;
import communication.MyCallbacks;
import communication.Operator;
import ga.GASingleton;
import ga.KMeans.AstarDistance;
import ga.KMeans.MyCluster;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.tabbedui.VerticalLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import picking.Item;
import utils.Graphs.*;
import utils.warehouse.*;
import weka.clusterers.SimpleKMeans;
import weka.core.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class SimulationPanel extends JPanel {

    private static final int MAX_WEIGHT = 500;
    private static final int MAX_KMEANS_ITERATIONS = 20;
    private static int SLEEP_MILLIS = 10; // modify to speed up the simulation
    private static final int FIXED_CELL_SIZE = 10;
    private static int CELL_SIZE = 10;
    private int NODE_SIZE = 5;
    private int SIMPLE_NODE_SIZE = 2;
    private int MARGIN = 15;

    private static final int GRID_TO_PANEL_GAP = 20;

    public static Environment environment;
    public static EnvironmentNodeGraph environmentNodeGraph;
    private Image image;
    private Graphics2D gfx;
    private JPanel environmentPanel = new JPanel();


    JButton jButtonRun = new JButton("Simulate Environment");
    JButton buttonRunFromFile = new JButton("Open Environment File");
    JButton buttonRunXML = new JButton("Modelo");
    JButton buttonZoomIN = new JButton("+");
    JButton buttonZoomOUT = new JButton("-");
    JButton buttonImportGraph = new JButton("Import Graph");
    JButton buttonRunNodeGraph = new JButton("NG");
    JButton buttonRandomProblem = new JButton("RNG");
    JButton buttonRandomProblemSeed = new JButton("LNG");
    JButton buttonVersionTest = new JButton("V1.0");
    private Graph graph;
    private Graph problemGraph;
    private int seed = 0;
    private int num_rows = 8;
    private int num_agents = 3;
    private int num_products = 21;
    private boolean stop = false;
    private int interruptionIndex = -1;
    private List<IterativeAgent> iterativeAgents = null;
    private float AMPLIFY = 0.2f;

    public SimulationPanel() {
        //environmentPanel.setPreferredSize(new Dimension(N * CELL_SIZE + GRID_TO_PANEL_GAP * 2, N * CELL_SIZE + GRID_TO_PANEL_GAP * 2));
        environmentPanel.setPreferredSize(new Dimension(800, 800));

        JPanel panelButtonsNorth = new JPanel(new GridLayout());
        setLayout(new BorderLayout());
        ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.add(environmentPanel);
        add(scrollPane, BorderLayout.CENTER);
        panelButtonsNorth.add(buttonZoomIN);
        panelButtonsNorth.add(buttonImportGraph);
        panelButtonsNorth.add(buttonZoomOUT);
        JPanel panelButtons = new JPanel(new GridLayout());
        //panelButtons.add(jButtonRun);
        //panelButtons.add(buttonRunFromFile);
        panelButtons.add(buttonRunXML);
        panelButtons.add(buttonRunNodeGraph);
        panelButtons.add(buttonRandomProblem);
        panelButtons.add(buttonRandomProblemSeed);
        panelButtons.add(buttonVersionTest);
        add(panelButtons, BorderLayout.SOUTH);
        add(panelButtonsNorth, BorderLayout.NORTH);

        jButtonRun.addActionListener(new SimulationPanel_jButtonRun_actionAdapter(this));
        buttonRunFromFile.addActionListener(new SimulationPanel_jButtonRunFromFile_actionAdapter(this));
        buttonRunXML.addActionListener(new SimulationPanel_jButtonRunFromXML_actionAdapter(this));
        buttonRunNodeGraph.addActionListener(new SimulationPanel_jButtonRunNodeGraph_actionAdapter(this));
        buttonRandomProblem.addActionListener(new SimulationPanel_jButtonGenRandNodeGraph_actionAdapter(this));
        buttonRandomProblemSeed.addActionListener(new SimulationPanel_jButtonGenRandNodeGraphSeed_actionAdapter(this));
        buttonVersionTest.addActionListener(new SimulationPanel_jbuttonVersionTest_actionAdapter(this));
        buttonZoomIN.addActionListener(new SimulationPanel_jButtonZoomIn_actionAdapter(this));
        buttonZoomOUT.addActionListener(new SimulationPanel_jButtonZoomOut_actionAdapter(this));
        buttonImportGraph.addActionListener(new SimulationPanel_jButtonImportGraph_actionAdapter(this));
        GASingleton.getInstance().setSimulationPanel(this);

    }


    public void jButtonRun_actionPerformed(ActionEvent e) {
        GASingleton.getInstance().setNodeProblem(false);
        buildImage(environment);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                //environment.run();
                return null;
            }
        };
        worker.execute();
    }

    public void jButtonZoomIn_actionPerformed(ActionEvent e) {
        //ZOOM IN
        AMPLIFY += 0.2f;
        problemGraph.amplify(AMPLIFY);
        image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
        gfx = (Graphics2D) image.getGraphics();
        draw(problemGraph, false, this.gfx, this.image);
    }

    public void jButtonZoomOut_actionPerformed(ActionEvent e) {
        //ZOOM OUT
        AMPLIFY -= 0.2f;
        problemGraph.amplify(AMPLIFY);
        image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
        gfx = (Graphics2D) image.getGraphics();
        draw(problemGraph, false, this.gfx, this.image);
    }

    public void jButtonImportGraph_actionPerformed(ActionEvent e) {
        //IMPORT GRAPH
        JFileChooser fc = new JFileChooser(new java.io.File("."));
        fc.setSelectedFile(new File("graph.xml"));
        int returnVal = fc.showOpenDialog(this);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                GASingleton.getInstance().readGraphFile(file);
                image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
                gfx = (Graphics2D) image.getGraphics();
                this.graph = GASingleton.getInstance().getGraph().clone();
                this.problemGraph = graph;
                problemGraph.amplify(AMPLIFY);
                draw(graph, false, this.gfx, this.image);
            }
        } catch (NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void jbuttonVersionTest_actionPerformed(ActionEvent e) {
        try {
            //Test Version
            //0 - Init Comms
            GASingleton.getInstance().getMainFrame().logMessage("Step 1\tCheck Comms", 0);
            /*
            CommunicationManager cm = new CommunicationManager(GASingleton.CLIENT_ID, new TopicsConfiguration(), new MyCallbacks());
            GASingleton.getInstance().setCm(cm);
            */
            GASingleton.getInstance().getMainFrame().logMessage("CLIENT_ID: " + GASingleton.CLIENT_ID, 2);
            GASingleton.getInstance().getMainFrame().logMessage("ERP_ID: " + GASingleton.erpID, 2);
            GASingleton.getInstance().getMainFrame().logMessage("RA_ID: " + GASingleton.RA_ID + "[MAC]", 2);
            GASingleton.getInstance().getMainFrame().logMessage("LOC_APROX_ID: " + GASingleton.LOC_APROX_ID, 2);
            GASingleton.getInstance().getMainFrame().logMessage("MODELADOR_ID: " + GASingleton.MODELADOR_ID, 2);

            //1 - load graph.xml or get from modelador
            GASingleton.getInstance().getMainFrame().logMessage("Step 1.1\tLoad Graph file " + GASingleton.GRAPH_FILE, 0);
            GASingleton.getInstance().readGraphFile(new File(GASingleton.GRAPH_FILE));
            if (GASingleton.getInstance().getGraph() != null) {
                //Load graph image
                image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
                gfx = (Graphics2D) image.getGraphics();
                this.graph = GASingleton.getInstance().getGraph().clone();
                this.problemGraph = graph;
                problemGraph.amplify(AMPLIFY);
                draw(graph, false, this.gfx, this.image);
                GASingleton.getInstance().getMainFrame().logMessage("Complete", 1);
                //
                GASingleton.getInstance().getMainFrame().logMessage("Step 1.2\tLoad Warehouse file " + GASingleton.WAREHOUSE_FILE, 0);
                try {
                    PrefabManager prefabManager = readPrefabXML();
                    if (prefabManager != null) {
                        GASingleton.getInstance().setPrefabManager(prefabManager);
                    } else {
                        GASingleton.getInstance().getMainFrame().logMessage("prefabManager is NULL (Check prefab xml)", 1);
                    }
                } catch (Exception ex) {
                    GASingleton.getInstance().getMainFrame().logMessage("Error reading prefab " + ex.getMessage(), 1);
                }
            } else {
                GASingleton.getInstance().getMainFrame().logMessage("Step 1\tGraph file not found, use details to generate graph file from prefab file.", 0);
                try {
                    PrefabManager prefabManager = readPrefabXML();
                    if (prefabManager != null) {
                        GASingleton.getInstance().setPrefabManager(prefabManager);
                        GASingleton.getInstance().getMainFrame().logMessage("Complete", 1);
                    }
                    GASingleton.getInstance().getMainFrame().logMessage("prefabManager is NULL (Check prefab xml)", 1);
                } catch (Exception ex) {
                    GASingleton.getInstance().getMainFrame().logMessage("Error reading prefab " + ex.getMessage(), 1);
                }
                GASingleton.getInstance().getMainFrame().logMessage("Step 1\tGet Graph from Modelador", 0);

                //GET FROM MODELADOR
                GASingleton.getInstance().getCm().SendMessageAsync(Util.GenerateId(), "request", "updateXML", GASingleton.MODELADOR_ID, "application/xml", "", "1");
            }
            //get products from ERP if operators are available
            GASingleton.getInstance().getMainFrame().logMessage("Step 2\tChecking availability", 0);
            GASingleton.getInstance().getCm().SendMessageAsync(Util.GenerateId(), "request", "getTarefa", "ERP", "PlainText", "Dá-me uma tarefa!", "1");
            //SEND availability
            if (GASingleton.getInstance().getCommunication_variables().getOperators().size() > 0) {

            } else {
                GASingleton.getInstance().getMainFrame().logMessage("No operators available", 1);
                GASingleton.getInstance().getMainFrame().logMessage("Waiting for operators...", 1);
            }
            //send task to an operator
            //cm.SendMessageAsync(Util.GenerateId(), "request", "setRoute", GASingleton.getInstance().getCommunication_variables().getOperators().get(0).getId(), "application/xml", xmlString, "1");

        } catch (Exception ex) {
            GASingleton.getInstance().getMainFrame().logMessage(ex.getMessage(), 0);
        }

    }

    public void draw_problem_graph() {
        image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
        gfx = (Graphics2D) image.getGraphics();
        this.graph = GASingleton.getInstance().getProblemGraph().clone();
        this.problemGraph = graph;
        graph = fixNeighbours_New(graph);
        problemGraph.amplify(AMPLIFY);
        draw(graph, false, this.gfx, this.image);
    }

    public void jButtonRandNodeGraphProblem_actionPerformed(ActionEvent e) {
        //environmentPanel.updateUI();
        Graph graph2;
        if (GASingleton.getInstance().getGraph().getGraphNodes().size() == 0) {
            graph2 = exampleGraph(num_rows);
            graph = graph2.clone();
        } else {
            graph2 = GASingleton.getInstance().getGraph().clone();
            graph = graph2.clone();
        }
        graph2 = randomProblem(graph2, num_agents, num_products, -1);
        graph2 = fixNeighbours_New(graph2);
        problemGraph = graph2;
        problemGraph.amplify(AMPLIFY);
        image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
        gfx = (Graphics2D) image.getGraphics();
        draw(graph2, false, this.gfx, this.image);
    }

    public void generateExperimentGraph(int num_colums, int num_agents, int num_products, int seed) {
        Graph graph2 = exampleGraph(num_colums);
        graph2 = randomProblem(graph2, num_agents, num_products, seed);
        graph2 = fixNeighbours_New(graph2);

        image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
        gfx = (Graphics2D) image.getGraphics();

        problemGraph = graph2;
        draw(graph2, false, this.gfx, this.image);

        //TODO
        //environmentNodeGraph = new EnvironmentNodeGraph(graph2);
        //graph = graph2;
        /*
        List<Item> items = new ArrayList<>();
        List<GraphNode> agents = new ArrayList<>();
        for (int i = 0; i < graph2.getGraphNodes().size(); i++) {
            if (graph2.getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT || graph2.getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                if (graph2.getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                    agents.add(graph2.getGraphNodes().get(i));
                }
                Item item = new Item((graph2.getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT ? "P" : graph2.getGraphNodes().get(i).getType() == GraphNodeType.AGENT ? "A" : "N") + graph2.getGraphNodes().get(i).getGraphNodeId(), graph2.getGraphNodes().get(i));
                items.add(item);
            }
        }
        GASingleton.getInstance().setItems(items);
        GASingleton.getInstance().setLastAgent(agents.get(agents.size() - 1));
        GASingleton.getInstance().setNodeProblem(true);
        */

    }

    private Graph randomProblem(Graph graph, int num_agents, int num_products, int seed) {
        int agentCount = 0;
        if (seed == -1) {
            Random rand = new Random();
            this.seed = rand.nextInt(5000);
        } else {
            this.seed = seed;
        }

        for (int i = 0; i < num_products + num_agents; i++) {
            Random r = new Random(i + this.seed);
            Edge edge;
            do {
                edge = graph.getEdges().get(r.nextInt(graph.getEdges().size()));
            } while (!edge.isProduct_line());
            GraphNode start = edge.getStart();
            GraphNode end = edge.getEnd();
            GraphNode product = new GraphNode(graph.getGraphNodes().size() + 1);
            product.setType(GraphNodeType.PRODUCT);
            if (agentCount != num_agents) {
                product.setType(GraphNodeType.AGENT);
                agentCount++;
            }
            if (edge.getStart().getLocation().getX() == edge.getEnd().getLocation().getX()) {
                float YStart = start.getLocation().getY();
                float YEnd = end.getLocation().getY();
                float result = 0;
                do {
                    try {
                        result = r.nextInt(Math.round(Math.abs(YStart - YEnd))) + (Math.min(YStart, YEnd));
                    } catch (Exception e) {
                        System.out.println();
                    }
                } while (result == YStart || result == YEnd);
                product.setLocation(new Coordenates(edge.getStart().getLocation().getX(), result, 0));
            } else if (edge.getStart().getLocation().getY() == edge.getEnd().getLocation().getY()) {
                float xStart = start.getLocation().getX();
                float xEnd = end.getLocation().getX();
                float result;
                try {
                    do {
                        result = r.nextInt(Math.round(Math.abs(xStart - xEnd))) + (Math.min(xStart, xEnd));
                    } while (result == xStart || result == xEnd);
                    product.setLocation(new Coordenates(result, edge.getStart().getLocation().getY(), 0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (GASingleton.getInstance().isSimulatingWeights()) {
                    product.setWeightPhysical(r.nextInt(MAX_WEIGHT));
                    product.setWeightSupported((r.nextInt(4) + 1) * product.getWeightPhysical());
                }
            } else {
                System.out.println();
                float x1 = start.getLocation().getX();
                float x2 = end.getLocation().getX();
                float y1 = start.getLocation().getY();
                float y2 = end.getLocation().getY();
                float ratio = (y2 - y1) / (x2 - x1);
                float width = Math.abs(x2 - x1);
                int value = r.nextInt(Math.round(width));
                float x = x1 + value;
                float y = y1 + (ratio * value);
                if (x1 < x2)
                    x = x2 + value;
                product.setLocation(new Coordenates(x, y, 0));
                product.getLocation().amplified(AMPLIFY);
                if (GASingleton.getInstance().isSimulatingWeights()) {
                    product.setWeightPhysical(r.nextInt(MAX_WEIGHT));
                    product.setWeightSupported((r.nextInt(4) + 1) * product.getWeightPhysical());
                }
            }
            graph.getGraphNodes().add(product);
            /*
            if (product.getType() == GraphNodeType.AGENT) {
                System.out.println(product.toString());
            }*/
        }

        return graph;
    }

    public void jButtonRandNodeGraphSeedProblem_actionPerformed(ActionEvent e) {
        //SEED
        try {
            //environmentPanel.updateUI();
            image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
            gfx = (Graphics2D) image.getGraphics();
            Graph graph2 = graph.clone();
            graph2 = randomProblem(graph2, num_agents, num_products, seed);
            graph2 = fixNeighbours_New(graph2);
            problemGraph = graph2;
            draw(graph2, false, this.gfx, this.image);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void jButtonRunNodeGraph_actionPerformed(ActionEvent e) {
        try {
            //NODE GRAPH
            GASingleton.getInstance().setNodeProblem(true);
            graph = exampleGraph(6);
            Graph graph2 = this.graph.clone();
            List<GraphNode> nodes = exampleGraphProblem();
            if (GASingleton.getInstance().isSimulatingWeights()) {
                Random r = new Random(seed);
                for (GraphNode n : nodes) {
                    n.setWeightPhysical(r.nextInt(MAX_WEIGHT));
                    n.setWeightSupported((r.nextInt(4) + 1) * n.getWeightPhysical());
                }
            }
            for (int i = 0; i < nodes.size(); i++) {
                graph2.getGraphNodes().add(nodes.get(i));
            }
            //Communicate
            if (GASingleton.getInstance().getCm() != null) {
                GASingleton.getInstance().getCm().SendMessageAsync(Util.GenerateId(), "request", "getAllOrders", GASingleton.erpID, "text/plain", "", "1");
            }
            graph2 = fixNeighbours_New(graph2);
            image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
            gfx = (Graphics2D) image.getGraphics();
            this.problemGraph = graph2;
            draw(graph2, false, this.gfx, this.image);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Graph fixNeighbours_New(Graph graph) {
        for (GraphNode product_agent : graph.getProducts_Agents()) {
            Edge edge = graph.findClosestEdge(product_agent);
            edge.addProduct(product_agent);
        }
        List<Edge> mainEdges = new ArrayList<>(graph.getEdges());
        for (Edge edge : mainEdges) {
            if (edge.getProducts().size() > 0) {
                for (GraphNode product1 : edge.getProducts()) {
                    graph.makeNeighbors(product1, edge.getStart(), true);
                    graph.makeNeighbors(product1, edge.getEnd(), true);
                    for (GraphNode product2 : edge.getProducts()) {
                        if (!product1.equals(product2)) {
                            graph.makeNeighbors(product1, product2, true);
                        }
                    }
                }
            }
        }
        return graph;
    }

    private List<GraphNode> exampleGraphProblem() {
        List<GraphNode> list = new ArrayList<>();


        GraphNode product = new GraphNode(20);
        product.setType(GraphNodeType.AGENT);
        product.setLocation(new Coordenates(170, 40, 0));

        GraphNode product2 = new GraphNode(21);
        product2.setType(GraphNodeType.PRODUCT);
        product2.setLocation(new Coordenates(140, 140, 0));

        GraphNode productt = new GraphNode(25);
        productt.setType(GraphNodeType.PRODUCT);
        productt.setLocation(new Coordenates(110, 20, 0));

        GraphNode product3 = new GraphNode(22);
        product3.setType(GraphNodeType.PRODUCT);
        product3.setLocation(new Coordenates(170, 140, 0));

        GraphNode product4 = new GraphNode(23);
        product4.setType(GraphNodeType.PRODUCT);
        product4.setLocation(new Coordenates(80, 40, 0));

        GraphNode agent = new GraphNode(19);
        agent.setType(GraphNodeType.AGENT);
        agent.setLocation(new Coordenates(110, 100, 0));

        GraphNode agent2 = new GraphNode(24);
        agent2.setType(GraphNodeType.PRODUCT);
        agent2.setLocation(new Coordenates(110, 40, 0));

        GraphNode agent3 = new GraphNode(26);
        agent3.setType(GraphNodeType.PRODUCT);
        agent3.setLocation(new Coordenates(110, 40, 0));

//        for (int i = 0; i < 10; i++){
//            GraphNode productX = new GraphNode(24+i);
//            productX.setType(GraphNodeType.PRODUCT);
//            productX.setLocation(new Coordenates(170, 50 + i*10, 0));
//            list.add(productX);
//        }

        list.add(agent);
        list.add(product);
        list.add(product2);
        list.add(product3);
        list.add(productt);
        list.add(product4);
        list.add(agent2);
        list.add(agent3);
        return list;
    }

    private Graph exampleGraph(int num_rows) {
        Graph graph = new Graph();
        for (int i = 1; i < num_rows + 1; i++) {
            GraphNode ai = new GraphNode(i);
            ai.setLocation(new Coordenates(20 + i * 15 * 2, 160, 0));
            graph.createGraphNode(ai);
        }
        for (int i = 1; i < num_rows + 1; i++) {
            GraphNode bi = new GraphNode(i + num_rows);
            bi.setLocation(new Coordenates(20 + i * 15 * 2, 80, 0));
            graph.createGraphNode(bi);
        }
        for (int i = 1; i < num_rows + 1; i++) {
            GraphNode ci = new GraphNode(i + num_rows * 2);
            ci.setLocation(new Coordenates(20 + i * 15 * 2, 0, 0));
            graph.createGraphNode(ci);
        }
        for (int i = 0; i < num_rows; i++) {
            GraphNode ai = graph.getGraphNodes().get(i);
            GraphNode bi = graph.getGraphNodes().get(i + num_rows);
            GraphNode ci = graph.getGraphNodes().get(i + num_rows * 2);
            if (i < num_rows - 1) {
                GraphNode a2 = graph.getGraphNodes().get(i + 1);
                GraphNode b2 = graph.getGraphNodes().get(i + num_rows + 1);
                GraphNode c2 = graph.getGraphNodes().get(i + (num_rows * 2) + 1);

                Edge a1a2 = new Edge(ai, a2, a2.getLocation().getX() - ai.getLocation().getX(), i + num_rows);
                Edge b1b2 = new Edge(bi, b2, b2.getLocation().getX() - bi.getLocation().getX(), i + num_rows * 2);
                Edge c1c2 = new Edge(ci, c2, c2.getLocation().getX() - ci.getLocation().getX(), i + num_rows * 3);
                if (graph.checkForAvailability()) {
                    graph.createEdge(a1a2);
                    graph.createEdge(b1b2);
                    graph.createEdge(c1c2);
                }
            }
            Edge aibi = new Edge(ai, bi, ai.getLocation().getY() - bi.getLocation().getY(), i, true);
            Edge bici = new Edge(bi, ci, bi.getLocation().getY() - ci.getLocation().getY(), i + num_rows * 4, true);


            if (graph.checkForAvailability()) {
                graph.createEdge(aibi);
                graph.createEdge(bici);
            }
        }
        graph.getGraphNodes().get(graph.getGraphNodes().size() - 1).setType(GraphNodeType.EXIT);

        return graph;
    }

    private void draw(Graph graph, boolean justDraw, Graphics gfx, Image image) {
        List<GraphNode> nodes = graph.getGraphNodes();
        for (int i = 0; i < nodes.size(); i++) {
            GraphNode node = nodes.get(i);
            Point p = new Point(Math.round(node.getLocationAmplified().getX()), Math.round(node.getLocationAmplified().getY()));
            if (node.getType() == GraphNodeType.AGENT) {
                gfx.setColor(node.getCluster() == null ? Color.black : node.getCluster().getColor());
                gfx.fillOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);
            } else if (node.getType() == GraphNodeType.PRODUCT) {
                gfx.setColor(node.getCluster() == null ? Color.blue : node.getCluster().getColor());
                gfx.fillOval(p.x - (NODE_SIZE / 2), p.y + (NODE_SIZE / 2), NODE_SIZE, NODE_SIZE);
            } else if (node.getType() == GraphNodeType.EXIT) {
                gfx.setColor(Color.green);
                gfx.drawOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);
            } else if (node.getType() == GraphNodeType.DELIVERING) {
                gfx.setColor(Color.GRAY);
                gfx.drawOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);
            } else {
                gfx.drawOval(p.x - (SIMPLE_NODE_SIZE / 2), p.y + (SIMPLE_NODE_SIZE / 2), SIMPLE_NODE_SIZE, SIMPLE_NODE_SIZE);
            }
            if (node.getType() != GraphNodeType.DELIVERING && node.getType() != GraphNodeType.SIMPLE) {
                gfx.drawString("" + node.getType().toLetter() + node.getGraphNodeId(), p.x + (NODE_SIZE / 2), p.y + (NODE_SIZE / 2) + MARGIN);
            }
            gfx.setColor(Color.BLACK);
        }
        for (int i = 0; i < graph.getEdges().size(); i++) {
            GraphNode start = graph.getEdges().get(i).getStart();
            GraphNode end = graph.getEdges().get(i).getEnd();
            gfx.drawLine(Math.round(start.getLocationAmplified().getX()), Math.round(start.getLocationAmplified().getY()) + (NODE_SIZE / 2), Math.round(end.getLocationAmplified().getX()), Math.round(end.getLocationAmplified().getY()) + (NODE_SIZE / 2));
            //gfx.drawString("" + (int) graph.getEdges().get(i).getWeight(), x, y);
        }
        //environmentPanel.getGraphics().drawImage(image, 0, 0, this);
        environmentPanel.getGraphics().drawImage(image, GRID_TO_PANEL_GAP, GRID_TO_PANEL_GAP, null);

        if (graph.containsProblem() && !justDraw) {
            environmentNodeGraph = new EnvironmentNodeGraph(graph);
            List<Item> items = new ArrayList<>();
            for (GraphNode product : graph.getProducts()) {
                Item item = new Item(product.printName(), product);
                items.add(item);
            }
            for (int i = 0; i < graph.getAgents().size(); i++) {
                Item item = new Item(graph.getAgents().get(i).printName(), graph.getAgents().get(i));
                items.add(item);
            }
            GASingleton.getInstance().setItems(items);
            GASingleton.getInstance().setLastAgent(graph.getAgents().get(graph.getAgents().size() - 1));
            GASingleton.getInstance().setNodeProblem(true);
        }

        //gfx.transform(new AffineTransform(2, 2, 2, 2, 2, 2));

/*
        try {
            Point p = new Point(20,50);
            gfx.drawOval(p.x - (NODE_SIZE / 2), p.y - (NODE_SIZE / 2), NODE_SIZE, NODE_SIZE);
            gfx.drawString("Node 1",  p.x + (NODE_SIZE / 2), p.y + (NODE_SIZE / 2));
            Point p2 = new Point(80,50);
            gfx.drawOval(p2.x- (NODE_SIZE / 2), p2.y- (NODE_SIZE / 2), NODE_SIZE, NODE_SIZE);
            gfx.drawString("Node 2",  p2.x + (NODE_SIZE / 2), p2.y + (NODE_SIZE /2));
            gfx.drawLine(p.x,p.y,p2.x,p2.y);

            environmentPanel.getGraphics().drawImage(image, 0, 0, this);
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    //
    public void runPath(FitnessResults results) {
        //g.setColor(environment.getCellColor(y, x));
        //g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        //environmentPanel.getGraphics().drawImage(image, GRID_TO_PANEL_GAP, GRID_TO_PANEL_GAP, null);

        results = fixProductsInPath(results);

        try {
            List<IterativeAgent> iterativeAgents;
            Image backup = image;
            Graphics2D gfx2 = (Graphics2D) backup.getGraphics();


            if (this.iterativeAgents == null) {
                iterativeAgents = new ArrayList<>();
                problemGraph.unfixAgentNeighbours();
                for (Map.Entry<GraphNode, List<FitnessNode>> entry : results.getTaskedAgentsFullNodes().entrySet()) {
                    GraphNode agent = entry.getKey();
                    List<FitnessNode> finalpath = entry.getValue();
                    if (!finalpath.isEmpty()) {
                        iterativeAgents.add(new IterativeAgent(agent, finalpath, results.getTaskedAgentsOnly().get(agent)));
                    }
                }
            } else {
                iterativeAgents = this.iterativeAgents;
            }
            for (int i = (interruptionIndex == -1 ? 0 : interruptionIndex); i < results.getTime(); i++) {
                if (stop) {
                    this.interruptionIndex = i;
                    this.iterativeAgents = iterativeAgents;
                    throw new InterruptedException("Interrupted by user");
                } else {
                    for (IterativeAgent iterativeAgent : iterativeAgents) {
                        Coordenates location = iterativeAgent.followObjective();
                        Point p = new Point((int) location.amplified().getX(), (int) location.amplified().getY());
                        draw(problemGraph, true, gfx2, backup);
                        gfx2.setColor(Color.RED);
                        gfx2.fillOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);
                        environmentPanel.getGraphics().drawImage(backup, GRID_TO_PANEL_GAP, GRID_TO_PANEL_GAP, null);
                        image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
                        backup = image;
                        gfx2 = (Graphics2D) backup.getGraphics();
                        gfx2.setColor(Color.BLACK);
                        try {
                            Thread.sleep(SLEEP_MILLIS);
                        } catch (InterruptedException ignore) {
                            return;
                        }
                    }
                }
            }
            this.interruptionIndex = -1;
            this.iterativeAgents = null;
            GASingleton.getInstance().getMainFrame().buttonVisualize.setText("Play");
            GASingleton.getInstance().getMainFrame().setStop(false);
        } catch (Exception e) {
            if (e.getClass().equals(InterruptedException.class)) {
                this.stop = false;
                System.out.println(e.getMessage());
            } else {
                e.printStackTrace();
            }
            return;
        }
    }

    private FitnessResults fixProductsInPath(FitnessResults results) {
        for (Map.Entry<GraphNode, List<FitnessNode>> entry : results.getTaskedAgentsFullNodes().entrySet()) {
            GraphNode agent = entry.getKey();
            List<FitnessNode> finalpath = entry.getValue();
            List<FitnessNode> toRemove = new ArrayList<>();
            for (int i = 0; i < finalpath.size(); i++) {
                if (finalpath.get(i).getNode().getType() == GraphNodeType.PRODUCT && !results.getTaskedAgentsOnly().get(agent).contains(finalpath.get(i).getNode())) {
                    FitnessNode nextNode = finalpath.get(i + 1);
                    nextNode.setCost(nextNode.getCost() + finalpath.get(i).getCost());
                    nextNode.setTime(nextNode.getTime() + finalpath.get(i).getCost());
                    toRemove.add(finalpath.get(i));
                }
            }
            results.getTaskedAgentsFullNodes().get(agent).removeAll(toRemove);
        }
        return results;
    }

    public void setStop(boolean b) {
        this.stop = b;
    }

    public void     incrementTime(int i) {
        this.SLEEP_MILLIS += i;
    }

    public void descrementTime(int i) {
        if (this.SLEEP_MILLIS - i < 0) {
            this.SLEEP_MILLIS = 1;
        } else if (this.SLEEP_MILLIS - i < 5) {
            this.SLEEP_MILLIS -= 1;
        } else {
            this.SLEEP_MILLIS -= i;
        }
    }

    public HashMap<GraphNode, List<GraphNode>> generateClusters(int seed, boolean apply_heuristic) throws Exception {
        try {
            Clustering clustering = new Clustering(problemGraph, MAX_KMEANS_ITERATIONS);
            return clustering.generateClusters(seed, apply_heuristic);
        } catch (Exception ex) {
            System.err.println("Unable to buld Clusterer: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public void runCluster(HashMap<GraphNode, List<GraphNode>> taskMap) {
        FitnessResults results = calculatePath(taskMap);
        results = EnvironmentNodeGraph.checkColisions2(results);
        results.setTaskedAgentsOnly(taskMap);
        GASingleton.getInstance().getBestIndividualPanel().textArea.setText(results.printTaskedAgents());
        GASingleton.getInstance().setBestInRun(results);
            /*
            for (int i = 0; i < problemGraph.getAgents().size(); i++) {
                List<GraphNode> clusterList = clusterMap.get(kmeans.getAssignments()[kmeans.getAssignments().length - 1 - i]);
                problemGraph.getAgents().get(problemGraph.getAgents().size() - 1 - i).setCluster(findMyCluster(kmeans.getAssignments()[kmeans.getAssignments().length - 1 - i], clusters));
                clusterList.add(problemGraph.getAgents().get(problemGraph.getAgents().size() - 1 - i));
            }
            HashMap<Integer, ArrayList<GraphNode>> clusterMapFixed = null;
            do {
                clusterMapFixed = fixAgentClusters(clusterMap);
                if (clusterMapFixed != null) {
                    clusterMap = clusterMapFixed;
                }
            } while (clusterMapFixed != null);

            HashMap<GraphNode, List<ProductsWithDistance>> pathMap = new HashMap<>();
            for (Map.Entry<Integer, ArrayList<GraphNode>> entry : clusterMap.entrySet()) {
                Integer cluster = entry.getKey();
                ArrayList<GraphNode> clusterList = entry.getValue();
                clusterList = moveAgentToFront(clusterList);
                GraphNode agent = clusterList.get(0);
                clusterList.remove(agent);
                List<ProductsWithDistance> distances = new ArrayList<>();
                for (GraphNode product : clusterList) {
                    distances.add(new ProductsWithDistance(product, agent.getDistance(product)));
                }
                distances.sort(Comparator.comparing(ProductsWithDistance::getDistance));
                pathMap.put(agent, distances);
                System.out.println(Arrays.toString(distances.toArray()));
            }
            //FitnessResults results = calculatePath(pathMap);
            results = fixRepetedProduct(results);
            GASingleton.getInstance().getBestIndividualPanel().textArea.setText(results.printTaskedAgents());
            GASingleton.getInstance().setBestInRun(results);*/
        Image backup = image;
        Graphics2D gfx2 = (Graphics2D) backup.getGraphics();
        draw(problemGraph, true, gfx2, backup);
    }

    private FitnessResults calculatePath(HashMap<GraphNode, List<GraphNode>> pathMap) {
        Clustering clustering = new Clustering(problemGraph, MAX_KMEANS_ITERATIONS);
        return clustering.calculatePath(pathMap);
    }

    public Graph getProblemGraph() {
        return problemGraph;
    }


    private class IterativeAgent {
        private final List<GraphNode> taskOnly;
        GraphNode agent;
        List<FitnessNode> path;
        FitnessNode currentObjective;
        int objectiveIdx;
        Coordenates location;

        public IterativeAgent(GraphNode agent, List<FitnessNode> path, List<GraphNode> taskOnly) {
            this.agent = agent;
            this.path = path;
            this.currentObjective = path == null ? null : path.get(0);
            this.objectiveIdx = 0;
            this.location = agent.getLocation();
            this.taskOnly = taskOnly;
        }

        public Coordenates followObjective() {
            if (this.location.getX() == currentObjective.getNode().getLocation().getX() && this.location.getY() == this.currentObjective.getNode().getLocation().getY()) {
                //at objective
                if (objectiveIdx < path.size() - 1) {
                    currentObjective = path.get(objectiveIdx + 1);
                    objectiveIdx++;
                    if (path.get(objectiveIdx - 1).getNode().getType() == GraphNodeType.PRODUCT && taskOnly.contains(path.get(objectiveIdx - 1).getNode()) && path.lastIndexOf(path.get(objectiveIdx - 1)) == (objectiveIdx - 1)) {
                        problemGraph.makeDelivering(path.get(objectiveIdx - 1).getNode());
                    }
                }
            }
            if (this.location.getX() < currentObjective.getNode().getLocation().getX()) {
                this.location.setX(this.location.getX() + 1);
            } else if (this.location.getX() > currentObjective.getNode().getLocation().getX()) {
                this.location.setX(this.location.getX() - 1);
            }
            if (this.location.getY() < currentObjective.getNode().getLocation().getY()) {
                this.location.setY(this.location.getY() + 1);
            } else if (this.location.getY() > currentObjective.getNode().getLocation().getY()) {
                this.location.setY(this.location.getY() - 1);
            }
            return this.location;
        }
    }

    public void jButtonRunFromXML_actionPerformed(ActionEvent e) {
        //XML FILE BUTTON ACTION
        PrefabManager prefabManager = readPrefabXML();
        draw_prefabs(prefabManager);

    }

    private PrefabManager readPrefabXML() {
        try {
            File inputFile = new File(GASingleton.WAREHOUSE_FILE);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.print("Root element: ");
            System.out.println(doc.getDocumentElement().getNodeName());
            Element warehouse = doc.getDocumentElement();
            LinkedList prefabList = new LinkedList();
            Config config = new Config();
            for (int i = 0; i < warehouse.getChildNodes().getLength(); i++) {

                if (warehouse.getChildNodes().item(i).getNodeName().equals("xmlInfo")) {
                    XMLInfo xmlInfo = parseXMLInfoNode(warehouse.getChildNodes().item(i));
                    System.out.println(xmlInfo.toString() + "\n");
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("config")) {
                    config = parseConfigNode(warehouse.getChildNodes().item(i));
                    System.out.println(config.toString() + "\n");
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("prefabs")) {
                    prefabList = parsePrefabs(warehouse.getChildNodes().item(i));
                }
            }
            //FILL
            PrefabManager prefabManager = new PrefabManager(prefabList, config);
            //TODO
            for (int i = 0; i < warehouse.getChildNodes().getLength(); i++) {
                if (warehouse.getChildNodes().item(i).getNodeName().equals("racks")) {
                    prefabManager = parseRacks(warehouse.getChildNodes().item(i), prefabManager);
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("structures")) {
                    prefabManager = parseStructuresExtra(warehouse.getChildNodes().item(i), prefabManager);
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("devices")) {
                    //prefabManager = parseDevicesExtra(warehouse.getChildNodes().item(i), prefabManager);
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("markers")) {
                    //prefabManager = parseMarkersExtra(warehouse.getChildNodes().item(i), prefabManager);
                }
            }


            //Collections.sort(prefabList, new CustomComparator());

            prefabManager.fillAllPrefabs();
            /*
            for (Prefab prefab : prefabManager.getAllPrefabs()) {
                System.out.println(prefab.toString());
            }*/

            return prefabManager;
            /*
            for (int i = 0; i < prefabList.size(); i++) {
                System.out.println(prefabList.get(i));
            }*/

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void draw_prefabs(PrefabManager prefabManager) {
        DetailsPage frame = new DetailsPage(prefabManager);
        /*prefabManager.changeAxis();
        prefabManager.fixSizesToInteger();
        prefabManager.fixRotation();*/
        /*
        LinkedList<Shape> shapes = prefabManager.generateShapes();
        image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
        gfx = (Graphics2D) image.getGraphics();
        for (Shape shape :shapes){
            gfx.draw(shape);
        }
        environmentPanel.getGraphics().drawImage(image, GRID_TO_PANEL_GAP, GRID_TO_PANEL_GAP, null);
        */


        //gfx.drawLine(Math.round(start.getLocation().getX() * AMPLIFY_MULTIPLIER), Math.round(start.getLocation().getY() * AMPLIFY_MULTIPLIER) + (NODE_SIZE / 2), Math.round(end.getLocation().getX() * AMPLIFY_MULTIPLIER), Math.round(end.getLocation().getY() * AMPLIFY_MULTIPLIER) + (NODE_SIZE / 2));
        //gfx.drawString("" + (int) graph.getEdges().get(i).getWeight(), x, y);


    }


    private PrefabManager parseStructuresExtra(Node item, PrefabManager prefabManager) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabManager = parseStructuresExtraExtry(item.getChildNodes().item(i), prefabManager);
            }
        }
        return prefabManager;
    }

    private PrefabManager parseMarkersExtra(Node item, PrefabManager prefabManager) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabManager = parseMarkersExtraEntry(item.getChildNodes().item(i), prefabManager);
            }
        }
        return prefabManager;
    }

    private PrefabManager parseDevicesExtra(Node item, PrefabManager prefabManager) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabManager = parseDevicesExtraEntry(item.getChildNodes().item(i), prefabManager);
            }
        }
        return prefabManager;
    }

    private PrefabManager parseMarkersExtraEntry(Node item, PrefabManager prefabManager) {
        String markerSTR = "";
        Marker marker = new Marker();


        Coordenates position = new Coordenates();
        Coordenates rotation = new Coordenates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            markerSTR = item.getChildNodes().item(i).getNodeName();
            switch (markerSTR) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    try {
                        marker = (Marker) prefabManager.findPrefabID(prefabID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            markerSTR = item.getChildNodes().item(i).getNodeName();
            switch (markerSTR) {
                case "mkCode":
                    marker.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "positionX":
                    position.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionY":
                    position.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionZ":
                    position.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationX":
                    rotation.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationY":
                    rotation.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationZ":
                    rotation.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        marker.setPosition(position);
        marker.setRotation(rotation);
        prefabManager.addMarker(marker);
        return prefabManager;
    }

    private PrefabManager parseDevicesExtraEntry(Node item, PrefabManager prefabManager) {
        String structureSTR = "";
        Device device = new Device();

        Coordenates position = new Coordenates();
        Coordenates rotation = new Coordenates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    try {
                        device = (Device) prefabManager.findPrefabID(prefabID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "stCode":
                    device.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "stCodeD":
                    device.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "positionX":
                    position.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionY":
                    position.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionZ":
                    position.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationX":
                    rotation.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationY":
                    rotation.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationZ":
                    rotation.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        device.setPosition(position);
        device.setRotation(rotation);
        prefabManager.addDevice(device);
        return prefabManager;
    }

    private PrefabManager parseStructuresExtraExtry(Node item, PrefabManager prefabManager) {
        String structureSTR = "";
        Structure structure = new Structure();

        Coordenates position = new Coordenates();
        Coordenates rotation = new Coordenates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    try {
                        structure = (Structure) prefabManager.findPrefabID(prefabID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "stCode":
                    structure.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "stCodeD":
                    structure.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "positionX":
                    position.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionY":
                    position.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionZ":
                    position.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationX":
                    rotation.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationY":
                    rotation.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationZ":
                    rotation.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        structure.setPosition(position);
        structure.setRotation(rotation);
        prefabManager.addStructure(structure);
        return prefabManager;
    }

    private PrefabManager parseRacks(Node item, PrefabManager prefabManager) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabManager = parseRackEntry(item.getChildNodes().item(i), prefabManager);
            }
        }
        return prefabManager;
    }

    private PrefabManager parseRackEntry(Node item, PrefabManager prefabManager) {
        int prefabIDX = -1;
        Rack rack = new Rack();
        Coordenates position = new Coordenates();
        Coordenates rotation = new Coordenates();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String rackEntry = item.getChildNodes().item(i).getNodeName();
            boolean break_flag = false;
            switch (rackEntry) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    try {
                        rack = (Rack) prefabManager.findPrefabID(prefabID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break_flag = true;
                    break;
            }
            if (break_flag == true) {
                break;
            }
        }

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String rackEntry = item.getChildNodes().item(i).getNodeName();
            switch (rackEntry) {
                case "rCode":
                    rack.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "rCodeD":
                    rack.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "positionX":
                    position.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionY":
                    position.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionZ":
                    position.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationX":
                    rotation.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationY":
                    rotation.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationZ":
                    rotation.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "row":
                    rack.setRow(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "shelves":
                    rack = parseRackShelvesExtras(item.getChildNodes().item(i), rack);
                    break;
            }
        }
        rack.setPosition(position);
        rack.setRotation(rotation);
        prefabManager.addRack(rack);
        return prefabManager;
    }

    private Rack parseRackShelvesExtras(Node item, Rack rack) {
        String prefabChild = "";
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                rack = parseRackShelvesExtrasEntry(item.getChildNodes().item(i), rack);
            }
        }
        return rack;
    }

    private Rack parseRackShelvesExtrasEntry(Node item, Rack rack) {
        String rackshelfEntry = "";
        Shelf shelf = new Shelf();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            rackshelfEntry = item.getChildNodes().item(i).getNodeName();
            switch (rackshelfEntry) {
                case "sPrefabID":
                    int id = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    for (int j = 0; j < rack.getShelves().size(); j++) {
                        if (rack.getShelves().get(j).getsID() == id) {
                            shelf = rack.getShelves().get(j);
                        }
                    }
                    break;
            }
        }
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            rackshelfEntry = item.getChildNodes().item(i).getNodeName();
            switch (rackshelfEntry) {
                case "sCode":
                    shelf.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "sCodeD":
                    shelf.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "areas":
                    shelf = parseRackShelfExtra(item.getChildNodes().item(i), shelf);
                    break;
            }
        }
        return rack;
    }

    private Shelf parseRackShelfExtra(Node item, Shelf shelf) {

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String shelfSTR = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(shelfSTR)) {
                shelf = parseShelfEntryExtra(item.getChildNodes().item(i), shelf);
            }
        }
        return shelf;
    }

    private Shelf parseShelfEntryExtra(Node item, Shelf shelf) {
        String shelfSTR = "";
        SmallArea area = new SmallArea();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            shelfSTR = item.getChildNodes().item(i).getNodeName();
            switch (shelfSTR) {
                case "aPrefabID":
                    int id = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    for (int j = 0; j < shelf.getArea().getAreas().size(); j++) {
                        if (shelf.getArea().getAreas().get(j).getaID() == id) {
                            area = shelf.getArea().getAreas().get(j);
                        }
                    }
                    break;
            }
        }
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            shelfSTR = item.getChildNodes().item(i).getNodeName();
            switch (shelfSTR) {
                case "aCode":
                    area.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "aCodeD":
                    area.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "aProduct":
                    area.setProduct(Boolean.parseBoolean(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        return shelf;
    }

    private LinkedList<Prefab> parsePrefabs(Node item) {
        LinkedList<Prefab> prefabs = new LinkedList<Prefab>();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                Prefab prefab = parsePrefabEntry(item.getChildNodes().item(i));
                prefabs.add(prefab);
            }
        }
        return prefabs;
    }

    private Prefab parsePrefabEntry(Node item) {
        Prefab prefab = new Prefab();
        Size size = new Size();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabEntry = item.getChildNodes().item(i).getNodeName();
            switch (prefabEntry) {
                case "ID":
                    prefab.setId(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "type":
                    prefab.setType(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "name":
                    prefab.setName(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "sizeX":
                    size.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeY":
                    size.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeZ":
                    size.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        prefab.setSize(size);
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            switch (prefab.getType()) {
                case 0: //RACK
                    if (item.getChildNodes().item(i).getNodeName().equals("shelves")) {
                        prefab = new Rack(prefab, parseRackShelves(item.getChildNodes().item(i)));
                    }
                    break;
                case 1: //Structure
                    if (item.getChildNodes().item(i).getNodeName().equals("sType")) {
                        prefab = new Structure(prefab, Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    }
                    break;
                case 2: //Device
                    if (item.getChildNodes().item(i).getNodeName().equals("devType")) {
                        prefab = new Device(prefab, Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    }
                    break;
                case 3: //Marker
                    if (item.getChildNodes().item(i).getNodeName().equals("mkType")) {
                        prefab = new Marker(prefab, Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    }
                    break;
            }
        }

        return prefab;
    }

    private LinkedList<Shelf> parseRackShelves(Node item) {
        LinkedList<Shelf> shelves = new LinkedList<>();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            if (item.getChildNodes().item(i).getNodeName().equals("entry")) { //shelves entry
                Shelf shelf = new Shelf();

                for (int j = 0; j < item.getChildNodes().item(i).getChildNodes().getLength(); j++) {
                    String entryChild = item.getChildNodes().item(i).getChildNodes().item(j).getNodeName();
                    switch (entryChild) {
                        case "sID":
                            shelf.setsID(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                            break;
                        case "sThick":
                            shelf.setsThick(Double.parseDouble(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                            break;
                        case "sHeight":
                            shelf.setsHeight(Double.parseDouble(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                            break;
                        case "areas":
                            shelf.setArea(parseAreaFromNode(item.getChildNodes().item(i).getChildNodes().item(j)));
                            break;
                    }
                }
                shelves.add(shelf);
            }
        }
        return shelves;
    }

    private Area parseAreaFromNode(Node item) {
        Area area = new Area();
        LinkedList<SmallArea> smallAreas = new LinkedList<>();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String areaChild = item.getChildNodes().item(i).getNodeName();
            switch (areaChild) {
                case "gridType":
                    area.setGridType(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "gridNumber":
                    area.setGridNumber(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "entry":
                    smallAreas.add(parseSmallAreaFromNode(item.getChildNodes().item(i)));
                    break;
            }
        }
        area.setAreas(smallAreas);
        return area;
    }

    private SmallArea parseSmallAreaFromNode(Node item) {
        SmallArea smallArea = new SmallArea();
        Size size = new Size();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String smallAreaChild = item.getChildNodes().item(i).getNodeName();
            switch (smallAreaChild) {
                case "aID":
                    smallArea.setaID(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeX":
                    size.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeY":
                    size.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeZ":
                    size.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        smallArea.setSize(size);
        return smallArea;
    }


    private Config parseConfigNode(Node item) {
        String width = "";
        String height = "";
        String depth = "";
        Coordenates coordenates = new Coordenates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            switch (item.getChildNodes().item(i).getNodeName()) {
                case "width":
                    width = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "height":
                    height = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "depth":
                    depth = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "startConfig":
                    for (int j = 0; j < item.getChildNodes().item(i).getChildNodes().getLength(); j++) {
                        String startConfigChildNode = item.getChildNodes().item(i).getChildNodes().item(j).getNodeName();
                        switch (startConfigChildNode) {
                            case "positionX":
                                coordenates.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                                break;
                            case "positionY":
                                coordenates.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                                break;
                            case "positionZ":
                                coordenates.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                                break;
                        }
                    }
                    break;
            }

        }
        return new Config(Float.parseFloat(width), Float.parseFloat(height), Float.parseFloat(depth), coordenates);
    }

    private XMLInfo parseXMLInfoNode(Node item) {
        String created = "";
        String modified = "";
        Byte updating = 0;
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            switch (item.getChildNodes().item(i).getNodeName()) {
                case "created":
                    created = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "modified":
                    modified = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "Updating":
                    updating = Byte.valueOf(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
            }
        }
        return new XMLInfo(created, modified, updating);
    }


    public void jButtonRunFromFile_actionPerformed(ActionEvent e) {
        GASingleton.getInstance().setNodeProblem(false);

        JFileChooser fc = new JFileChooser(new java.io.File("."));
        int returnVal = fc.showOpenDialog(this);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dataSet = fc.getSelectedFile();
                Scanner fileScanner = new Scanner(dataSet);
                int lines = fileScanner.nextInt();
                int column = fileScanner.nextInt();

                int[][] grid = new int[lines][column];
                int i = 0;
                int j = 0;


                String line = fileScanner.nextLine();
                StringBuilder str = new StringBuilder();
                while (fileScanner.hasNextLine()) {
                    line = fileScanner.nextLine();
                    Scanner lineScanner = new Scanner(line);
                    while (lineScanner.hasNext()) {
                        grid[i][j] = lineScanner.nextInt();
                        str.append(grid[i][j] + " ");
                        //System.out.print(grid[i][j]);
                        j++;
                    }
                    //System.out.println();
                    lineScanner.close();
                    str.append("\n");
                    j = 0;
                    i++;
                }
                fileScanner.close();
                System.out.println(str.toString());
                if (grid.length > grid[0].length) {
                    CELL_SIZE = FIXED_CELL_SIZE / (grid.length / 10);
                } else {
                    CELL_SIZE = FIXED_CELL_SIZE / (grid[0].length / 10);
                }
                environment = new Environment(grid, false, 0, 0, 0);
                //environment.addEnvironmentListener(this);

            }
        } catch (IOException e1) {
            e1.printStackTrace(System.err);
        } catch (java.util.NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }

        //environment = new Environment(grid);
        // environment.addEnvironmentListener(this);


        buildImage(environment);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                //environmentUpdated();
                //environment.run();
                return null;
            }
        };
        worker.execute();
    }

    public void buildImage(Environment environment) {
        image = new BufferedImage(environment.getNumColumns() * CELL_SIZE, environment.getNumLines() * CELL_SIZE, BufferedImage.TYPE_INT_RGB);
    }


}

//--------------------
class SimulationPanel_jButtonRun_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonRun_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRun_actionPerformed(e);
    }
}

class SimulationPanel_jButtonRunFromFile_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonRunFromFile_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRunFromFile_actionPerformed(e);
    }
}

class SimulationPanel_jButtonRunFromXML_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonRunFromXML_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRunFromXML_actionPerformed(e);
    }
}

class SimulationPanel_jButtonRunNodeGraph_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonRunNodeGraph_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRunNodeGraph_actionPerformed(e);
    }
}

class SimulationPanel_jButtonGenRandNodeGraph_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonGenRandNodeGraph_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRandNodeGraphProblem_actionPerformed(e);
    }
}

class SimulationPanel_jButtonGenRandNodeGraphSeed_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonGenRandNodeGraphSeed_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRandNodeGraphSeedProblem_actionPerformed(e);
    }
}

class SimulationPanel_jbuttonVersionTest_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jbuttonVersionTest_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jbuttonVersionTest_actionPerformed(e);
    }
}

class SimulationPanel_jButtonZoomIn_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonZoomIn_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonZoomIn_actionPerformed(e);
    }
}

class SimulationPanel_jButtonZoomOut_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonZoomOut_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonZoomOut_actionPerformed(e);
    }
}

class SimulationPanel_jButtonImportGraph_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonImportGraph_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonImportGraph_actionPerformed(e);
    }
}