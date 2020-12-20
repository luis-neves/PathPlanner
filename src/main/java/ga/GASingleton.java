package ga;

import armazem.Cell;
import classlib.CommunicationManager;
import classlib.Util;
import communication.CommunicationVariables;
import communication.Operator;
import communication.Tarefa;
import ga.multiple.GAwithEnvironment;
import gui.Main;
import gui.MainFrame;
import gui.PanelTextArea;
import gui.SimulationPanel;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import picking.Item;
import picking.Picking;
import picking.PickingIndividual;
import utils.Graphs.*;
import utils.warehouse.Prefab;
import utils.warehouse.PrefabManager;
import utils.warehouse.Rack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class GASingleton implements Observer {
    public static final String WAREHOUSE_FILE = "warehouse_model.xml";
    public static final String GRAPH_FILE = "graph.xml";
    private static GASingleton instance;

    PrefabManager prefabManager;
    private List<Item> items;
    private int distanceMatrix[][];
    private int fitnessType;
    private int[][] grid;
    private boolean nodeProblem;
    private GraphNode lastAgent;
    private SimulationPanel simulationPanel;
    private FitnessResults bestInRun;
    private PanelTextArea bestIndividualPanel;
    private boolean simulatingWeights;
    private MainFrame mainFrame;
    private float colisionWeight;
    private float weightsPenaltyWeight;
    private CommunicationManager cm;
    private float timeWeight;
    private int numExperiments;
    private CommunicationVariables communication_variables;
    private int seed;
    private HashMap<GraphNode, List<GraphNode>> itemMap;
    private Graph problemGraph;
    private HashMap<GraphNode, List<GraphNode>> taskMap;
    private GAwithEnvironment[] lastGenGAs;
    private boolean multipleGA;
    private Individual defaultBestInRun;
    private GeneticAlgorithm defaultGA;
    private Graph graph;
    public static final String CLIENT_ID = "planeador";
    public static final String erpID = "erp";
    public static final String RA_ID = "ra";
    public static final String LOC_APROX_ID = "locaproximada";
    public static final String MODELADOR_ID = "modelador";
    public static final int NUM_OPERATORS = 1;

    public PrefabManager getPrefabManager() {
        return prefabManager;
    }

    public void setPrefabManager(PrefabManager prefabManager) {
        this.prefabManager = prefabManager;
    }

    public GeneticAlgorithm getDefaultGA() {
        return defaultGA;
    }

    public void setDefaultGA(GeneticAlgorithm defaultGA) {
        this.defaultGA = defaultGA;
    }

    public boolean isMultipleGA() {
        return multipleGA;
    }

    public GAwithEnvironment[] getLastGenGAs() {
        return lastGenGAs;
    }

    public void setLastGenGAs(GAwithEnvironment[] lastGenGAs) {
        this.lastGenGAs = lastGenGAs;
    }


    @Override
    public void update(Observable o, Object arg) {
        CommunicationVariables comms = (CommunicationVariables) o;
        if (comms.isTasks_ready()) {
            prepareTasks();
            comms.setTasks_ready(false);
            comms.getGa_ready()[0] = true;
        }
        if (comms.isOperators_ready()) {
            prepareOperators();
            comms.setOperators_ready(false);
            comms.getGa_ready()[1] = true;
        }
        if (comms.ga_fully_ready()) {
            //GA
            System.out.println("Ready for GA");
            simulationPanel.draw_problem_graph();
            mainFrame.runMultipleGA();
        }
    }

    private void simulateOperator() {
        Operator operator = new Operator(true);
        operator.setId("RA1");
        operator.setX(problemGraph.findNode(15).getLocation().getX());
        operator.setY(problemGraph.findNode(15).getLocation().getY() - 5);

        communication_variables.getOperators().add(operator);
        communication_variables.setOperators_ready(true);
    }

    private void prepareOperators() {
        if (communication_variables.getOperators().size() > 0) {
            for (Operator operator : communication_variables.getOperators()) {
                if (operator.isAvailable()) {
                    GraphNode agent = new GraphNode(problemGraph.getNumNodes(), operator.getX(), operator.getY(), GraphNodeType.AGENT);
                    operator.setAgent(agent);
                    problemGraph.createGraphNodeOnClosestEdge(agent);
                }
            }
        } else {
            System.out.println("No operators to prepare");
        }
    }

    private void prepareTasks() {
        if (prefabManager != null) {
            prefabManager.fixSizesToInteger();
            prefabManager.generateShapes();
            problemGraph = graph.clone();
            for (Tarefa tarefa : communication_variables.getTarefas()) {
                Rack rack = prefabManager.findRackByID(Integer.parseInt(tarefa.getOrigem()));
                if (rack != null) {
                    int new_x = Math.round((int) rack.getShape().getBounds().getCenterX());
                    int new_y = Math.round((int) rack.getShape().getBounds().getCenterY());
                    problemGraph.createGraphNodeOnClosestEdge(new GraphNode(problemGraph.getNumNodes(), new_x, new_y, GraphNodeType.PRODUCT));
                }
            }
        } else {
            mainFrame.logMessage("prefabManager is NULL, can't generate tasks", 0);
        }
    }

    public CommunicationManager getCm() {
        return cm;
    }

    public void setCm(CommunicationManager cm) {

        this.cm = cm;
    }

    public boolean isSimulatingWeights() {
        return simulatingWeights;
    }

    public void setBestInRun(FitnessResults bestInRun) {
        this.bestInRun = bestInRun;
    }

    public SimulationPanel getSimulationPanel() {
        return simulationPanel;
    }

    public void setSimulationPanel(SimulationPanel simulationPanel) {
        this.simulationPanel = simulationPanel;
    }

    private String str;

    public GraphNode getLastAgent() {
        return lastAgent;
    }

    public boolean isNodeProblem() {
        return nodeProblem;
    }

    public int getFitnessType() {
        return fitnessType;
    }

    public int getMatrixCounter() {
        return matrixCounter;
    }

    private int matrixCounter = 0;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void generateDistanceMatrix(int lines, int columns) {
        this.distanceMatrix = new int[lines][columns];
    }

    public boolean increaseMatrixCounter() {
        if (matrixCounter == distanceMatrix.length) {
            return false;
        } else {
            matrixCounter++;
            return true;
        }
    }

    public void addDistanceToMatrix(int line, int column, int distance) {
        distanceMatrix[line][column] = distance;
        distanceMatrix[column][line] = distance;
    }

    public void addToItems(Item item) {
        items.add(item);
    }

    private GASingleton() {
        this.items = new ArrayList<>();
        this.weightsPenaltyWeight = 0.0f;
        this.timeWeight = 0.5f;
        this.colisionWeight = 0.5f;
        this.graph = new Graph();
        this.communication_variables = new CommunicationVariables(this);
    }

    public static GASingleton getInstance() {
        if (instance == null) {
            instance = new GASingleton();
        }
        return instance;
    }

    public void printMatrix(String matrix[][]) {
        if (matrix == null) {
            for (int i = 0; i < distanceMatrix.length; i++) {
                for (int j = 0; j < distanceMatrix[i].length; j++) {
                    System.out.print(distanceMatrix[i][j] + " \t");
                }
            }
        } else {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    System.out.print(matrix[i][j] == null ? "------- \t" : matrix[i][j] + "\t");
                }
            }
        }

    }

    public void printItems() {
        for (Item i : items) {
            System.out.println(i.name + " Position " + i.positionINMATRIX);
        }
    }

    public int getDistanceFromMatrix(int line, int column) {
        return distanceMatrix[line][column];
    }

    public List<Double> setFinalItemSet(List<Item> items, boolean show) {
        if (show) {
        }
        //String missing = getMissingAgent(finalSet);
        if (SimulationPanel.environment != null) {
            return SimulationPanel.environment.showPath(items, show);
        }
        return null;
    }


    public String getMissingAgent(List<Item> items) {
        List<Integer> agentsUsed = new ArrayList<>();
        List<Integer> allAgents = new ArrayList<>();

        for (Item item : items) {
            try {
                agentsUsed.add(Integer.parseInt(item.name));
            } catch (NumberFormatException e) {
                if (allAgents.isEmpty()) {
                    for (Cell c : item.agents) {
                        allAgents.add(item.agents.indexOf(c));
                    }
                }
            }
        }
        Integer lastAgent = -1;
        for (Integer a : allAgents) {
            if (agentsUsed.contains(a)) {

            } else {
                lastAgent = a;
            }
        }
        return lastAgent.toString();
    }

    public void clearData() {
        this.items = new ArrayList<>();
    }

    public void setFitnessType(int selectedIndex) {
        this.fitnessType = selectedIndex;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void generateGrid(int[][] grid) {
        this.grid = grid;
    }

    public String getMissingAgentString() {
        return this.getMissingAgent(items);
    }

    public void setNodeProblem(boolean b) {
        this.nodeProblem = b;
    }

    public void setLastAgent(GraphNode graphNode) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).node.getGraphNodeId() == graphNode.getGraphNodeId()) {
                this.lastAgent = graphNode;
                items.remove(i);
                return;
            }
        }
    }

    public void generateXMLPath(HashMap<GraphNode, List<FitnessNode>> taskedAgents) {
        try {
            for (Map.Entry<GraphNode, List<FitnessNode>> entry : taskedAgents.entrySet()) {
                String xmlFilePath = "..\\" + entry.getKey().getGraphNodeId() + ".xml";

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

                Document document = documentBuilder.newDocument();

                // root element
                Element root = document.createElement("Task");

                root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
                root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                Attr attrAgent = document.createAttribute("agent-id");
                GraphNode agent = entry.getKey();
                attrAgent.setValue(communication_variables.getOperatorByGraphNode(agent).getId());
                root.setAttributeNode(attrAgent);
                Attr attRoot = document.createAttribute("id");
                attRoot.setValue(entry.getKey().getGraphNodeId() + "");
                root.setAttributeNode(attRoot);
                document.appendChild(root);

                List<FitnessNode> agentPath = entry.getValue();
                //

                //root.appendChild(agent.generateXMLelement(document));
                Element pathElement = document.createElement("Path");
                //
                //str += "\nAgent " + agent.getType().toLetter() + agent.getGraphNodeId() + "\n\t";
                if (agentPath.isEmpty()) {

                    //str += "Empty Path";
                } else {
                    for (int i = 0; i < agentPath.size(); i++) {
                        Attr nattr = document.createAttribute("id");
                        nattr.setValue(agentPath.get(i).getNode().getGraphNodeId() + "");
                        Element node = document.createElement("Node");
                        node.setAttributeNode(nattr);
                        if (agentPath.get(i).getNode().getType() == GraphNodeType.PRODUCT) {
                            Attr productiD = document.createAttribute("product-id");
                            productiD.setValue(agentPath.get(i).getNode().getGraphNodeId() + "");
                            node.setAttributeNode(productiD);
                        }
                        node.appendChild(agentPath.get(i).getNode().generateXMLelement(document));
                        pathElement.appendChild(node);
                        //str += "[" + agentPath.get(i).getType().toLetter() + agentPath.get(i).getGraphNodeId() + "]";
                    }
                }
                root.appendChild(pathElement);


                // create the xml file
                //transform the DOM Object to an XML File
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(new File(xmlFilePath));

                // If you use
                // StreamResult result = new StreamResult(System.out);
                // the output will be pushed to the standard output ...
                // You can use that for debugging

                transformer.transform(domSource, streamResult);
                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(document), new StreamResult(writer));

                String xmlString = writer.getBuffer().toString();
                System.out.println(xmlString);
                if (communication_variables.getOperatorByGraphNode(agent).isAvailable()) {
                    cm.SendMessageAsync(Util.GenerateId(), "request", "setRoute", communication_variables.getOperatorByGraphNode(agent).getId(), "application/xml", xmlString, "1");
                }//cm.SendMessageAsync(Util.GenerateId(), "request", "setRoute", "ra1", "application/xml", xmlString, "1");
            }
            System.out.println("\nDone creating/sending XML File");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public void write_modelador_xml_to_file(String fileName, String str)
            throws IOException {
        FileOutputStream outputStream = new FileOutputStream(fileName);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
    }

    public FitnessResults getBestInRun() {
        return this.bestInRun;
    }


    public PanelTextArea getBestIndividualPanel() {
        return this.bestIndividualPanel;
    }

    public void setBestIndividualPanel(PanelTextArea bestIndividualPanel) {
        this.bestIndividualPanel = bestIndividualPanel;
    }

    public void setSimulatingWeights(boolean weight) {
        this.simulatingWeights = weight;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public float getColisionWeight() {
        return colisionWeight;
    }

    public void setColisionWeight(float colisionWeight) {
        this.colisionWeight = colisionWeight;
    }

    public float getWeightsPenaltyWeight() {
        return this.weightsPenaltyWeight;
    }

    public void setWeightsPenaltyWeight(float weightsPenaltyWeight) {
        this.weightsPenaltyWeight = weightsPenaltyWeight;
    }

    public void setTimeWeight(float timeWeight) {
        this.timeWeight = timeWeight;
    }

    public float getTimeWeight() {
        return this.timeWeight;
    }

    public void setNumExperiments(int numExperiments) {
        this.numExperiments = numExperiments;
    }

    public int getNumExperiments() {
        return numExperiments;
    }


    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getSeed() {
        return seed;
    }

    public HashMap<GraphNode, List<GraphNode>> getItemMap() {
        return itemMap;
    }

    public void setItemMap(HashMap<GraphNode, List<GraphNode>> itemMap) {
        this.itemMap = itemMap;
    }

    public Graph getProblemGraph() {
        return problemGraph;
    }

    public void setProblemGraph(Graph problemGraph) {
        this.problemGraph = problemGraph;
    }

    public void setTaskMap(HashMap<GraphNode, List<GraphNode>> taskMap) {
        this.taskMap = taskMap;
        if (taskMap != null) {
            this.lastGenGAs = new GAwithEnvironment[taskMap.entrySet().size()];
            for (int i = 0; i < lastGenGAs.length; i++) {
                lastGenGAs[i] = new GAwithEnvironment();
                lastGenGAs[i].setEnvironment(new EnvironmentNodeGraph(SimulationPanel.environmentNodeGraph.getGraph()));
            }
        }
    }

    public GraphNode getResponsibleAgentFromArray(GraphNode node) {
        try {
            for (int i = 0; i < lastGenGAs.length; i++) {
                if (lastGenGAs[i].getGa().getBaseGenome() != null)
                    if (lastGenGAs[i].getGa().getBaseGenome().contains(node)) {
                        return lastGenGAs[i].getLastAgent();
                    }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GraphNode getResponsibleAgent(GraphNode node) {
        if (node == null) {
            for (Map.Entry<GraphNode, List<GraphNode>> entry : this.taskMap.entrySet()) {
                GraphNode agent = entry.getKey();
                List<GraphNode> task = entry.getValue();
                if (task.isEmpty()) {
                    return agent;
                }
            }
        }
        for (Map.Entry<GraphNode, List<GraphNode>> entry : this.taskMap.entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> task = entry.getValue();
            for (GraphNode n : task) {
                if (node.getGraphNodeId() == n.getGraphNodeId()) {
                    return agent;
                }
            }
        }
        return null;
    }

    public HashMap<GraphNode, List<GraphNode>> getTaskMap() {
        return taskMap;
    }

    public <P extends Problem<I>, I extends Individual> int addLastGenGA(GeneticAlgorithm ipGeneticAlgorithm, int index) {
        if (index == -1) {
            for (int i = 0; i < lastGenGAs.length; i++) {
                try {
                    if (lastGenGAs[i].getGa() == null) {
                        lastGenGAs[i].setGa(ipGeneticAlgorithm);
                        if (ipGeneticAlgorithm.getBaseGenome() == null) {
                            lastGenGAs[i].setLastAgent(getResponsibleAgent(null));
                        } else {
                            lastGenGAs[i].setLastAgent(getResponsibleAgent((GraphNode) ipGeneticAlgorithm.getBaseGenome().get(0)));
                        }
                        lastGenGAs[i].setGenBestFitness(new Float[ipGeneticAlgorithm.getMaxGenerations() + 1]);
                        lastGenGAs[i].setGenAvgFitness(new Float[ipGeneticAlgorithm.getMaxGenerations() + 1]);
                        return i;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            lastGenGAs[index].setGa(ipGeneticAlgorithm);
            lastGenGAs[index].addGenFitValue(ipGeneticAlgorithm.getGeneration(), ipGeneticAlgorithm.getBestInRun());
            lastGenGAs[index].addAvgFitValue(ipGeneticAlgorithm.getGeneration(), (float) ipGeneticAlgorithm.getAverageFitness());

            return index;
        }
        return -1;
    }

    public EnvironmentNodeGraph getRespectiveEnvironment(GraphNode node) {
        for (int i = 0; i < lastGenGAs.length; i++) {
            if (lastGenGAs[i].getGa().getBaseGenome() != null)
                if (lastGenGAs[i].getGa().getBaseGenome().contains(node)) {
                    return lastGenGAs[i].getEnvironment();
                }
        }
        return null;
    }

    public void fixMultipleGAs() {
        HashMap<GraphNode, List<FitnessNode>> fullResult = new HashMap<>();
        HashMap<GraphNode, List<GraphNode>> taskOnly = new HashMap<>();
        float fitness = 0;
        float time = 0;

        for (int i = 0; i < lastGenGAs.length; i++) {
            taskOnly.putAll(lastGenGAs[i].getGa().getBestInRun().getResults().getTaskedAgentsOnly());
            fullResult.putAll(lastGenGAs[i].getGa().getBestInRun().getResults().getTaskedAgentsFullNodes());
            if (lastGenGAs[i].getGa().getBestInRun().getResults().getTime() > time) {
                time = lastGenGAs[i].getGa().getBestInRun().getResults().getTime();
                fitness = lastGenGAs[i].getGa().getBestInRun().getResults().getFitness();
            }
        }
        bestInRun = new FitnessResults();
        bestInRun.setTaskedAgentsFullNodes(fullResult);
        bestInRun.setTaskedAgentsOnly(taskOnly);
        bestInRun.setTime(time);
        bestInRun.setFitness(fitness);
    }

    public void setMultipleGA(boolean b) {
        this.multipleGA = b;
    }

    public Individual getDefaultBestInRun() {
        return defaultBestInRun;
    }

    public void setDefaultBestInRun(Individual defaultBestInRun) {
        this.defaultBestInRun = defaultBestInRun;
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
            float amplify_x = Float.parseFloat(graphElement.getAttributes().getNamedItem("amplify_x").getNodeValue());
            float amplify_y = Float.parseFloat(graphElement.getAttributes().getNamedItem("amplify_y").getNodeValue());


            for (int i = 0; i < graphElement.getChildNodes().getLength(); i++) {
                if (graphElement.getChildNodes().item(i).getNodeName().equals("Nodes")) {
                    graphNodes = parseNodes(graphElement.getChildNodes().item(i));
                }
            }
            graph.setgraphNodes(graphNodes);
            //graph.deAmplify(amplify_x);
            for (int i = 0; i < graphElement.getChildNodes().getLength(); i++) {
                if (graphElement.getChildNodes().item(i).getNodeName().equals("Edges")) {
                    edgeList = parseEdges(graphElement.getChildNodes().item(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.mainFrame.logMessage("Error reading graph file", 1);
            this.mainFrame.logMessage(e.getMessage(), 1);
        }
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

    public Graph getGraph() {
        return graph;
    }

    public CommunicationVariables getCommunication_variables() {
        return communication_variables;
    }

    public void setCommunication_variables(CommunicationVariables communication_variables) {
        this.communication_variables = communication_variables;
    }

    public void parseTarefaXML(String content) {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(content));

            Document doc = db.parse(is);
            NodeList tarefas_nodes = doc.getElementsByTagName("Tarefa");

            List<Tarefa> tarefas = new ArrayList<>();

            for (int j = 0; j < tarefas_nodes.getLength(); j++) {
                Element element = (Element) tarefas_nodes.item(j);
                Tarefa tarefa = new Tarefa();
                for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                    switch (element.getChildNodes().item(i).getNodeName()) {
                        case "Ordem":
                            tarefa.setOrdem(element.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                            break;
                        case "LinhaOrdem":
                            tarefa.setLinhaOrdem(element.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                            break;
                        case "Produto":
                            tarefa.setProduto(element.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                            break;
                        case "Quantidade":
                            tarefa.setQuantidade(Integer.parseInt(element.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                            break;
                        case "Origem":
                            tarefa.setOrigem(element.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                            break;
                        case "Destino":
                            tarefa.setDestino(element.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                            break;
                    }
                }
                tarefas.add(tarefa);
            }
            communication_variables.setTarefas(tarefas);
            communication_variables.setTasks_ready(true);
            simulateOperator();

            System.out.println("Received " + communication_variables.getTarefas().size() + " products from ERP");
        } catch (Exception e) {
            mainFrame.logMessage("Error " + e.getMessage(), 1);
            e.printStackTrace();
        }

    }

    //Document doc = builder.newDocument();

}
