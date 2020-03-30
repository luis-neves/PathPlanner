package ga;

import armazem.Cell;
import gui.PanelTextArea;
import gui.SimulationPanel;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import picking.Item;
import utils.Graphs.FitnessResults;
import utils.Graphs.GraphNode;
import utils.Graphs.GraphNodeType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class GASingleton {
    private static GASingleton instance;

    private List<Item> items;
    private int distanceMatrix[][];
    private List<Item> finalSet;
    private int fitnessType;
    private int[][] grid;
    private boolean nodeProblem;
    private GraphNode lastAgent;
    private SimulationPanel simulationPanel;
    private FitnessResults bestInRun;
    private PanelTextArea bestIndividualPanel;

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
        this.finalSet = new ArrayList<>();
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
                System.out.println();
            }
        } else {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    System.out.print(matrix[i][j] == null ? "------- \t" : matrix[i][j] + "\t");
                }
                System.out.println();
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
            this.finalSet = items;
        }
        //String missing = getMissingAgent(finalSet);
        if (SimulationPanel.environment != null) {
            return SimulationPanel.environment.showPath(items, show);
        }
        return null;
    }

    public FitnessResults checkResultsForColision(FitnessResults results) {
        if (SimulationPanel.environmentNodeGraph != null) {
            return SimulationPanel.environmentNodeGraph.checkColisions2(results);
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
        this.finalSet = new ArrayList<>();
        this.distanceMatrix = null;
        this.matrixCounter = 0;
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

    public void generateXMLPath(HashMap<GraphNode, List<GraphNode>> taskedAgents) {
        try {


            for (Map.Entry<GraphNode, List<GraphNode>> entry : taskedAgents.entrySet()) {
                String xmlFilePath = "..\\" + entry.getKey().getGraphNodeId() + ".xml";

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

                Document document = documentBuilder.newDocument();

                // root element
                Element root = document.createElement("Task");
                Attr attrAgent = document.createAttribute("agent-id");
                attrAgent.setValue(entry.getKey().getGraphNodeId() + "");
                root.setAttributeNode(attrAgent);
                Attr attRoot = document.createAttribute("id");
                attRoot.setValue(entry.getKey().getGraphNodeId() + "" + 1);
                root.setAttributeNode(attRoot);
                document.appendChild(root);

                GraphNode agent = entry.getKey();
                List<GraphNode> agentPath = entry.getValue();
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
                        nattr.setValue(agentPath.get(i).getGraphNodeId() + "");
                        Element node = document.createElement("Node");
                        node.setAttributeNode(nattr);
                        if (agentPath.get(i).getType() == GraphNodeType.PRODUCT) {
                            Attr productiD = document.createAttribute("product-id");
                            productiD.setValue(agentPath.get(i).getGraphNodeId() + "");
                            node.setAttributeNode(productiD);
                        }
                        node.appendChild(agentPath.get(i).generateXMLelement(document));
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

            }
            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
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


    //Document doc = builder.newDocument();

}
