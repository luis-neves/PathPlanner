package clustering;

import armazem.AStar;
import ga.*;
import ga.KMeans.AstarDistance;
import ga.KMeans.MyCluster;
import picking.PickingIndividual;
import utils.Graphs.*;
import weka.clusterers.SimpleKMeans;
import weka.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clustering {

    Graph problemGraph;
    private int MAX_KMEANS_ITERATIONS = 20;
    private int seed;
    private final transient List<GAListener> listeners = new ArrayList<>(3);

    private Individual bestInRun;

    public Clustering(Graph problemGraph, int MAX_KMEANS_ITERATIONS) {
        this.problemGraph = problemGraph;
        this.MAX_KMEANS_ITERATIONS = MAX_KMEANS_ITERATIONS;
    }

    public Clustering(int seed) {
        this.seed = seed;
        problemGraph = GASingleton.getInstance().getSimulationPanel().getProblemGraph();
    }

    public FitnessResults calculatePath(HashMap<GraphNode, List<GraphNode>> pathMap) {
        AStar aStar = new AStar(problemGraph);
        FitnessResults results = new FitnessResults();
        List<GraphNode> finalPath = new ArrayList<>();
        for (Map.Entry<GraphNode, List<GraphNode>> entry : pathMap.entrySet()) {
            GraphNode agent = entry.getKey();
            aStar.setInitialGraphNode(agent);
            List<GraphNode> agentFinalPath = new ArrayList<>();
            List<GraphNode> products = entry.getValue();
            for (GraphNode product : products) {
                if (product == null) {
                    System.out.println("bad");
                }
                aStar.setFinalGraphNode(product);
                agentFinalPath.addAll(aStar.findGraphPath(products));
                aStar.setInitialGraphNode(product);
            }
            aStar.setFinalGraphNode(problemGraph.getExit());
            agentFinalPath.addAll(aStar.findGraphPath(products));

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

        return results;
    }

    private EnvironmentNodeGraph.FitnessCosts calculateFitness(List<GraphNode> finalPath, GraphNode agent) {
        if (!finalPath.isEmpty()) {
            float fitness = 0;
            List<Float> costs = new ArrayList<>();
            costs.add(finalPath.get(0).getDistance(agent));
            for (int i = 0; i < finalPath.size(); i++) {
                if (i < finalPath.size() - 1) {
                    GraphNode start = finalPath.get(i);
                    GraphNode end = finalPath.get(i + 1);
                    costs.add(start.getDistance(end));
                }
            }
            //costs.add(finalPath.get(finalPath.size() - 1).getDistance(findExits(graph).get(0)));
            return new EnvironmentNodeGraph.FitnessCosts(costs, fitness);
        }
        return new EnvironmentNodeGraph.FitnessCosts(new ArrayList<>(), 0);
    }

    public HashMap<GraphNode, List<GraphNode>> generateClusters(int seed, boolean apply_heuristic) throws Exception {
        SimpleKMeans kmeans = new SimpleKMeans();
        kmeans.setNumClusters(problemGraph.getAgentsNum());
        kmeans.setMaxIterations(MAX_KMEANS_ITERATIONS);
        kmeans.setSeed(seed);
        kmeans.setPreserveInstancesOrder(true);
        GASingleton.getInstance().setProblemGraph(problemGraph);
        DistanceFunction function = new AstarDistance();
        kmeans.setDistanceFunction(function);

        Attribute PT1 = new Attribute("X");
        Attribute w1 = new Attribute("Y");
        // Declare the feature vector
        FastVector fvWekaAttributes = new FastVector(7);
        // Add attributes
        fvWekaAttributes.addElement(PT1);
        fvWekaAttributes.addElement(w1);

        // Declare Instances which is required since I want to use classification/Prediction
        Instances dataset = new Instances("whatever", fvWekaAttributes, 0);

        //Creating a double array and defining values
        for (int i = 0; i < problemGraph.getProducts().size(); i++) {
            double[] attValues = new double[dataset.numAttributes()];
            attValues[0] = (int) problemGraph.getProducts().get(i).getLocation().getX();
            attValues[1] = (int) problemGraph.getProducts().get(i).getLocation().getY();
            ;
            Instance i1 = new DenseInstance(1.0, attValues);
            dataset.add(i1);
        }
        /*
        for (int i = 0; i < problemGraph.getAgents().size(); i++) {
            double[] attValues = new double[dataset.numAttributes()];
            attValues[0] = (int) problemGraph.getAgents().get(i).getLocation().getX();
            attValues[1] = (int) problemGraph.getAgents().get(i).getLocation().getY();
            ;
            Instance i1 = new DenseInstance(1.0, attValues);
            dataset.add(i1);
        }*/

        //Create the new instance i1

        //Add the instance to the dataset (Instances) (first element 0)
        //Define class attribute position
        //dataset.setClassIndex(dataset.numAttributes() - 1);

        //Will print 0 if it's a "yes", and 1 if it's a "no"

        try {
            kmeans.buildClusterer(dataset);
            //System.out.println(Arrays.toString(kmeans.getAssignments()));
            Instances centroids = kmeans.getClusterCentroids();
            List<MyCluster> clusters = new ArrayList<>();
            HashMap<Integer, ArrayList<GraphNode>> clusterMap = new HashMap<>();
            DistanceFunction function1 = new AstarDistance();
            kmeans.setDistanceFunction(function1);
            for (int i = 0; i < problemGraph.getAgentsNum(); i++) {
                //System.out.print("Cluster " + i + " size: " + kmeans.getClusterSizes()[i]);
                clusters.add(new MyCluster(i));
                clusterMap.put(i, new ArrayList<>());
                //System.out.println(" Centroid: " + centroids.instance(i));
            }
            for (int i = 0; i < problemGraph.getProducts().size(); i++) {
                List<GraphNode> clusterList = clusterMap.get(kmeans.getAssignments()[i]);
                problemGraph.getProducts().get(i).setCluster(findMyCluster(kmeans.getAssignments()[i], clusters));
                clusterList.add(problemGraph.getProducts().get(i));
            }
            attributeAgentsToCluster(clusters);
            HashMap<GraphNode, List<GraphNode>> taskMap;
            if(apply_heuristic) {
                taskMap = generateTasks();
            }else{
                taskMap = randomTask();
            }
            return taskMap;
        } catch (Exception ex) {
            System.err.println("Unable to buld Clusterer: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    private HashMap<GraphNode, List<GraphNode>> randomTask() {
        HashMap<GraphNode, List<GraphNode>> taskMap = new HashMap<>();
        for (GraphNode agent : problemGraph.getAgents()) {
            MyCluster cluster = agent.getCluster();
            if(cluster == null){
                System.out.println("null cluster");
            }
            taskMap.put(agent, problemGraph.getProductsByCluster(agent.getCluster()));
        }
        return taskMap;
    }

    private HashMap<GraphNode, List<GraphNode>> generateTasks() {
        HashMap<GraphNode, List<GraphNode>> taskMap = new HashMap<>();
        for (GraphNode agent : problemGraph.getAgents()) {
            MyCluster cluster = agent.getCluster();
            List<GraphNode> products = new ArrayList<>();
            products.addAll(problemGraph.getProductsByCluster(cluster));
            List<GraphNode> task = new ArrayList<>();
            GraphNode closestToExit = null;
            for (GraphNode product : products) {
                if (closestToExit == null || product.getDistance(problemGraph.getExit()) <= closestToExit.getDistance(problemGraph.getExit())) {
                    closestToExit = product;
                }
            }
            if (closestToExit != null) {
                task.add(closestToExit);
                products.remove(closestToExit);
            }
            GraphNode current = closestToExit;
            do {
                GraphNode closestToCurrent = null;
                if (!products.isEmpty()) {
                    for (GraphNode product : products) {
                        if (closestToCurrent == null || product.getDistance(current) <= closestToCurrent.getDistance(current)) {
                            closestToCurrent = product;
                        }
                    }
                    if (closestToCurrent == null) {
                        System.out.println("null closest");
                    }
                    task.add(0, closestToCurrent);
                    products.remove(closestToCurrent);
                }
            } while (!products.isEmpty());
            taskMap.put(agent, task);
        }
        return taskMap;
    }



    private MyCluster findMyCluster(int assignment, List<MyCluster> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            if (clusters.get(i).getId() == assignment) {
                return clusters.get(i);
            }
        }
        return null;
    }

    private void attributeAgentsToCluster(List<MyCluster> clusters) {
        List<MyCluster> usedClusters = new ArrayList<>();
        for (GraphNode agent : problemGraph.getAgents()) {
            GraphNode closest_product = null;
            for (GraphNode product : problemGraph.getProducts()) {
                if (closest_product == null) {
                    if (!usedClusters.contains(product.getCluster())) {
                        closest_product = product;
                    }
                } else {
                    if ((product.getDistance(agent) <= closest_product.getDistance(agent) && !usedClusters.contains(product.getCluster()))) {
                        closest_product = product;
                    }
                }
            }
            if (closest_product == null){
                for (MyCluster cluster : clusters){
                    if (!usedClusters.contains(cluster)){
                        agent.setCluster(cluster);
                        usedClusters.add(agent.getCluster());
                    }
                }
            }else {
                try {
                    agent.setCluster(closest_product.getCluster());
                    usedClusters.add(agent.getCluster());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        checkRepeatedCluster();
    }

    private void checkRepeatedCluster() {
        List<MyCluster> clusters = new ArrayList<>();
        for (GraphNode agent : problemGraph.getAgents()) {
            if (!clusters.contains(agent.getCluster())) {
                clusters.add(agent.getCluster());
            } else {
                try {
                    throw new Exception("Multiple Agents in a single cluster");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public <P extends Problem> void run(P problem) throws Exception {
        this.bestInRun = GASingleton.getInstance().getDefaultBestInRun();
        bestInRun.setResults(calculatePath(generateClusters(seed, false)));
        GeneticAlgorithm ga = GASingleton.getInstance().getDefaultGA();
        EnvironmentNodeGraph.checkColisions2(bestInRun.getResults());
        ga.setBestInRun(bestInRun);
        fireRunEnded(new GAEvent(ga));
    }

    public void fireRunEnded(GAEvent e) {
        for (GAListener listener : listeners) {
            listener.runEnded(e);
        }
    }

    public synchronized void addGAListener(GAListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
}
