package armazem;



import java.util.*;

public class AStar {
    private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
    private static int DEFAULT_DIAGONAL_COST = 14;
    private int hvCost;
    private int diagonalCost;
    private Cell[][] searchArea;
    private PriorityQueue<Cell> openList;
    private Set<Cell> closedSet;
    private Cell initialNode;
    private Cell finalNode;

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

    public AStar(int line, int col, Cell initialNode, Cell finalNode) {
        this(line ,col, initialNode,finalNode, DEFAULT_HV_COST, DEFAULT_DIAGONAL_COST);
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

    public void calculaHeuristica(){
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {

               // this.searchArea[i][j] = new Cell(i, j);
                searchArea[i][j].calcularHeuristica(getFinalNode());
            }
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

    private void addAdjacentNodes(Cell currentNode) {
        addAdjacentUpperRow(currentNode);
        addAdjacentMiddleRow(currentNode);
        addAdjacentLowerRow(currentNode);
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

    private boolean isEmpty(PriorityQueue<Cell> openList) {
        return openList.size() == 0;
    }

    public void setBlocks(List<Prateleira> prateleiras) {
        for(Prateleira p: prateleiras){
            setBlock(p.getCell().getLine(), p.getCell().getColumn());
        }
    }

    public void setEncomendas(List<Encomenda> encomendas) {
        for(Encomenda p: encomendas){
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
