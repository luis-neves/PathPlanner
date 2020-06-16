package gui;

import armazem.AStar;
import armazem.Environment;
import armazem.EnvironmentListener;
import classlib.Util;
import ga.GASingleton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import picking.Item;
import utils.Graphs.*;
import utils.warehouse.*;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.Location;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class SimulationPanel extends JPanel implements EnvironmentListener {

    private static final int MAX_WEIGHT = 500;
    private static int SLEEP_MILLIS = 10; // modify to speed up the simulation
    private static final int NUM_ITERATIONS = 2000; // modify to change the number of iterations

    private static final int FIXED_CELL_SIZE = 10;
    private static int CELL_SIZE = 10;
    private int NODE_SIZE = 5;
    private int MARGIN = 15;

    private static final int GRID_TO_PANEL_GAP = 20;
    private static final int N = 20;

    public static Environment environment;
    public static EnvironmentNodeGraph environmentNodeGraph;
    private Image image;
    private Graphics2D gfx;
    private JPanel environmentPanel = new JPanel();


    JButton jButtonRun = new JButton("Simulate Environment");
    JButton buttonRunFromFile = new JButton("Open Environment File");
    JButton buttonRunXML = new JButton("Test XML");
    JButton buttonRunNodeGraph = new JButton("Node Graph");
    JButton buttonRandomProblem = new JButton("Rand Node Graph");
    JButton buttonRandomProblemSeed = new JButton("Last Node Graph");
    JButton buttonTestColision = new JButton("X");
    private Graph graph;
    private Graph problemGraph;
    private int seed = 0;
    private int num_rows = 10;
    private int num_agents = 2;
    private int num_products = 5;
    private boolean stop = false;
    private int interruptionIndex = -1;
    private List<IterativeAgent> iterativeAgents = null;

    public SimulationPanel() {
        environmentPanel.setPreferredSize(new Dimension(N * CELL_SIZE + GRID_TO_PANEL_GAP * 2, N * CELL_SIZE + GRID_TO_PANEL_GAP * 2));

        setLayout(new BorderLayout());
        add(environmentPanel, BorderLayout.CENTER);
        JPanel panelButtons = new JPanel();
        //panelButtons.add(jButtonRun);
        //panelButtons.add(buttonRunFromFile);
        panelButtons.add(buttonRunXML);
        panelButtons.add(buttonRunNodeGraph);
        panelButtons.add(buttonRandomProblem);
        panelButtons.add(buttonRandomProblemSeed);
        panelButtons.add(buttonTestColision);
        add(panelButtons, BorderLayout.SOUTH);

        jButtonRun.addActionListener(new SimulationPanel_jButtonRun_actionAdapter(this));
        buttonRunFromFile.addActionListener(new SimulationPanel_jButtonRunFromFile_actionAdapter(this));
        buttonRunXML.addActionListener(new SimulationPanel_jButtonRunFromXML_actionAdapter(this));
        buttonRunNodeGraph.addActionListener(new SimulationPanel_jButtonRunNodeGraph_actionAdapter(this));
        buttonRandomProblem.addActionListener(new SimulationPanel_jButtonGenRandNodeGraph_actionAdapter(this));
        buttonRandomProblemSeed.addActionListener(new SimulationPanel_jButtonGenRandNodeGraphSeed_actionAdapter(this));
        buttonTestColision.addActionListener(new SimulationPanel_jButtonTestColision_actionAdapter(this));

        GASingleton.getInstance().setSimulationPanel(this);

    }


    public void jButtonRun_actionPerformed(ActionEvent e) {
        GASingleton.getInstance().setNodeProblem(false);

        environment = new Environment(N, N, 0, 0, 0);
        environment.addEnvironmentListener(this);

        buildImage(environment);

        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                environmentUpdated();
                //environment.run();
                return null;
            }
        };
        worker.execute();
    }

    public void jButtonTestColision_actionPerformed(ActionEvent e) {
        //test Colision
        FitnessResults results = GASingleton.getInstance().getBestInRun();
        int agent_id = 23;
        int agent_id_2 = 19;
        List<FitnessNode> nodes = results.getTaskedAgentsFullNodes().get(problemGraph.findNode(agent_id));
        nodes.remove(2);
        nodes.get(2).setCost(40f);
        List<FitnessNode> nodesClone2 = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getNode().getType() != GraphNodeType.PRODUCT) {
                nodesClone2.add(nodes.get(i));
            }
        }
        results.getTaskedAgentsFullNodes().replace(problemGraph.findNode(agent_id), nodes);
        results.getTaskedAgentsFullNodesNoPackages().replace(problemGraph.findNode(agent_id), nodesClone2);
        results.getTaskedAgentsFullNodes().get(problemGraph.findNode(agent_id_2)).clear();
        List<FitnessNode> nodes19 = results.getTaskedAgentsFullNodes().get(problemGraph.findNode(agent_id_2));

        nodes19 = insertFakeFitnessNode(nodes19, 0, 8, 60);
        nodes19 = insertFakeFitnessNode(nodes19, 1, 9, 30);
        nodes19 = insertFakeFitnessNode(nodes19, 2, 25, 40);
        nodes19 = insertFakeFitnessNode(nodes19, 3, 15, 40);
        nodes19 = insertFakeFitnessNode(nodes19, 4, 16, 30);
        nodes19 = insertFakeFitnessNode(nodes19, 5, 17, 30);
        nodes19 = insertFakeFitnessNode(nodes19, 6, 18, 30);

        results.getTaskedAgentsFullNodes().replace(problemGraph.findNode(agent_id_2), nodes19);
        List<FitnessNode> nodesClone = new ArrayList<>();

        for (int i = 0; i < nodes19.size(); i++) {
            if (nodes19.get(i).getNode().getType() != GraphNodeType.PRODUCT) {
                nodesClone.add(nodes19.get(i));
            }
        }
        results.getTaskedAgentsFullNodesNoPackages().replace(problemGraph.findNode(agent_id_2), nodesClone);

        results = GASingleton.getInstance().checkResultsForColision(results);
        GASingleton.getInstance().setBestInRun(results);
        GASingleton.getInstance().getBestIndividualPanel().textArea.setText(GASingleton.getInstance().getBestInRun().printTaskedAgents());
    }


    private List<FitnessNode> insertFakeFitnessNode(List<FitnessNode> nodes, int index, int nID, float cost) {
        Float time = 0f;
        if (index > 0) {
            time = nodes.get(index - 1).getTime();

        }
        nodes.add(index, new FitnessNode(nodes.size(), problemGraph.findNode(nID), cost, (time + cost)));
        for (int i = index + 1; i < nodes.size(); i++) {
            nodes.get(i).setTime(nodes.get(i).getTime() + cost);
        }
        return nodes;
    }

    public void jButtonRandNodeGraphProblem_actionPerformed(ActionEvent e) {
        //environmentPanel.updateUI();
        image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
        gfx = (Graphics2D) image.getGraphics();
        Graph graph2 = exampleGraph(num_rows);
        graph2 = randomProblem(graph2, num_agents, num_products, -1);
        graph2 = fixNeighboursFixed(graph2);
        problemGraph = graph2;
        draw(graph2, false, this.gfx, this.image);
    }

    public void generateExperimentGraph(int num_colums, int num_agents, int num_products, int seed) {
        Graph graph2 = exampleGraph(num_colums);
        graph2 = randomProblem(graph2, num_agents, num_products, seed);
        graph2 = fixNeighboursFixed(graph2);
        //TODO
        environmentNodeGraph = new EnvironmentNodeGraph(graph2);
        graph = graph2;
        List<Item> items = new ArrayList<>();
        List<GraphNode> agents = new ArrayList<>();
        for (int i = 0; i < graph.getGraphNodes().size(); i++) {
            if (graph.getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT || graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                if (graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                    agents.add(graph.getGraphNodes().get(i));
                }
                Item item = new Item((graph.getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT ? "P" : graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT ? "A" : "N") + graph.getGraphNodes().get(i).getGraphNodeId(), graph.getGraphNodes().get(i));
                items.add(item);
            }
        }
        GASingleton.getInstance().setItems(items);
        GASingleton.getInstance().setLastAgent(agents.get(agents.size() - 1));
        GASingleton.getInstance().setNodeProblem(true);

    }

    private Graph randomProblem(Graph graph, int num_agents, int num_products, int seed) {
        int agentCount = 0;
        if (seed == -1) {
            Random rand = new Random();
            this.seed = rand.nextInt(5000);
        }

        for (int i = 0; i < num_products + num_agents; i++) {
            Random r = new Random(i + this.seed);
            Edge edge;
            do {
                edge = graph.getEdges().get(r.nextInt(graph.getEdges().size()));
            } while (edge.getLocation().getX() == 0);
            GraphNode start = edge.getStart();
            GraphNode end = edge.getEnd();
            GraphNode product = new GraphNode(graph.getGraphNodes().size() + 1);
            product.setType(GraphNodeType.PRODUCT);
            if (agentCount != num_agents) {
                product.setType(GraphNodeType.AGENT);
                agentCount++;
            }
            if (edge.getLocation().getX() == 0) {
                float xStart = start.getLocation().getX();
                float xEnd = end.getLocation().getX();
                float result;
                do {
                    result = r.nextInt((int) Math.abs(xStart - xEnd)) + (Math.min(xStart, xEnd));
                } while (result == xStart || result == xEnd);
                product.setLocation(new Coordenates(result, edge.getLocation().getY(), 0));
            } else {
                float yStart = start.getLocation().getY();
                float yEnd = end.getLocation().getY();
                float result;
                do {
                    result = r.nextInt((int) Math.abs(yStart - yEnd)) + (Math.min(yStart, yEnd));
                } while (result == yStart || result == yEnd);
                product.setLocation(new Coordenates(edge.getLocation().getX(), result, 0));
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
            Graph graph2 = exampleGraph(num_rows);
            graph2 = randomProblem(graph2, num_agents, num_products, seed);
            graph2 = fixNeighboursFixed(graph2);
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

            graph2 = fixNeighboursFixed(graph2);
            image = environmentPanel.createImage(environmentPanel.getWidth(), environmentPanel.getHeight());
            gfx = (Graphics2D) image.getGraphics();
            this.problemGraph = graph2;
            draw(graph2, false, this.gfx, this.image);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private Graph fixNeighboursFixed(Graph graph) {
        for (int i = 0; i < graph.getGraphNodes().size(); i++) {
            GraphNode selectedNode = graph.getGraphNodes().get(i);
            if (selectedNode.getType() == GraphNodeType.PRODUCT || selectedNode.getType() == GraphNodeType.AGENT) {
                Float selected_y = selectedNode.getLocation().getY();
                Float lowest = Float.MAX_VALUE;
                Float highest = -1f;
                Float lowestS = Float.MAX_VALUE;
                Float highestS = -1f;
                GraphNode neighborN = null;
                GraphNode neighborS = null;
                GraphNode neighborNS = null;
                GraphNode neighborSS = null;

                GraphNode sameSpot = null;
                for (int j = 0; j < graph.getGraphNodes().size(); j++) {
                    GraphNode neighbour = graph.getGraphNodes().get(j);
                    if (!selectedNode.equals(neighbour) && neighbour.getLocation().getX() == selectedNode.getLocation().getX()) {
                        //NOT SIMPLE
                        if (neighbour.getLocation().getY() > selected_y && neighbour.getLocation().getY() < lowest) {
                            neighborS = neighbour;
                            lowest = neighbour.getLocation().getY();
                        }
                        if (neighbour.getLocation().getY() < selected_y && neighbour.getLocation().getY() >= highest) {
                            neighborN = neighbour;
                            highest = neighbour.getLocation().getY();
                        }
                        //SIMPLE
                        if (neighbour.getLocation().getY() > selected_y && neighbour.getLocation().getY() < lowestS && (neighbour.getType() == GraphNodeType.SIMPLE || neighbour.getType() == GraphNodeType.EXIT)) {
                            neighborSS = neighbour;
                            lowestS = neighbour.getLocation().getY();
                        }
                        if (neighbour.getLocation().getY() < selected_y && neighbour.getLocation().getY() >= highestS && (neighbour.getType() == GraphNodeType.SIMPLE || neighbour.getType() == GraphNodeType.EXIT)) {
                            neighborNS = neighbour;
                            highestS = neighbour.getLocation().getY();
                        }
                        if (neighbour.getLocation().getY() == selected_y) {
                            Edge edge = new Edge(selectedNode, neighbour, 0, graph.getEdges().size());
                            selectedNode.addNeighbour(edge);
                            neighbour.addNeighbour(edge);
                            graph.getEdges().add(edge);
                        }
                    }
                }
                if (neighborN == null || neighborS == null) {
                    //System.out.println();
                }

                if (neighborS != null) {
                    Edge edgeS = new Edge(selectedNode, neighborS, selectedNode.getDistance(neighborS), graph.getEdges().size());
                    graph.getEdges().add(edgeS);
                    selectedNode.addNeighbour(edgeS);
                    neighborS.addNeighbour(edgeS);
                }
                if (neighborN != null) {
                    Edge edgeN = new Edge(selectedNode, neighborN, selectedNode.getDistance(neighborN), graph.getEdges().size());
                    graph.getEdges().add(edgeN);
                    selectedNode.addNeighbour(edgeN);
                    neighborN.addNeighbour(edgeN);
                }
                //SIMPLE
                Edge edgeS = new Edge(selectedNode, neighborSS, selectedNode.getDistance(neighborSS), graph.getEdges().size());
                graph.getEdges().add(edgeS);
                selectedNode.addNeighbour(edgeS);
                neighborSS.addNeighbour(edgeS);
                Edge edgeN = new Edge(selectedNode, neighborNS, selectedNode.getDistance(neighborNS), graph.getEdges().size());
                graph.getEdges().add(edgeN);
                selectedNode.addNeighbour(edgeN);
                neighborNS.addNeighbour(edgeN);

                //neighborN.removeNeighbour(neighborS);
                //neighborS.removeNeighbour(neighborN);
                /*if (neighborN.getType() == GraphNodeType.SIMPLE) {
                    neighborN.removeSouthSimple();
                }*/
            }
        }
        /*
        for (int i = 0; i < graph.getGraphNodes().size(); i++) {
            System.out.println(graph.getGraphNodes().get(i).toString());
        }*/
        return graph;
    }

    private Graph fixNeighbours(Graph graph) {
        List<Edge> removedEdges = new ArrayList<>();
        List<Edge> addedEdges = new ArrayList<>();
        try {
            for (int i = 0; i < graph.getGraphNodes().size(); i++) {
                if (graph.getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT || graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                    GraphNode selectedNode = graph.getGraphNodes().get(i);
                    for (int j = 0; j < graph.getEdges().size(); j++) {
                        Edge selectedEdge = graph.getEdges().get(j);
                        if (selectedNode.getLocation().getX() == selectedEdge.getLocation().getX()) {
                            if (selectedEdge.getEnd().getLocation().getY() < selectedNode.getLocation().getY() && selectedEdge.getStart().getLocation().getY() > selectedNode.getLocation().getY() ||
                                    selectedEdge.getEnd().getLocation().getY() > selectedNode.getLocation().getY() && selectedEdge.getStart().getLocation().getY() < selectedNode.getLocation().getY()) {
                                removedEdges.add(selectedEdge);
                                Edge newEdge = new Edge(selectedNode, selectedEdge.getEnd(), Math.abs(selectedNode.getLocation().getY() - selectedEdge.getEnd().getLocation().getY()), graph.getEdges().size());
                                Edge newEdge2 = new Edge(selectedNode, selectedEdge.getStart(), Math.abs(selectedNode.getLocation().getY() - selectedEdge.getStart().getLocation().getY()), graph.getEdges().size());
                                addedEdges.add(newEdge);
                                addedEdges.add(newEdge2);
                                graph.getEdges().remove(selectedEdge);
                            }
                        }
                        if (selectedNode.getLocation().getY() == selectedEdge.getLocation().getY()) {
                            if (selectedEdge.getEnd().getLocation().getX() < selectedNode.getLocation().getX() && selectedEdge.getStart().getLocation().getX() > selectedNode.getLocation().getX() ||
                                    selectedEdge.getEnd().getLocation().getX() > selectedNode.getLocation().getX() && selectedEdge.getStart().getLocation().getX() < selectedNode.getLocation().getX()) {
                                removedEdges.add(selectedEdge);
                                Edge newEdge = new Edge(selectedNode, selectedEdge.getEnd(), Math.abs(selectedNode.getLocation().getX() - selectedEdge.getEnd().getLocation().getX()), graph.getEdges().size());
                                Edge newEdge2 = new Edge(selectedNode, selectedEdge.getStart(), Math.abs(selectedNode.getLocation().getX() - selectedEdge.getStart().getLocation().getX()), graph.getEdges().size());
                                addedEdges.add(newEdge);
                                addedEdges.add(newEdge2);
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < addedEdges.size(); i++) {
                addedEdges.get(i).setId(graph.getEdges().size());
                addedEdges.get(i).getStart().addNeighbour(addedEdges.get(i));
                addedEdges.get(i).getEnd().addNeighbour(addedEdges.get(i));
                graph.getEdges().add(addedEdges.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        graph.getEdges().removeAll(removedEdges);
        return graph;
    }

    private List<GraphNode> exampleGraphProblem() {
        List<GraphNode> list = new ArrayList<>();
        GraphNode agent = new GraphNode(19);
        agent.setType(GraphNodeType.AGENT);
        agent.setLocation(new Coordenates(80, 100, 0));

        GraphNode product = new GraphNode(20);
        product.setType(GraphNodeType.PRODUCT);
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
        product4.setType(GraphNodeType.AGENT);
        product4.setLocation(new Coordenates(80, 40, 0));

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
            Edge aibi = new Edge(ai, bi, ai.getLocation().getY() - bi.getLocation().getY(), i);
            Edge bici = new Edge(bi, ci, bi.getLocation().getY() - ci.getLocation().getY(), i + num_rows * 4);


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
            Point p = new Point(Math.round(node.getLocation().getX()), Math.round(node.getLocation().getY()));
            if (node.getType() == GraphNodeType.AGENT) {
                gfx.setColor(Color.RED);
                gfx.fillOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);

            } else if (node.getType() == GraphNodeType.PRODUCT) {
                gfx.setColor(Color.blue);
                gfx.fillOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);
            } else if (node.getType() == GraphNodeType.EXIT) {
                gfx.setColor(Color.green);
                gfx.drawOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);
            } else if (node.getType() == GraphNodeType.DELIVERING) {
                gfx.setColor(Color.GRAY);
                gfx.drawOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);
            } else {
                gfx.drawOval(p.x - (NODE_SIZE / 2), p.y, NODE_SIZE, NODE_SIZE);
            }
            if (node.getType() != GraphNodeType.DELIVERING) {
                gfx.drawString("" + node.getType().toLetter() + node.getGraphNodeId(), p.x + (NODE_SIZE / 2), p.y + (NODE_SIZE / 2) + MARGIN);
            }
            /*for(int j = 0; j < node.getNeighbours().size(); j++){
                GraphNode startNode = nodes.get(node.getNeighbours().get(j).getIdOfStartGraphNode() - 1);
                int id = node.getNeighbours().get(j).getIdOfEndGraphNode();
                GraphNode endNode = nodes.get(id-1);
                gfx.drawLine(Math.round(startNode.getLocation().getX()),Math.round(startNode.getLocation().getY()),Math.round(endNode.getLocation().getX()),Math.round(endNode.getLocation().getY()));
                gfx.drawString("" +node.getNeighbours().get(j).getWeight(),  (Math.round(endNode.getLocation().getX()) + Math.round(startNode.getLocation().getX())) / 2, (Math.round(endNode.getLocation().getY()) + Math.round(startNode.getLocation().getY()) / 2));
                System.out.println(node.getNeighbours().get(j).getWeight());
            }*/
            // gfx.drawLine(p.x,p.y,p2.x,p2.y);
            gfx.setColor(Color.BLACK);

        }
        for (
                int i = 0; i < graph.getEdges().

                size();

                i++) {
            GraphNode start = graph.getEdges().get(i).getStart();
            GraphNode end = graph.getEdges().get(i).getEnd();

            gfx.drawLine(Math.round(start.getLocation().getX()), Math.round(start.getLocation().getY()) + (NODE_SIZE / 2), Math.round(end.getLocation().getX()), Math.round(end.getLocation().getY()) + (NODE_SIZE / 2));
            int x = ((Math.round(end.getLocation().getX()) + Math.round(start.getLocation().getX())) / 2) + (NODE_SIZE / 2);
            int y = ((Math.round(end.getLocation().getY()) + Math.round(start.getLocation().getY())) / 2) + (NODE_SIZE / 2);
            //gfx.drawString("" + (int) graph.getEdges().get(i).getWeight(), x, y);

        }
        //environmentPanel.getGraphics().drawImage(image, 0, 0, this);
        environmentPanel.getGraphics().

                drawImage(image, GRID_TO_PANEL_GAP, GRID_TO_PANEL_GAP, null);

        if (graph.containsProblem() && !justDraw) {
            environmentNodeGraph = new EnvironmentNodeGraph(graph);
            List<Item> items = new ArrayList<>();
            List<GraphNode> agents = new ArrayList<>();
            for (int i = 0; i < graph.getGraphNodes().size(); i++) {
                if (graph.getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT || graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                    if (graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                        agents.add(graph.getGraphNodes().get(i));
                    }
                    Item item = new Item((graph.getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT ? "P" : graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT ? "A" : "N") + graph.getGraphNodes().get(i).getGraphNodeId(), graph.getGraphNodes().get(i));
                    items.add(item);
                }
            }
            GASingleton.getInstance().setItems(items);
            GASingleton.getInstance().setLastAgent(agents.get(agents.size() - 1));
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
                        Point p = new Point((int) location.getX(), (int) location.getY());
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

    public void incrementTime(int i) {
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
        try {
            File inputFile = new File("input.xml");
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
            for (int i = 0; i < warehouse.getChildNodes().getLength(); i++) {
                if (warehouse.getChildNodes().item(i).getNodeName().equals("racks")) {
                    prefabList = parseRacks(warehouse.getChildNodes().item(i), prefabList);
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("structures")) {
                    prefabList = parseStructuresExtra(warehouse.getChildNodes().item(i), prefabList);
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("devices")) {
                    prefabList = parseDevicesExtra(warehouse.getChildNodes().item(i), prefabList);
                }
            }
            Collections.sort(prefabList, new CustomComparator());

            for (int i = 0; i < prefabList.size(); i++) {
                System.out.println(prefabList.get(i));
            }


            Rack rack = findFirst(prefabList, Rack.class);
            Device device = findFirst(prefabList, Device.class);
            Structure structure = findFirst(prefabList, Structure.class);


            int[][] grid = new int[Math.round(config.getDepth()) / Math.round(device.getSize().getZ())][Math.round(config.getWidth()) / Math.round(device.getSize().getX())];
            System.out.println(Math.round(device.getSize().getX()));
            grid[Math.round((rack.getPosition().getZ()))][Math.round(rack.getPosition().getX())] = 1;
            grid[grid.length + 1 - Math.round(device.getPosition().getZ())][grid[0].length + 1 - Math.round(device.getPosition().getX())] = 3;
            grid[Math.round(structure.getPosition().getZ())][Math.round(structure.getPosition().getX())] = 1;

            int rackPositionX = Math.round(rack.getPosition().getX());
            int rackPositionY = Math.round(rack.getPosition().getZ());
            int rackWidthX = Math.round(rack.getSize().getX()) / Math.round(device.getSize().getX());
            int rackWidthY = Math.round(rack.getSize().getZ()) / Math.round(device.getSize().getZ());

            System.out.println(Math.round(device.getPosition().getZ()) + " " + Math.round(device.getPosition().getX()));
            System.out.println(rackPositionX + "," + rackPositionY + "," + rackWidthX + "," + rackWidthY);

            for (int i = rackPositionY; i < (rackPositionY + rackWidthY); i++) {
                for (int j = rackPositionX; j < (rackPositionX + rackWidthX); j++) {
                    System.out.println("[" + i + "][" + j + "]");
                    grid[i][j] = 1;
                }
            }

            System.out.println("[" + grid.length + "][" + grid[0].length + "]");

            if (grid.length > grid[0].length || grid[0].length > grid.length && grid.length > 30) {
                CELL_SIZE = FIXED_CELL_SIZE / (grid.length / 12);
            } else {
                CELL_SIZE = FIXED_CELL_SIZE / (grid[0].length / 12);
            }

            environment = new Environment(grid, false, 0, 0, 0);
            environment.addEnvironmentListener(this);

            buildImage(environment);

            SwingWorker worker = new SwingWorker<Void, Void>() {
                public Void doInBackground() {
                    environmentUpdated();
                    //environment.run();
                    return null;
                }
            };
            worker.execute();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class CustomComparator implements Comparator<Prefab> {
        @Override
        public int compare(Prefab o1, Prefab o2) {
            try {
                return Float.compare(o1.getPosition().getX(), o2.getPosition().getX());
            } catch (NullPointerException e) {
                if (o1.getPosition() == null) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }

    }

    public static <E> E findFirst(LinkedList<E> list, Class<E> itemType) {
        for (E element : list) {
            if (itemType.isInstance(element)) {
                return element;
            }
        }
        return null;
    }

    private LinkedList parseStructuresExtra(Node item, LinkedList prefabList) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabList = parseStructuresExtraExtry(item.getChildNodes().item(i), prefabList);
            }
        }
        return prefabList;
    }

    private LinkedList parseDevicesExtra(Node item, LinkedList prefabList) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabList = parseDevicesExtraEntry(item.getChildNodes().item(i), prefabList);
            }
        }
        return prefabList;
    }

    private LinkedList<Prefab> parseDevicesExtraEntry(Node item, LinkedList<Prefab> prefabList) {
        String structureSTR = "";
        Device device = new Device();

        Coordenates position = new Coordenates();
        Coordenates rotation = new Coordenates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    for (int j = 0; j < prefabList.size(); j++) {
                        if (prefabList.get(j).getId() == prefabID) {
                            device = (Device) prefabList.get(prefabID);
                        }
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
        return prefabList;
    }

    private LinkedList<Prefab> parseStructuresExtraExtry(Node item, LinkedList<Prefab> prefabList) {
        String structureSTR = "";
        Structure structure = new Structure();

        Coordenates position = new Coordenates();
        Coordenates rotation = new Coordenates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    for (int j = 0; j < prefabList.size(); j++) {
                        if (prefabList.get(j).getId() == prefabID) {
                            structure = (Structure) prefabList.get(prefabID);
                        }
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
        return prefabList;
    }

    private LinkedList<Prefab> parseRacks(Node item, LinkedList<Prefab> prefabs) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabs = parseRackEntry(item.getChildNodes().item(i), prefabs);
            }
        }
        return prefabs;
    }

    private LinkedList<Prefab> parseRackEntry(Node item, LinkedList<Prefab> prefabs) {
        int prefabIDX = -1;
        Rack rack = new Rack();
        Coordenates position = new Coordenates();
        Coordenates rotation = new Coordenates();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String rackEntry = item.getChildNodes().item(i).getNodeName();
            switch (rackEntry) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    for (int j = 0; j < prefabs.size(); j++) {
                        if (prefabs.get(j).getId() == prefabID) {
                            rack = (Rack) prefabs.get(prefabID);
                        }
                    }
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
        return prefabs;
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
                environment.addEnvironmentListener(this);

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
                environmentUpdated();
                //environment.run();
                return null;
            }
        };
        worker.execute();
    }

    public void buildImage(Environment environment) {
        image = new BufferedImage(environment.getNumColumns() * CELL_SIZE, environment.getNumLines() * CELL_SIZE, BufferedImage.TYPE_INT_RGB);
    }


    public void environmentUpdated() {
        int n = environment.getNumLines();
        int c = environment.getNumColumns();
        Graphics g = image.getGraphics();
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < c; x++) {
                g.setColor(environment.getCellColor(y, x));
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

            }
        }
        environmentPanel.getGraphics().drawImage(image, GRID_TO_PANEL_GAP, GRID_TO_PANEL_GAP, null);
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException ignore) {
        }
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

class SimulationPanel_jButtonTestColision_actionAdapter implements ActionListener {

    private SimulationPanel adaptee;

    SimulationPanel_jButtonTestColision_actionAdapter(SimulationPanel adaptee) {
        this.adaptee = adaptee;
    }


    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonTestColision_actionPerformed(e);
    }
}