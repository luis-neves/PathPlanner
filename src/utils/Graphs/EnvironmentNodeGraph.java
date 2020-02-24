package utils.Graphs;

import armazem.AStar;
import ga.GASingleton;
import jdk.jfr.StackTrace;
import org.w3c.dom.Node;
import picking.Item;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnvironmentNodeGraph {

    private GraphNode[][] nodeMatrix;
    private Graph graph;
    private Item[] previousItemSet;
    private FitnessResults previousFitness;

    public EnvironmentNodeGraph(GraphNode[][] nodeMatrix) {
        this.nodeMatrix = nodeMatrix;
        runEnvironment();
    }

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

    public FitnessResults calculatePaths(Item[] items) {
        if (previousItemSet != null && isSameSet(items, previousItemSet)) {
            return previousFitness;
        }
        FitnessResults results = new FitnessResults();
        List<GraphNode> finalPath = new ArrayList<>();
        List<Item> agentPath = new ArrayList<>();
        List<GraphNode> agentFinalPath = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            if (items[i].node.getType() == GraphNodeType.PRODUCT) {
                agentPath.add(items[i]);
            } else if (items[i].node.getType() == GraphNodeType.AGENT) {
                AStar aStar = new AStar(graph);
                aStar.setInitialGraphNode(findEqual(items[i].node));
                for (int j = 0; j < agentPath.size(); j++) {
                    aStar.setFinalGraphNode(agentPath.get(j).node);
                    agentFinalPath.addAll(aStar.findGraphPath());
                    aStar.setInitialGraphNode(agentPath.get(j).node);
                }
                if (!agentPath.isEmpty()) {
                    aStar.setFinalGraphNode(findExits(graph).get(0));
                    agentFinalPath.addAll(aStar.findGraphPath());
                    finalPath.addAll(agentFinalPath);
                }

                FitnessCosts costs = calculateFitness(agentFinalPath);
                results.addTaskedAgent(findEqual(items[i].node), agentFinalPath, costs.costs);
                //System.out.println("\nFitness 1 - " + fitness + " - ");
                //printFinalPath(agentFinalPath);

                aStar.setInitialGraphNode(null);
                aStar.setFinalGraphNode(null);
                agentPath.clear();
                agentFinalPath.clear();
            }
        }

        AStar aStar = new AStar(graph);
        aStar.setInitialGraphNode(GASingleton.getInstance().getLastAgent());

        for (int j = 0; j < agentPath.size(); j++) {
            aStar.setFinalGraphNode(findEqual(agentPath.get(j).node));
            agentFinalPath.addAll(aStar.findGraphPath());
            aStar.setInitialGraphNode(findEqual(agentPath.get(j).node));
        }
        if (!agentPath.isEmpty()) {
            aStar.setFinalGraphNode(findExits(graph).get(0));
            agentFinalPath.addAll(aStar.findGraphPath());
            finalPath.addAll(agentFinalPath);
        }
        //System.out.println("\nFitness 2 - " + calculateFitness(agentFinalPath) +  " ");
        //printFinalPath(agentFinalPath);
        FitnessCosts costs = calculateFitness(agentFinalPath);

        results.addTaskedAgent(GASingleton.getInstance().getLastAgent(), agentFinalPath, costs.costs);
        //System.out.println("Full F - " + fitness);

        //printFinalPath(agentFinalPath);

        aStar.setInitialGraphNode(null);
        aStar.setFinalGraphNode(null);
        agentPath.clear();
        agentFinalPath.clear();
        //System.out.println("FINISHED A*");
        //FOR EXIT

        float highest = Float.MIN_VALUE;
        for (Map.Entry<GraphNode, List<Float>> entry : results.getTaskedAgentsCosts().entrySet()) {
            GraphNode agent = entry.getKey();
            List<Float> finalcosts = entry.getValue();
            float sum = 0;
            for (int i = 0; i < finalcosts.size(); i++) {
                sum += finalcosts.get(i);
            }
            if (sum > highest) {
                highest = sum;
            }
        }
        results.setFitness(highest);
        results.setPath(finalPath);
        results = checkColisions(results);
        previousFitness = new FitnessResults(highest, finalPath);
        previousFitness.setTaskedAgents(results.getTaskedAgents());
        previousFitness.setTaskedAgentsCosts(results.getTaskedAgentsCosts());
        previousFitness.setTaskedAgentsCostsTime(results.getTaskedAgentsCostsTime());
        previousItemSet = items;
        return results;
    }

    private FitnessResults checkColisions(FitnessResults results) {
        int nColisions = 0;
        float collisionsPenalty = 0;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : results.getTaskedAgents().entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> pathNodes = entry.getValue();
            for (int i = 0; i < pathNodes.size(); i++) {
                GraphNode currentNode = pathNodes.get(i);
                for (Map.Entry<GraphNode, List<GraphNode>> entry2 : results.getTaskedAgents().entrySet()) {
                    GraphNode agent2 = entry2.getKey();
                    if (!agent.equals(agent2)) {
                        List<GraphNode> pathNodes2 = entry2.getValue();
                        if (pathNodes2.contains(currentNode)) {
                            try {
                                List<Float> times1 = results.getTaskedAgentsCostsTime().get(agent);
                                List<Float> times2 = results.getTaskedAgentsCostsTime().get(agent2);
                                Float cost = 0f;
                                if (pathNodes.indexOf(currentNode) - 1 == -1) {

                                } else {
                                    cost = times1.get(pathNodes.indexOf(currentNode) - 1);
                                }
                                Float cost1 = times1.get(pathNodes.indexOf(currentNode));
                                Float cost2 = 0f;
                                if (pathNodes2.indexOf(currentNode) - 1 == -1) {

                                } else {
                                    cost2 = times2.get(pathNodes2.indexOf(currentNode) - 1);
                                }
                                Float cost3 = times2.get(pathNodes2.indexOf(currentNode));
                                //Float currentCost2 = results.getTaskedAgentsCostsTime().get(agent2).get(pathNodes2.indexOf(currentNode));
                                if ((cost.equals(cost2) && (pathNodes.indexOf(currentNode) - 1) != -1) || cost1.equals(cost3)) {
                                    //System.out.println("Same Node " + currentNode.getGraphNodeId());
                                    //System.out.println("Costs [" + cost + " , " + cost1 + "]-[" + cost2 + " , " + cost3 + "]");
                                    //System.out.println(Math.min((cost1 - cost), (cost3 - cost2)));
                                    collisionsPenalty += Math.min((cost1 - cost), (cost3 - cost2));
                                    nColisions++;
                                    //System.out.println(results.printTaskedAgents());
                                }
                                //a--c  cost - cost1
                                //b--d  cost2 - cost3
                                //a < d || c < b

                                if (i < pathNodes.size() - 1) {
                                    GraphNode nextNode = pathNodes.get(i + 1);
                                    if (pathNodes.indexOf(nextNode) == (pathNodes2.indexOf(currentNode) - 1)) {
                                        float a = times1.get(pathNodes.indexOf(currentNode));
                                        float c = times1.get(pathNodes.indexOf(currentNode) + 1);
                                        float b = times2.get(pathNodes2.indexOf(currentNode) - 1);
                                        float d = times2.get(pathNodes2.indexOf(currentNode));
                                        if (a <= d && c >= b) {
                                            //System.out.println("Intersection " + currentNode.getType().toLetter() + currentNode.getGraphNodeId() + "," + nextNode.getType().toLetter() + nextNode.getGraphNodeId());
                                            //System.out.println("Costs [" + a + " , " + c + "]-[" + b + " , " + d + "]");
                                            //System.out.println(Math.min((c - a), (d - b)));
                                            collisionsPenalty += Math.min((c - a), (d - b));
                                            nColisions++;
                                            //System.out.println(results.printTaskedAgents());
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
        if (nColisions > 0) {
            System.out.println("Nº Collision " + nColisions);
            System.out.println("Collision(s) Penalty" + collisionsPenalty);
        }
        results.setNumCollisions(nColisions);
        results.setCollisionPenalty(collisionsPenalty);
        return results;
    }


    private FitnessCosts calculateFitness(List<GraphNode> finalPath) {
        float fitness = 0;
        List<Float> costs = new ArrayList<>();
        for (int i = 0; i < finalPath.size(); i++) {
            if (i < finalPath.size() - 1) {
                GraphNode start = finalPath.get(i);
                GraphNode end = finalPath.get(i + 1);
                for (int j = 0; j < graph.getEdges().size(); j++) {
                    Edge edge = graph.getEdges().get(j);
                    if (edge.getStart() == start && edge.getEnd() == end || edge.getStart() == end && edge.getEnd() == start) {
                        fitness += edge.getWeight();
                        costs.add((float) edge.getWeight());
                    }
                }
            }
        }
        FitnessCosts finalCosts = new FitnessCosts(costs, fitness);
        return finalCosts;
    }

    private class FitnessCosts {
        List<Float> costs = new ArrayList<>();
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
        List<GraphNode> path = aStar.findGraphPath();
        System.out.println(path.size());
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).toString());
        }
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
