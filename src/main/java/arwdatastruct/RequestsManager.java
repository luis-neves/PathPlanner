package arwdatastruct;

import algorithms.*;
import newWarehouse.Warehouse;
import orderpicking.GNode;
import orderpicking.Pick;
import orderpicking.Request;
import pathfinder.Graph;
import whgraph.ARWGraph;
import whgraph.ARWGraphNode;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static whgraph.GraphNodeType.PRODUCT;

public class RequestsManager {
    private Warehouse warehouse;
    private ARWGraph arwGraph;
    private List<Agent> availableAgents;
    private Map<String, Agent> occupiedAgents;
    private List<OneAgentOneDestinyTask> pendingTasks;

    private String defaultDestiny = "13.S.0.0";
    public final int MIN_NUM_AGENTS = 2;

    public RequestsManager() {
        this.warehouse = new Warehouse();
        this.arwGraph = new ARWGraph();
        this.availableAgents = new LinkedList<>();
        this.occupiedAgents = new HashMap<>();
        this.pendingTasks = new LinkedList<>();
    }

    //Carrega o pedido do ERP recebido como XML
    //Grilo: policy seguida - cada task tem os picks para uma saída de uma order
    //Para já o método devolve false se houver algum destino não definido no armazém, e true em caso contrário...
    public boolean addRequest(String xmlERPRequest) {
        Request request = new Request();
        request.parseXMLERPRequest(xmlERPRequest);

//        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
//        for (Order order : request.getOrders()) {
//            List<Pick> orderPicks = new ArrayList<>(order.getPicks());
//            for (Pick pick : orderPicks) {
//                System.out.println(pick);
//            }
//        }
//        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");

        for (Order order : request.getOrders()) {

            List<Pick> orderPicks = new ArrayList<>(order.getPicks());

            Map<String, OneAgentOneDestinyTask> tasksPerOrderDestiny = new HashMap<>();

            for (Pick pick : orderPicks) {
                String destiny = pick.getDestiny();
                if (!warehouse.checkWms(destiny))
                    return false; //TODO Lançar exceção apropriada
                OneAgentOneDestinyTask task = tasksPerOrderDestiny.get(pick.getDestiny());
                if (task == null) {
                    task = new OneAgentOneDestinyTask(request, destiny);
                    request.incNumberOfUnfinishedTasks();
                    tasksPerOrderDestiny.put(pick.getDestiny(), task);
                }
                task.addPick(pick);
            }
            pendingTasks.addAll(new LinkedList(tasksPerOrderDestiny.values()));
        }

//        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
//        for (Task task : pendingTasks) {
//            System.out.println(task);
//        }
//        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

        return true;
    }

    //Caso a task seja a última que faltava para terminhar o request, devolve o request
    //Caso contrário devolve null;
    //O agente é "libertado" no TOPIC_OPAVAIL do PathPlanner com o método releaseAgent()
    public Request closeTask(String agentID, List<Pick> picks) {

        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println("Agent: " + agentID);
        System.out.println("Task\n");
        for (Pick pick : picks) {
            System.out.println(pick);
        }
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

        Agent agent = occupiedAgents.get(agentID);
        Request request = agent.getTask().getRequest();
        request.decNumberTasksUnfinished();
        request.addSolvedPicks(picks);
        agent.setTask(null);

        return request.isSolved()? request : null;
    }

    //Trata tarefas pendentes
    public Map<Agent, String> handlePendingTasks() {

        if (warehouse == null || arwGraph == null || arwGraph.getNumberOfNodes() == 0) {
            System.out.println("Armazem ou grafo não definido!");
            return null; //TODO Lançar exceção apropriada
        }

        if (availableAgents.size() < MIN_NUM_AGENTS || pendingTasks.size() < MIN_NUM_AGENTS) {
            return null; //TODO Lançar exceção apropriada
        }

        int numberOfTasksToProcess = Math.min(availableAgents.size(), pendingTasks.size());
        List<Agent> auxAgents = availableAgents.subList(0, numberOfTasksToProcess);
        List<Agent> agents = new LinkedList<>(auxAgents);
        List<OneAgentOneDestinyTask> auxTasks = pendingTasks.subList(0, numberOfTasksToProcess);
        List<OneAgentOneDestinyTask> tasksToBeProcessed = new LinkedList<>(auxTasks);

        System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
        for (Task task : tasksToBeProcessed) {
            System.out.println(task);
        }
        System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

        SingleOrderDyn orderDyn = new SingleOrderDyn();
        Map<Agent, String> tasksAssignment = new HashMap<>();

        //Atribuir tasks aos agentes (critério: centro de massa) e resolver cada task
        for (OneAgentOneDestinyTask task : tasksToBeProcessed) {

            ////////////////////////////

            //Constrói um grafo com todos os nós, incluindo os picks de todas as tasks a processar a seguir

            ARWGraph problemGraph = arwGraph.clone();
            //picksAtNode: estrutura que guarda os picks em cada nó
            Map<Integer, ArrayList<Pick>> picksAtNode = new HashMap<>();

//            List<Pick> picksToBeProcessed = new LinkedList<>();
//            for (OneAgentOneDestinyTask task : tasksToBeProcessed) {
//                picksToBeProcessed.addAll(task.getPicks());
//            }

            //Colocar os nós dos picks no grafo
            for (Pick pick : task.getPicks()) {
                Point2D.Float rack = warehouse.getWms(pick.getOrigin());
                int numberOfNodes = problemGraph.getNumberOfNodes();
                int graphNodeID = problemGraph.insertNode(
                        new ARWGraphNode(numberOfNodes, rack.x, rack.y, PRODUCT)).getGraphNodeId();
                if (!picksAtNode.containsKey(graphNodeID)){
                    picksAtNode.put(graphNodeID, new ArrayList<>());
                }
                picksAtNode.get(graphNodeID).add(pick);
                pick.setNode(problemGraph.findNode(graphNodeID));
            }

            //Transformação do grafo inicial para o grafo específico para o A*
            Graph<GNode> graph = problemGraph.getPathGraph();

            /////////////////////////////////////

            task.setGraph(graph);
            task.setPicksAtNode(picksAtNode);
            task.computeMassCenter();
            double smallerDistanceToMassCenter = Double.MAX_VALUE;
            Agent assignedAgent = null;
            for (Agent agent : agents) {
                double distance = Math.sqrt(
                        Math.pow(agent.getInitialX() - task.getMassCenterX(), 2) + Math.pow(agent.getInitialY() - task.getMassCenterY(), 2));
                if (distance < smallerDistanceToMassCenter) {
                    smallerDistanceToMassCenter = distance;
                    assignedAgent = agent;
                }
            }
            ARWGraphNode startNode = arwGraph.findClosestNode(
                    assignedAgent.getInitialX(),
                    assignedAgent.getInitialY());
            ARWGraphNode endNode = arwGraph.findClosestNode(
                    warehouse.getWms(task.getDestiny()).x,
                    warehouse.getWms(task.getDestiny()).y);
            assignedAgent.setStartNode(Integer.toString(startNode.getGraphNodeId()));
            assignedAgent.setEndNode(Integer.toString(endNode.getGraphNodeId()));

            agents.remove(assignedAgent);
            task.setAgent(assignedAgent);
            assignedAgent.setTask(task);

            Solution solution = orderDyn.solve(task);
            task.setRoute(solution.getRoute(assignedAgent));
            tasksAssignment.put(assignedAgent, task.XMLPath());
            occupiedAgents.put(assignedAgent.getId(), assignedAgent);
            availableAgents.remove(assignedAgent);

            pendingTasks.remove(task);
        }

        return tasksAssignment;
    }

    /////////////////////////////////////////////////////////////////////////////

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

    public void releaseAgent(String agentId, float newX, float newY) {
        Agent agent = occupiedAgents.get(agentId);
        agent.setInitialX(newX);
        agent.setInitialY(newY);
        agent.setStartNode(null);
        agent.setEndNode(null);
        availableAgents.add(agent);
        occupiedAgents.remove(agentId);
    }

    public Integer getNumberOfPendingOrders() {
        return pendingTasks.size();
    }

    public boolean checkOccupiedAgent(String agentId) {
        return occupiedAgents.containsKey(agentId);
    }

}

//    //Trata tarefas pendentes
//    public HashMap<Agent, String> handlePendingOrders() {
//
//        if (!((availableAgents.size() >= MIN_NUM_AGENTS) && (pendingTasks.size() > 0))) {
//            return null;
//        }
//
//        String xmlString = "";
//
//        Order order = pendingTasks.get(0);
//        HashMap<String, Pick> picks = order.getPicks();
//        String pickKey = (String) picks.keySet().toArray()[0];
//        String destiny = picks.get(pickKey).getDestiny();
//        if (!warehouse.checkWms(destiny))
//            destiny = defaultDestiny;
//
//        Agent agent = availableAgents.get(0);
//
//        xmlString = planPath(picks, agent, destiny);
//
//        List<Agent> agents = new LinkedList<>();
//        agents.add(agent);
//
//        order.setAssignedAgents(agents);
//
//        assignedTasks.put(order.getId(), order);
//        occupiedAgents.put(agent.getId(), agent);
//        pendingTasks.remove(0);
//        availableAgents.remove(0);
//
//        HashMap<Agent, String> tasksAssignment = new HashMap<>();
//        tasksAssignment.put(agent, xmlString);
//        return tasksAssignment;
//    }
//
//
//    public String planPath(HashMap<String, Pick> picks, Agent agent, String destiny) {
//
//        if ((warehouse == null) || (arwGraph == null) || (arwGraph.getNumberOfNodes() == 0)) {
//            System.out.println("Armazem ou grafo não definido!");
//            return "";
//        }
//
//        //Constroi grafo com todos os nós, incluindo produtos
//
//        ARWGraph problemGraph = arwGraph.clone();
//
//        //picksAtNode: estrutura que guarda os picks em cada nó
//        Hashtable<Integer, ArrayList<Pick>> picksAtNode = new Hashtable<>();
//
//        List<Pick> problemPicks = new ArrayList<>();
//
//        //Colocar os nós dos produtos no grafo
//        for (String pickKey : picks.keySet()) {
//            Pick pick = picks.get(pickKey);
//            Point2D.Float rack = warehouse.getWms(pick.getOrigin());
//            int numberOfNodes = problemGraph.getNumberOfNodes();
//            int graphNodeID = problemGraph.insertNode(
//                    new ARWGraphNode(numberOfNodes, rack.x, rack.y, PRODUCT)).getGraphNodeId();
//            if (picksAtNode.containsKey(graphNodeID))
//                picksAtNode.get(graphNodeID).add(pick);
//            else {
//                ArrayList<Pick> picksList = new ArrayList<>();
//                picksList.add(pick);
//                picksAtNode.put(graphNodeID, picksList);
//            }
//
//            pick.setNode(problemGraph.findNode(graphNodeID));
//            problemPicks.add(pick);
//        }
//
//        //Os passos abaixo para a criação do vetor produtos são para simplificar. Serviram para aprendizagem.
//        //Os prods parece ser uma lista de ids de nós e não de produtos
//        ArrayList<String> productsIDsAuxList = new ArrayList<>();
//        for (int id : picksAtNode.keySet())
//            productsIDsAuxList.add(new Integer(id).toString());
//
//        //String[] productsIDs = productsIDsAuxList.toArray(new String[productsIDsAuxList.size()]);
//
//        Graph<GNode> graph = problemGraph.getPathGraph();
//
//        //Determinar ponto de entrega
//        //Colocar depois dentro do ciclo de leitura
//        ARWGraphNode startNode = arwGraph.findClosestNode(agent.getInitialX(), agent.getInitialY());
//        int endNode = arwGraph.findClosestNode(warehouse.getWms(destiny).x, warehouse.getWms(destiny).y).getGraphNodeId();
//
//        agent.setStartNode(Integer.toString(startNode.getGraphNodeId()));
//        agent.setEndNode(new Integer(endNode).toString());
//
//        OneDestinyProblem problem = new OneDestinyProblem(graph, problemPicks, agent, destiny);
//
//        SingleOrderDyn orderDyn = new SingleOrderDyn();
//
//        Solution solution = orderDyn.solve(problem);
//        Route route = solution.getRoute(agent);
//
//        Task task = new Task(route.getNodes(), graph, picksAtNode);
//
//        String xmlString = task.XMLPath(agent.getId(), picksAtNode.keys().toString());
//        System.out.println("Tour:\n " + xmlString);
//
//        // Print: 42.0
//        System.out.println("Tour cost: " + route.getCost());
//
//        return xmlString;
//    }
