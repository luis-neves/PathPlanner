package utils.Graphs;

import armazem.AStar;
import picking.Item;
import utils.warehouse.Colision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentNodeGraph {

    private Graph graph;
    private Item[] previousItemSet;
    private FitnessResults previousFitness;


    public EnvironmentNodeGraph(Graph graph) {
        this.graph = graph;
        this.previousFitness = new FitnessResults();
        //runEnvironment();
    }

    public void setupEnvironment() {

    }

    private boolean isSameSet(Item[] items1, Item[] items2) {
        for (int i = 0; i < items1.length; i++) {
            if (items1[i].node.getGraphNodeId() != items2[i].node.getGraphNodeId()) {
                return false;
            }
        }
        return true;
    }

    public FitnessResults calculatePath(HashMap<GraphNode, List<GraphNode>> pathMap) {
        AStar aStar = new AStar(graph);
        FitnessResults results = new FitnessResults();
        List<GraphNode> finalPath = new ArrayList<>();

        for (Map.Entry<GraphNode, List<GraphNode>> entry : pathMap.entrySet()) {
            GraphNode agent = entry.getKey();
            aStar.setInitialGraphNode(agent);
            List<GraphNode> agentFinalPath = new ArrayList<>();
            List<GraphNode> products = entry.getValue();

            for(GraphNode prod: products){
                if(products.indexOf(prod) != products.lastIndexOf(prod)){
                    System.out.println("problem");
                }
            }

            for (int i = 0; i < products.size(); i++){
                GraphNode product = products.get(i);
                aStar.setFinalGraphNode(product);
                agentFinalPath.addAll(aStar.findGraphPath(products));
                aStar.setInitialGraphNode(product);
            }
            aStar.setFinalGraphNode(graph.getExit());
            agentFinalPath.addAll(aStar.findGraphPath(products));
            if(agentFinalPath.size() >= 4){
                if (agentFinalPath.get(0).equals(agentFinalPath.get(2)) && agentFinalPath.get(1).equals(agentFinalPath.get(3))){
                    agentFinalPath.remove(0);
                    agentFinalPath.remove(1);
                }
            }
            if (agentFinalPath.get(0).getType() == GraphNodeType.AGENT) {
                agentFinalPath.remove(0);
            }
            EnvironmentNodeGraph.FitnessCosts costs = calculateFitness(agentFinalPath, agent);
            results.addTaskedAgent(agent, agentFinalPath, costs.costs);
        }
        //System.out.println(results);

        float highest = Float.MIN_VALUE;
        for (Map.Entry<GraphNode, List<FitnessNode>> entry : results.getTaskedAgentsFullNodes().entrySet()) {
            GraphNode agent = entry.getKey();
            List<FitnessNode> finalcosts = entry.getValue();
            float sum = 0;
            for (int i = 0; i < finalcosts.size(); i++) {
                sum += finalcosts.get(i).getCost();
            }
            if (sum > highest) {
                highest = sum;
            }
        }
        results.setFitness(highest);
        results.setPath(finalPath);
        results.setTime(highest);
        results.setTaskedAgentsOnly(pathMap);
        results = checkColisions2(results);

        return results;
    }

    public FitnessResults calculatePaths(Item[] items, GraphNode lastAgent) {
        if (previousItemSet != null && isSameSet(items, previousItemSet)) {
            return previousFitness;
        }
        lastAgent = findEqual(lastAgent);

        FitnessResults results = new FitnessResults();
        List<GraphNode> finalPath = new ArrayList<>();
        List<Item> agentPath = new ArrayList<>();
        List<GraphNode> agentFinalPath = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            List<GraphNode> products_for_tasks = new ArrayList<>();
            if (items[i].node.getType() == GraphNodeType.PRODUCT) {
                agentPath.add(items[i]);
            } else if (items[i].node.getType() == GraphNodeType.AGENT) {
                AStar aStar = new AStar(graph);
                aStar.setInitialGraphNode(findEqual(items[i].node));
                for (int j = 0; j < agentPath.size(); j++) {
                    products_for_tasks.add(findEqual(agentPath.get(j).node));
                }
                for (int j = 0; j < agentPath.size(); j++) {
                    aStar.setFinalGraphNode(findEqual(agentPath.get(j).node));
                    agentFinalPath.addAll(aStar.findGraphPath(products_for_tasks));
                    aStar.setInitialGraphNode(findEqual(agentPath.get(j).node));
                }
                if (!agentPath.isEmpty()) {
                    aStar.setFinalGraphNode(findExits(graph).get(0));
                    agentFinalPath.addAll(aStar.findGraphPath(products_for_tasks));
                    finalPath.addAll(agentFinalPath);
                }
                results.addTaskedAgentOnly(findEqual(items[i].node), products_for_tasks);
                FitnessCosts costs = calculateFitness(agentFinalPath, findEqual(items[i].node));
                if (costs.costs.size() != agentFinalPath.size()) {
                    System.out.println("Diferent costs");
                }
                results.addTaskedAgent(findEqual(items[i].node), agentFinalPath, costs.costs);
                //System.out.println("\nFitness 1 - " + fitness + " - ");
                //printFinalPath(agentFinalPath);

                aStar.setInitialGraphNode(null);
                aStar.setFinalGraphNode(null);
                agentPath.clear();
                agentFinalPath.clear();
                products_for_tasks.clear();
            }
        }
        AStar aStar = new AStar(graph);
        aStar.setInitialGraphNode(lastAgent);
        List<GraphNode> products_for_tasks = new ArrayList<>();

        for (int j = 0; j < agentPath.size(); j++) {
            products_for_tasks.add(findEqual(agentPath.get(j).node));
        }

        for (int j = 0; j < agentPath.size(); j++) {
            aStar.setFinalGraphNode(findEqual(agentPath.get(j).node));
            agentFinalPath.addAll(aStar.findGraphPath(products_for_tasks));
            aStar.setInitialGraphNode(findEqual(agentPath.get(j).node));
        }
        if(agentFinalPath.size() >= 4){
            if (agentFinalPath.get(0).equals(agentFinalPath.get(2)) && agentFinalPath.get(1).equals(agentFinalPath.get(3))){
                agentFinalPath.remove(0);
                agentFinalPath.remove(1);
            }
        }

        results.addTaskedAgentOnly(lastAgent, products_for_tasks);
        if (!agentPath.isEmpty()) {
            aStar.setFinalGraphNode(findExits(graph).get(0));
            agentFinalPath.addAll(aStar.findGraphPath(products_for_tasks));
            finalPath.addAll(agentFinalPath);
        }


        //System.out.println("\nFitness 2 - " + calculateFitness(agentFinalPath) +  " ");
        //printFinalPath(agentFinalPath);
        FitnessCosts costs = calculateFitness(agentFinalPath, lastAgent);
        if (costs.costs.size() != agentFinalPath.size()) {
            System.out.println("Diferent costs");
        }
        results.addTaskedAgent(lastAgent, agentFinalPath, costs.costs);
        //System.out.println("Full F - " + fitness);

        //printFinalPath(agentFinalPath);

        aStar.setInitialGraphNode(null);
        aStar.setFinalGraphNode(null);
        agentPath.clear();
        agentFinalPath.clear();
        products_for_tasks.clear();
        //System.out.println("FINISHED A*");
        //FOR EXIT

        float highest = Float.MIN_VALUE;
        for (Map.Entry<GraphNode, List<FitnessNode>> entry : results.getTaskedAgentsFullNodes().entrySet()) {
            GraphNode agent = entry.getKey();
            List<FitnessNode> finalcosts = entry.getValue();
            float sum = 0;
            for (int i = 0; i < finalcosts.size(); i++) {
                sum += finalcosts.get(i).getCost();
            }
            if (sum > highest) {
                highest = sum;
            }
        }
        results.setFitness(highest);

        results.setPath(finalPath);
        results.setTime(highest);
        results = checkColisions2(results);
        results = fixRepetedProduct(results);
        //checkColisions2(results);
        previousFitness = results;
        previousItemSet = items;
        return results;
    }

    public static FitnessResults checkColisions2(FitnessResults results) {
        results = fixRepetedProduct(results);
        int nColisions = 0;
        float collisionsPenalty = 0;
        List<GraphNode> agentsDone = new ArrayList<>();
        List<Colision> colisions = new ArrayList<>();

        for (Map.Entry<GraphNode, List<FitnessNode>> entry : results.getTaskedAgentsFullNodesNoPackages().entrySet()) {
            GraphNode agent = entry.getKey();
            List<FitnessNode> pathNodes = entry.getValue();
            for (int i = 0; i < pathNodes.size(); i++) {
                FitnessNode currentFullNode = pathNodes.get(i);
                for (Map.Entry<GraphNode, List<FitnessNode>> entry2 : results.getTaskedAgentsFullNodesNoPackages().entrySet()) {
                    GraphNode agent2 = entry2.getKey();
                    if (!agent.equals(agent2) && !agentsDone.contains(agent2)) {
                        List<FitnessNode> pathNodes2 = entry2.getValue();
                        for (int j = 0; j < pathNodes2.size(); j++) {
                            if (pathNodes2.get(j).getNode().equals(pathNodes.get(i).getNode())) {
                                try {
                                    Float time = 0f;
                                    if (i == 0) {

                                    } else {
                                        time = pathNodes.get(i - 1).getTime();
                                    }
                                    Float time1 = currentFullNode.getTime();
                                    Float time2 = 0f;
                                    if (j == 0) {

                                    } else {
                                        time2 = pathNodes2.get(j - 1).getTime();
                                    }
                                    Float cost3 = pathNodes2.get(j).getTime();
                                    //Float currenttime2 = results.getTaskedAgentsCostsTime().get(agent2).get(pathNodes2.indexOf(currentNode));
                                    if ((i != 0 && time.equals(time2) && pathNodes.get(i - 1).getNode().equals(pathNodes2.get(j - 1).getNode())) || time1.equals(cost3)) {
                                        if (currentFullNode.getNode().getNeighbourEdge(pathNodes2.get(j).getNode()).getNum_directions() <= 1) {
                                            Colision c = new Colision();
                                            c.addAgent(agent);
                                            c.addAgent(agent2);
                                            c.addNode(currentFullNode.getNode());
                                            c.addTime(time);
                                            c.addTime(time1);
                                            c.addTime(time2);
                                            c.addTime(cost3);
                                            c.setType("Same Node(s)");
                                            colisions.add(c);
                                            //System.out.println("Same Node " + currentFullNode.getNode().getGraphNodeId());
                                            //System.out.println("Costs [" + time + " , " + time1 + "]-[" + time2 + " , " + cost3 + "]");
                                            //System.out.println(Math.min((time1 - cost), (cost3 - time2)));
                                            collisionsPenalty += Math.min((time1 - time), (cost3 - time2));
                                            nColisions++;
                                            //System.out.println(results.printTaskedAgents());
                                        }
                                    }
                                    //a--c  cost - time1
                                    //b--d  time2 - cost3
                                    //a < d || c < b

                                    if (i < pathNodes.size() - 1) {
                                        GraphNode nextNode = pathNodes.get(i + 1).getNode();
                                        /*if(inSameIle(currentFullNode.getNode(), nextNode, pathNodes2.get(j).getNode(), pathNodes2.get(j-1).getNode())){
                                            System.out.println();
                                        }*/
                                        if (j != 0 && (pathNodes2.get(j - 1).getNode().equals(nextNode)) && pathNodes.get(i).getNode().getGraphNodeId() != pathNodes.get(i + 1).getNode().getGraphNodeId()) {
                                            if (currentFullNode.getNode().getNeighbourEdge(pathNodes2.get(j).getNode()).getNum_directions() <= 1) {
                                                float a = pathNodes.get(i).getTime();
                                                float c = pathNodes.get(i + 1).getTime();
                                                float b = pathNodes2.get(j - 1).getTime();
                                                float d = pathNodes2.get(j).getTime();
                                                if (a <= d && c >= b) {
                                                    //System.out.println("Intersection " + currentFullNode.getNode().getType
                                                    //().toLetter() + currentFullNode.getNode().getGraphNodeId() + "," + nextNode.getType().toLetter() + nextNode.getGraphNodeId());
                                                    //System.out.println("Costs [" + a + " , " + c + "]-[" + b + " , " + d + "]");
                                                    //System.out.println(Math.min((c - a), (d - b)));
                                                    collisionsPenalty += Math.min((c - a), (d - b));
                                                    nColisions++;
                                                    Colision colision = new Colision();
                                                    colision.addAgent(agent);
                                                    colision.addAgent(agent2);
                                                    colision.addNode(pathNodes.get(i).getNode());
                                                    colision.addNode(pathNodes.get(i + 1).getNode());
                                                    colision.addNode(pathNodes2.get(j - 1).getNode());
                                                    colision.addNode(pathNodes2.get(j).getNode());
                                                    colision.addTime(a);
                                                    colision.addTime(c);
                                                    colision.addTime(b);
                                                    colision.addTime(d);
                                                    colision.setType("Intersection");
                                                    colisions.add(colision);
                                                    //System.out.println(results.printTaskedAgents());
                                                }
                                            }
                                        }
                                        if (j != 0 && i != 0 && inSameIle(pathNodes.get(i - 1).getNode(), currentFullNode.getNode(), pathNodes2.get(j).getNode(), pathNodes2.get(j - 1).getNode())) {
                                            float a = pathNodes.get(i - 1).getTime();
                                            float c = pathNodes.get(i).getTime();
                                            float b = pathNodes2.get(j - 1).getTime();
                                            float d = pathNodes2.get(j).getTime();
                                            if (a <= d && c >= b) {
                                                if (pathNodes.get(i - 1).getNode().equals(currentFullNode.getNode()) && (c - b) >= (d - b) / 2) {
                                                    //collisionsPenalty += Math.min((c - a), (d - b));
                                                    collisionsPenalty += Math.min((c - a), (d - b));
                                                    nColisions++;
                                                    Colision colision = new Colision();
                                                    colision.addAgent(agent);
                                                    colision.addAgent(agent2);
                                                    colision.addNode(pathNodes.get(i - 1).getNode());
                                                    colision.addNode(pathNodes.get(i).getNode());
                                                    colision.addNode(pathNodes2.get(j - 1).getNode());
                                                    colision.addNode(pathNodes2.get(j).getNode());
                                                    colision.addTime(a);
                                                    colision.addTime(c);
                                                    colision.addTime(b);
                                                    colision.addTime(d);
                                                    colision.setType("Super Intersection");
                                                    colisions.add(colision);
                                                }
                                            }
                                        }
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            agentsDone.add(agent);
        }/*
        if (nColisions > 0) {
            System.out.println("Nº Collision\t" + nColisions);
            System.out.println("Collision(s) Penalty\t" + collisionsPenalty);
        }*//*
        if (nColisions > 1){
            results.setFitness(1);
            //System.out.println("Num collisions" + nColisions);
        }*/
        results.setColisions(colisions);
        results.setNumCollisions(nColisions);
        results.setCollisionPenalty(collisionsPenalty);
        return results;
    }

    public static boolean inSameIle(GraphNode first, GraphNode second, GraphNode second2, GraphNode first2) {
        if (first.getGraphNodeId() == second.getGraphNodeId() && second.getGraphNodeId() == second2.getGraphNodeId()) {

            if (first.getVerticalSimpleNode().contains(first2) && first.getVerticalSimpleNode().size() < 2) {
                return true;
            }
        }
        if (first2.getGraphNodeId() == second2.getGraphNodeId() && first2.getGraphNodeId() == second.getGraphNodeId()) {
            if (first2.getVerticalSimpleNode().contains(first) && first2.getVerticalSimpleNode().size() < 2) {
                return true;
            }
        }
        return false;
    }

    public static FitnessResults fixRepetedProduct(FitnessResults results) {
        for (Map.Entry<GraphNode, List<FitnessNode>> entry : results.getTaskedAgentsFullNodes().entrySet()) {
            GraphNode agent = entry.getKey();
            List<FitnessNode> pathNodes = entry.getValue();
            List<FitnessNode> toRemove = new ArrayList<>();
            for (int i = 0; i < pathNodes.size() - 1; i++) {
                if (pathNodes.get(i).getNode().getGraphNodeId() == pathNodes.get(i + 1).getNode().getGraphNodeId() && pathNodes.get(i + 1).getCost() == 0) {
                    toRemove.add(pathNodes.get(i + 1));
                }
            }
            pathNodes.removeAll(toRemove);
            toRemove.clear();
        }
        return results;
    }

    private FitnessCosts calculateFitness(List<GraphNode> finalPath, GraphNode agent) {
        if (!finalPath.isEmpty()) {
            float fitness = 0;
            List<Float> costs = new ArrayList<>();
            costs.add(finalPath.get(0).getDistance(agent));
            for (int i = 0; i < finalPath.size(); i++) {
                if (i < finalPath.size() - 1) {
                    GraphNode start = finalPath.get(i);
                    GraphNode end = finalPath.get(i + 1);
                    float distance = start.getDistance(end);
                    costs.add(distance);
                    fitness += distance;
                }
            }
            //costs.add(finalPath.get(finalPath.size() - 1).getDistance(findExits(graph).get(0)));
            return new FitnessCosts(costs, fitness);
        }
        return new FitnessCosts(new ArrayList<>(), 0);
    }

    public Graph getGraph() {
        return this.graph;
    }


    public static class FitnessCosts {
        public List<Float> costs = new ArrayList<>();
        float finalCost = 0;

        public FitnessCosts(List<Float> costs, float finalCost) {
            this.costs = costs;
            this.finalCost = finalCost;
        }
    }

    public void printFinalPath(List<GraphNode> finalPath) {
        for (int i = 0; i < finalPath.size(); i++) {
            System.out.print("[" + finalPath.get(i).getType().toLetter() + finalPath.get(i).getGraphNodeId() + "]");
        }
        System.out.println();
    }

    private GraphNode findEqual(GraphNode node) {
        for (int i = 0; i < graph.getGraphNodes().size(); i++) {
            if (graph.getGraphNodes().get(i).getGraphNodeId() == node.getGraphNodeId()) {
                return graph.getGraphNodes().get(i);
            }
        }
        return null;
    }

    public void runEnvironment() {
        AStar aStar = new AStar(graph);
        aStar.setInitialGraphNode(findProducts(graph).get(0));
        aStar.setFinalGraphNode(findProducts(graph).get(1));
        /*List<GraphNode> path = aStar.findGraphPath();
        System.out.println(path.size());
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).toString());
        }*/
    }

    public List<GraphNode> findAgents(Graph graph) {
        List<GraphNode> agents = new ArrayList<>();
        for (int i = 0; i < graph.getGraphNodes().size(); i++) {
            if (graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT) {
                agents.add(graph.getGraphNodes().get(i));
            }
            ;
        }
        return agents;
    }

    public GraphNode findAgent(GraphNode agent) {
        for (int i = 0; i < graph.getGraphNodes().size(); i++) {
            if (graph.getGraphNodes().get(i).getType() == GraphNodeType.AGENT && graph.getGraphNodes().get(i).getGraphNodeId() == agent.getGraphNodeId()) {
                return graph.getGraphNodes().get(i);
            }
            ;
        }
        return null;
    }

    public List<GraphNode> findExits(Graph graph) {
        List<GraphNode> exits = new ArrayList<>();
        for (int i = 0; i < graph.getGraphNodes().size(); i++) {
            if (graph.getGraphNodes().get(i).getType() == GraphNodeType.EXIT) {
                exits.add(graph.getGraphNodes().get(i));
            }
            ;
        }
        return exits;
    }

    public List<GraphNode> findProducts(Graph graph) {
        List<GraphNode> products = new ArrayList<>();
        for (int i = 0; i < graph.getGraphNodes().size(); i++) {
            if (graph.getGraphNodes().get(i).getType() == GraphNodeType.PRODUCT) {
                products.add(graph.getGraphNodes().get(i));
            }
            ;
        }
        return products;
    }
}
