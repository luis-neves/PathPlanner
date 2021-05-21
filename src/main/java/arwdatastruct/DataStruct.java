package arwdatastruct;

import algorithms.Problem;
import algorithms.Solution;
import newWarehouse.Warehouse;
import orderpicking.GNode;
import orderpicking.Pick;
import orderpicking.PickingOrders;
import algorithms.SingleOrderDyn;
import pathfinder.Graph;
import pathfinder.Route;
import whgraph.ARWGraph;
import whgraph.ARWGraphNode;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static whgraph.GraphNodeType.PRODUCT;

public class DataStruct {

    private Warehouse warehouse;
    private ARWGraph arwGraph;
    private ArrayList<Agent> availableAgents;
    private Hashtable<String, Agent> occupiedAgents;
    private ArrayList<Order> pendingOrders;
    //assignedOrders: orders que estão a aguardar feedback da realidade aumentada de que foram concluídas
    private Hashtable<String, Order> assignedOrders;
    private String defaultDestiny = "13.S.0.0";

    public final int MIN_NUM_AGENTS = 2;

    public DataStruct() {
        this.warehouse = new Warehouse();
        this.arwGraph = new ARWGraph();
        this.availableAgents = new ArrayList<>();
        this.occupiedAgents = new Hashtable<>();
        this.pendingOrders = new ArrayList<>();
        this.assignedOrders = new Hashtable<>();
    }

    public void setWareHouseFromXMLFile(String filename) {
        try {
            String contents = new String(Files.readAllBytes(Paths.get(filename)));
            warehouse.createFromXML(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    //Função para atribuição do grafo, lido de ficheiro ou criado/editado com editor
    public void setGraph(ARWGraph arwGraph) {
        this.arwGraph = arwGraph;
    }

    public void setDefaultDestiny(String defaultDestiny) {
        this.defaultDestiny = defaultDestiny;
    }

    //Adiciona um novo agente quando ficar disponível
    public void addAvailableAgent(Agent agent) {
        availableAgents.add(agent);
    }

    //Retira um agente caso os óculos sejam desligados
    public void removeAvailableAgent(String agentId) {
        for (Agent agent : availableAgents)
            if (agent.getId().equals(agentId)) {
                availableAgents.remove(agent);
                break;
            }
    }

    public Integer getNumberOfAvailableAgents() {
        return availableAgents.size();
    }

    public Integer getNumberOfPendingOrders() {
        return pendingOrders.size();
    }

    public boolean checkOccupiedAgent(String agentId) {
        return occupiedAgents.containsKey(agentId);
    }

    public void releaseAgent(String agentId, float newX, float newY) {
        Agent agent = occupiedAgents.get(agentId);
        agent.setInitialX(newX);
        agent.setInitialY(newY);
        availableAgents.add(agent);
        occupiedAgents.remove(agentId);
    }

    //Carrega o pedido do ERP recebido como XML
    public void addOrdersToPendingOrders(String xmlERPRequest) {
        PickingOrders pickingOrders = new PickingOrders();
        pickingOrders.parseXMLERPRequest(xmlERPRequest);
        pendingOrders.addAll(pickingOrders.getOrders());
    }

    //Trata tarefas pendentes
    public HashMap<Agent, String> handlePendingOrders() {

        if (!((availableAgents.size() >= MIN_NUM_AGENTS) && (pendingOrders.size() > 0))) {
            return null;
        }

        String xmlString = "";

        Order order = pendingOrders.get(0);
        Hashtable<String, Pick> picks = order.getPicks();
        String pickKey = (String) picks.keySet().toArray()[0];
        String destiny = picks.get(pickKey).getDestiny();
        if (!warehouse.checkWms(destiny))
            destiny = defaultDestiny;

        Agent agent = availableAgents.get(0);

        xmlString = planPath(picks, agent, destiny);
        assignedOrders.put(agent.getId(), order);
        occupiedAgents.put(agent.getId(), agent);
        pendingOrders.remove(0);
        availableAgents.remove(0);

        HashMap<Agent, String> tasksAssignment = new HashMap<>();
        tasksAssignment.put(agent, xmlString);
        return tasksAssignment;
    }

    //A hashtable dos picks está organizada
    public String planPath(Hashtable<String, Pick> picks, Agent agent, String destiny) {

        if ((warehouse == null) || (arwGraph == null) || (arwGraph.getNumberOfNodes() == 0)) {
            System.out.println("Armazem ou grafo não definido!");
            return "";
        }

        //Constroi grafo com todos os nós, incluindo produtos

        ARWGraph problemGraph = arwGraph.clone();

        //picksAtNode: estrutura que guarda os picks em cada nó
        Hashtable<Integer, ArrayList<Pick>> picksAtNode = new Hashtable<>();

        List<Pick> problemPicks = new ArrayList<>();

        //Colocar os nós dos produtos no grafo
        for (String pickKey : picks.keySet()) {
            Pick pick = picks.get(pickKey);
            Point2D.Float rack = warehouse.getWms(pick.getOrigin());
            int numberOfNodes = problemGraph.getNumberOfNodes();
            int graphNodeID = problemGraph.insertNode(
                    new ARWGraphNode(numberOfNodes, rack.x, rack.y, PRODUCT)).getGraphNodeId();
            if (picksAtNode.containsKey(graphNodeID))
                picksAtNode.get(graphNodeID).add(pick);
            else {
                ArrayList<Pick> picksList = new ArrayList<>();
                picksList.add(pick);
                picksAtNode.put(graphNodeID, picksList);
            }

            pick.setNode(problemGraph.findNode(graphNodeID));
            problemPicks.add(pick);
        }

        //Os passos abaixo para a criação do vetor produtos são para simplificar. Serviram para aprendizagem.
        //Os prods parece ser uma lista de ids de nós e não de produtos
        ArrayList<String> productsIDsAuxList = new ArrayList<>();
        for (int id : picksAtNode.keySet())
            productsIDsAuxList.add(new Integer(id).toString());

        //String[] productsIDs = productsIDsAuxList.toArray(new String[productsIDsAuxList.size()]);

        Graph<GNode> graph = problemGraph.getPathGraph();

        //Determinar ponto de entrega
        //Colocar depois dentro do ciclo de leitura
        ARWGraphNode startNode = arwGraph.findClosestNode(agent.getInitialX(), agent.getInitialY());
        int endNode = arwGraph.findClosestNode(warehouse.getWms(destiny).x, warehouse.getWms(destiny).y).getGraphNodeId();

        List<Agent> agents = new LinkedList<>();
        agent.setStartNode(Integer.toString(startNode.getGraphNodeId()));
        agent.setEndNode(new Integer(endNode).toString());
        agents.add(agent);

        Problem problem = new Problem(graph, problemPicks, agents);

        SingleOrderDyn orderDyn = new SingleOrderDyn();

        Solution solution = orderDyn.solve(problem);
        Route route = solution.getRoute(agent);

        Task task = new Task(route.getRoute(), graph, picksAtNode);

        String xmlString = task.XMLPath(agent.getId(), picksAtNode.keys().toString());
        System.out.println("Tour:\n " + xmlString);

        // Print: 42.0
        System.out.println("Tour cost: " + route.getCost());

        return xmlString;
    }

    public void concludeOrder(String orderId) {
        assignedOrders.remove(orderId);
    }

}
