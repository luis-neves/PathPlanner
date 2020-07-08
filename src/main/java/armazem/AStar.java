package armazem;


import utils.Graphs.Edge;
import utils.Graphs.Graph;
import utils.Graphs.GraphNode;
import utils.Graphs.GraphNodeType;

import java.util.*;

public class AStar {
    private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
    private static int DEFAULT_DIAGONAL_COST = 14;
    private Graph graph;
    private int hvCost;
    private int diagonalCost;
    private Cell[][] searchArea;
    //private GraphNode[][] graphNodeSearchArea;
    private PriorityQueue<Cell> openList;
    private PriorityQueue<GraphNode> openGraphList;
    private Set<Cell> closedSet;
    private Set<GraphNode> closedNodeSet;
    private Cell initialNode;
    private Cell finalNode;
    private GraphNode finalGraphNode;

    public PriorityQueue<GraphNode> getOpenGraphList() {
        return openGraphList;
    }

    public void setOpenGraphList(PriorityQueue<GraphNode> openGraphList) {
        this.openGraphList = openGraphList;
    }

    public Set<GraphNode> getClosedNodeSet() {
        return closedNodeSet;
    }

    public void setClosedNodeSet(Set<GraphNode> closedNodeSet) {
        this.closedNodeSet = closedNodeSet;
    }

    public void setFinalGraphNode(GraphNode finalGraphNode) {
        if(finalGraphNode != null) {
            this.finalGraphNode = graph.findNode(finalGraphNode.getGraphNodeId());
        }
    }

    public GraphNode getInitialGraphNode() {
        return initialGraphNode;
    }

    public void setInitialGraphNode(GraphNode initialGraphNode) {
        this.initialGraphNode = initialGraphNode;
    }

    private GraphNode initialGraphNode;

    public AStar(int line, int col, Cell initialNode, Cell finalNode, int hvCost, int diagonalCost) {
        this.hvCost = hvCost;
        this.diagonalCost = diagonalCost;
        setInitialNode(initialNode);
        setFinalNode(finalNode);
        this.searchArea = new Cell[line][col];
        this.openList = new PriorityQueue<Cell>(new Comparator<Cell>() {
            @Override
            public int compare(Cell node0, Cell node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        });
        setNodes();
        this.closedSet = new HashSet<>();
    }

    public AStar(GraphNode[][] graphNodes) {
        this.hvCost = DEFAULT_HV_COST;
        this.diagonalCost = DEFAULT_DIAGONAL_COST;
        /*this.graphNodeSearchArea = new GraphNode[graphNodes.length][graphNodes[0].length];
        setGraphNodes(graphNodes);
        this.graphNodeSearchArea = graphNodes;*/
        this.openGraphList = new PriorityQueue<GraphNode>(new Comparator<GraphNode>() {
            @Override
            public int compare(GraphNode node0, GraphNode node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        });
        this.closedNodeSet = new HashSet<>();
    }

    public AStar(Graph graph) {
        this.graph = graph;
        this.hvCost = DEFAULT_HV_COST;
        this.diagonalCost = DEFAULT_DIAGONAL_COST;
        this.openGraphList = new PriorityQueue<GraphNode>(new Comparator<GraphNode>() {
            @Override
            public int compare(GraphNode node0, GraphNode node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        });
        this.closedNodeSet = new HashSet<>();
    }

    public AStar(int line, int col, Cell initialNode, Cell finalNode) {
        this(line, col, initialNode, finalNode, DEFAULT_HV_COST, DEFAULT_DIAGONAL_COST);
    }

    /*
    public List<GraphNode> findGraphPath() {
        calculaHeuristicaNodes();
        openGraphList.clear();
        closedNodeSet.clear();
        openGraphList.add(initialGraphNode);

        while (!isEmptyGraph(openGraphList)) {
            GraphNode currentNode = openGraphList.poll();   //get last
            closedNodeSet.add(currentNode);                 //remove current node
            //System.out.println(currentNode.toString() + getFinalGraphNode().toString());
            if (isFinalGraphNode(currentNode)) {
                return getGraphPath(currentNode);
            } else {
                addAdjacentGraphNodes(currentNode);
            }
        }
        return new ArrayList<GraphNode>();
    }*/
    public List<GraphNode> findGraphPath(List<GraphNode> taskedNodes) {
        calculaHeuristicaNodes();
        openGraphList.clear();
        closedNodeSet.clear();
        openGraphList.add(initialGraphNode);

        while (!isEmptyGraph(openGraphList)) {
            GraphNode currentNode = openGraphList.poll();   //get last
            closedNodeSet.add(currentNode);                 //remove current node
            if (isFinalGraphNode(currentNode)) {
                return getGraphPath(currentNode);
            } else {
                addAdjacentGraphNodes(currentNode, taskedNodes);
            }
        }
        return new ArrayList<GraphNode>();
    }

    public List<Cell> findPath() {
        calculaHeuristica();
        openList.clear();
        closedSet.clear();
        openList.add(initialNode);

        while (!isEmpty(openList)) {
            Cell currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode)) {
                return getPath(currentNode);
            } else {
                addAdjacentNodes(currentNode);
            }
        }
        return new ArrayList<Cell>();
    }

    private void setNodes() {
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {

                this.searchArea[i][j] = new Cell(i, j);
                //searchArea[i][j].calcularHeuristica(getFinalNode());
            }
        }
    }
    /*
    private void setGraphNodes(GraphNode[][] nodes) {
        for (int i = 0; i < graphNodeSearchArea.length; i++) {
            for (int j = 0; j < graphNodeSearchArea[0].length; j++) {
                GraphNode node = nodes[i][j];
                this.graphNodeSearchArea[i][j] = node;
                //searchArea[i][j].calcularHeuristica(getFinalNode());
            }
        }
    }*/

    public void calculaHeuristica() {
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {

                // this.searchArea[i][j] = new Cell(i, j);
                searchArea[i][j].calcularHeuristica(getFinalNode());
            }
        }
    }

    public void calculaHeuristicaNodes() {
        try {
            for (int i = 0; i < graph.getGraphNodes().size(); i++) {
                graph.getGraphNodes().get(i).calculateHeuristic(getFinalGraphNode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int findPath2() {
        calculaHeuristica();
        openList.clear();
        closedSet.clear();
        openList.add(initialNode);
        while (!isEmpty(openList)) {
            Cell currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode)) {
                return getPath(currentNode).size();
            } else {
                addAdjacentNodes(currentNode);
            }
        }
        return 0;
    }

    private List<Cell> getPath(Cell currentNode) {
        List<Cell> path = new ArrayList<Cell>();
        path.add(currentNode);
        Cell parent;
        while ((parent = currentNode.getParent()) != null) {
            path.add(0, parent);
            currentNode = parent;
        }
        return path;
    }

    private List<GraphNode> getGraphPath(GraphNode currentNode) {
        List<GraphNode> path = new ArrayList<GraphNode>();
        path.add(currentNode);
        GraphNode parent;
        while ((parent = currentNode.getParent()) != null && parent.getParent() != currentNode) {
            //System.out.println(currentNode.getParent());
            path.add(0, parent);
            currentNode = parent;
        }
        return path;
    }

    private void addAdjacentNodes(Cell currentNode) {
        addAdjacentUpperRow(currentNode);
        addAdjacentMiddleRow(currentNode);
        addAdjacentLowerRow(currentNode);
    }

    private void addAdjacentGraphNodes(GraphNode currentNode, List<GraphNode> taskedNodes) {
        List<GraphNode> neighbourNodes = currentNode.getNeighbourNodesWithoutProducts(taskedNodes);
        //System.out.println("DEBUG" +currentNode);
        for (int i = 0; i < neighbourNodes.size(); i++) {
            if (!getClosedNodeSet().contains(neighbourNodes.get(i))) {
                if (!openGraphList.contains(neighbourNodes.get(i))) {
                    neighbourNodes.get(i).setNodeData(currentNode, currentNode.getNodeWeight(neighbourNodes.get(i)));
                    openGraphList.add(neighbourNodes.get(i));
                }
            } else {
                continue;
//                boolean changed = neighbourNodes.get(i).checkBetterPath(currentNode);
//                if (changed) {
//                    //System.out.println("BETTERPATH" + neighbourNodes.get(i));
//                    getOpenGraphList().remove(neighbourNodes.get(i));
//                    getOpenGraphList().add(neighbourNodes.get(i));
//                }
            }
            //openGraphList.addAll(neighbourNodes);
        }

    }

    private void addAdjacentLowerRow(Cell currentNode) {
        int row = currentNode.getLine();
        int col = currentNode.getColumn();
        int lowerRow = row + 1;
        if (lowerRow < getSearchArea().length) {
           /* if (col - 1 >= 0) {
                checkNode(currentNode, col - 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }*/
            checkNode(currentNode, col, lowerRow, getHvCost());
        }
    }

    private void addAdjacentMiddleRow(Cell currentNode) {
        int row = currentNode.getLine();
        int col = currentNode.getColumn();
        int middleRow = row;
        if (col - 1 >= 0) {
            checkNode(currentNode, col - 1, middleRow, getHvCost());
        }
        if (col + 1 < getSearchArea()[0].length) {
            checkNode(currentNode, col + 1, middleRow, getHvCost());
        }
    }

    private void addAdjacentUpperRow(Cell currentNode) {
        int row = currentNode.getLine();
        int col = currentNode.getColumn();
        int upperRow = row - 1;
        if (upperRow >= 0) {
           /* if (col - 1 >= 0) {
                checkNode(currentNode, col - 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }*/
            checkNode(currentNode, col, upperRow, getHvCost());
        }
    }

    private void checkNode(Cell currentNode, int col, int row, int cost) {
        Cell adjacentNode = getSearchArea()[row][col];
        if (!adjacentNode.hasPrateleira() && !getClosedSet().contains(adjacentNode)) {
            if (!getOpenList().contains(adjacentNode)) {
                adjacentNode.setNodeData(currentNode, cost);
                getOpenList().add(adjacentNode);
            } else {
                boolean changed = adjacentNode.checkBetterPath(currentNode, cost);
                if (changed) {
                    // Remove and Add the changed node, so that the PriorityQueue can sort again its
                    // contents with the modified "finalCost" value of the modified node
                    getOpenList().remove(adjacentNode);
                    getOpenList().add(adjacentNode);
                }
            }
        }
    }


    private boolean isFinalNode(Cell currentNode) {
        return currentNode.equals(finalNode);
    }

    private boolean isFinalGraphNode(GraphNode currentNode) {
        return currentNode.equals(finalGraphNode);
    }

    private boolean isEmpty(PriorityQueue<Cell> openList) {
        return openList.size() == 0;
    }

    private boolean isEmptyGraph(PriorityQueue<GraphNode> openList) {
        return openList.size() == 0;
    }

    public void setBlocks(List<Prateleira> prateleiras) {
        for (Prateleira p : prateleiras) {
            setBlock(p.getCell().getLine(), p.getCell().getColumn());
        }
    }

    public void setEncomendas(List<Encomenda> encomendas) {
        for (Encomenda p : encomendas) {
            setEncomenda(p.getCell().getLine(), p.getCell().getColumn());
        }
    }

    private void setEncomenda(int row, int col) {
        this.searchArea[row][col].setEncomenda(new Encomenda(this.searchArea[row][col].getPrateleira()));
    }

    private void setBlock(int row, int col) {
        this.searchArea[row][col].setPrateleira(new Prateleira(this.searchArea[row][col]));
    }

    public Cell getInitialNode() {
        return initialNode;
    }

    public void setInitialNode(Cell initialNode) {
        this.initialNode = initialNode;
    }

    public Cell getFinalNode() {
        return finalNode;
    }

    public GraphNode getFinalGraphNode() {
        return finalGraphNode;
    }

    public void setFinalNode(Cell finalNode) {
        this.finalNode = finalNode;
    }

    public Cell[][] getSearchArea() {
        return searchArea.clone();
    }

    public void setSearchArea(Cell[][] searchArea) {
        this.searchArea = searchArea;
    }

    public PriorityQueue<Cell> getOpenList() {
        return openList;
    }

    public void setOpenList(PriorityQueue<Cell> openList) {
        this.openList = openList;
    }

    public Set<Cell> getClosedSet() {
        return closedSet;
    }

    public void setClosedSet(Set<Cell> closedSet) {
        this.closedSet = closedSet;
    }

    public int getHvCost() {
        return hvCost;
    }

    public void setHvCost(int hvCost) {
        this.hvCost = hvCost;
    }

    private int getDiagonalCost() {
        return diagonalCost;
    }

    private void setDiagonalCost(int diagonalCost) {
        this.diagonalCost = diagonalCost;
    }
}
