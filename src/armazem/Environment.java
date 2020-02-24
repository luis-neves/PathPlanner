package armazem;

import ga.GASingleton;
import picking.Item;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Environment {

    private static final long SEED = 200;
    private static final int NUMBER_ENCOMENDAS = 4;
    private static final int BIGGER_PATH_PRIORITY = 0;
    private static final int SMALLER_PATH_PRIORITY = 1;
    private static final int CLOSEST_TO_EXIT_PRIORITY = 2;
    public static final int RACK = 1;
    public static final int PRODUCT = 2;
    public static final int DEVICE = 3;
    public static final int EXIT = 4;
    private static int ALLIGNMENT = 1;
    private final Cell[][] grid;
    private final LinkedList<Robo> agents;
    private final LinkedList<Prateleira> prateleiras;
    private final LinkedList<Encomenda> encomendas;
    private LinkedList<Cell> caminho;
    private Saida saida;
    private int numLines;
    private int numColumns;
    private AStar aStar;
    private int countNumeroIterecoes;


    private final int numIterations = 2000;
    private int fitnessType = -1;

    public Environment(int numLines, int numColumns, int seed, int numAgentes, int numEncomendas) {
        GASingleton.getInstance().clearData();
        this.numColumns = numColumns;
        this.numLines = numLines;
        grid = new Cell[numLines][numColumns];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }


        // this.grid = aStar.getSearchArea();

        this.prateleiras = new LinkedList<>();
        int aux = 0;
        for (int i = 1; i < numLines - 1; i++) {
            for (int j = 1; j < numColumns - 1; j++) {
                while (j < numColumns - 1) {
                    for (int a = 0; a < 5 && j + a < numColumns - 1; a++) {

                        prateleiras.add(new Prateleira(getCell(i, j + a)));

                    }
                    j = j + 7;
                }
            }
            aux++;
            if (aux > 1) {
                i = i + 2;
                aux = 0;
            }
        }

        encomendas = new LinkedList<Encomenda>();
        agents = new LinkedList<Robo>();

        if (seed != 0) {          // file
            saida = new Saida(getCell(0, numLines - 1));
            for (int i = 0; i < numAgentes; i++) {
                Cell agentsCell = null;
                int flag = 0;
                do {
                    int random = new Random(seed - flag).nextInt(numLines);
                    int random2 = new Random(seed + flag).nextInt(numLines);
                    agentsCell = getCell(random, random2);
                    flag++;
                } while (getCell(agentsCell.getLine(), agentsCell.getColumn()).hasPrateleira() || getCell(agentsCell.getLine(), agentsCell.getColumn()).hasAgent());

                agents.add(new Robo(getCell(agentsCell.getLine(), agentsCell.getColumn())));
            }
            for (Agent a : agents) {
                System.out.println(a.getCell().toString());
            }

            for (int ii = 0; ii < numEncomendas; ii++) {
                int random = new Random(seed + ii).nextInt(prateleiras.size());
                encomendas.add(new Encomenda(prateleiras.get(random)));
            }
        } else {
            saida = new Saida(getCell(0, numColumns - 1));
            agents.add(new Robo(getCell(14, 6)));
            agents.add(new Robo(getCell(16, 0)));
            agents.add(new Robo(getCell(0, 0)));
            encomendas.add(new Encomenda(prateleiras.get(40)));
            encomendas.add(new Encomenda(prateleiras.get(105)));
            encomendas.add(new Encomenda(prateleiras.get(12)));
            encomendas.add(new Encomenda(prateleiras.get(1)));
            encomendas.add(new Encomenda(prateleiras.get(20)));
            encomendas.add(new Encomenda(prateleiras.get(20)));
            encomendas.add(new Encomenda(prateleiras.get(80)));
            encomendas.add(new Encomenda(prateleiras.get(15)));

        }
        aStar = new AStar(numLines, numColumns, agents.get(0).getCell(), saida.getCell());
        runEnviroment();

    }

    private void runEnviroment() {

        aStar.setBlocks(prateleiras);
        aStar.setEncomendas(encomendas);

        GASingleton.getInstance().generateDistanceMatrix(encomendas.size(), encomendas.size());

        List<Cell> agentsCells = new ArrayList<>();
        int allignmentCheck = 0;
        for (Agent a : agents) {
            agentsCells.add(a.getCell());
        }
        for (int i = 0; i < encomendas.size(); i++) {
            List<Integer> agentsDistance = new ArrayList<>();

            for (Agent a : agents) {
                agentsDistance.add(getDistanceToAgent(encomendas.get(i).getCell(), agents.indexOf(a)).size() - 1);
                if((getDistanceToAgent(encomendas.get(i).getCell(), agents.indexOf(a)).size() - 1 ) == 0){
                    allignmentCheck++;
                }
            }/*
            if(allignmentCheck > (encomendas.size()/ 1.2)){
                ALLIGNMENT = 1;
            }*/

            GASingleton.getInstance().addToItems(new Item(encomendas.get(i).getCell().toString(),
                    GASingleton.getInstance().getMatrixCounter(),
                    encomendas.get(i).getCell().getLine(),
                    encomendas.get(i).getCell().getColumn(),
                    agentsCells,
                    agentsDistance,
                    getDistanceToExit(encomendas.get(i).getCell()).size() - 1));
            GASingleton.getInstance().increaseMatrixCounter();
            GASingleton.getInstance().addDistanceToMatrix(i, i, 0);
        }
        for (Agent a : agents) {
            if (agents.indexOf(a) < agents.size() - 1) {
                GASingleton.getInstance().addToItems(new Item(" | ", -1, -1, -1, null, null, -1));
            }
        }
        getDistanciasOtimizada();
    }

    public Environment(int[][] grid, boolean experiment, int seed, int numPicks, int numAgents) {
        GASingleton.getInstance().clearData();
        agents = new LinkedList<Robo>();
        prateleiras = new LinkedList<Prateleira>();
        encomendas = new LinkedList<Encomenda>();

        this.grid = new Cell[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                switch (grid[i][j]) {
                    case 0:// empty
                        this.grid[i][j] = new Cell(i, j);
                        break;
                    case RACK: //prateleira
                        this.grid[i][j] = new Cell(i, j);
                        prateleiras.add(new Prateleira(getCell(i, j)));
                        break;
                    case PRODUCT: // encomenda
                        this.grid[i][j] = new Cell(i, j);
                        Prateleira p = new Prateleira(getCell(i, j));
                        prateleiras.add(p);
                        encomendas.add(new Encomenda(p));
                        break;
                    case DEVICE: // robo
                        this.grid[i][j] = new Cell(i, j);
                        agents.add(new Robo(getCell(i, j)));
                        break;
                    case EXIT: // saida
                        this.grid[i][j] = new Cell(i, j);
                        saida = new Saida(getCell(i, j));
                        break;
                }
            }
        }
        if (experiment) {
            generatePickings(seed, numPicks, numAgents);
        }
        int numL = this.grid.length;
        int numC = this.grid[0].length;
        if(!agents.isEmpty() && saida != null && saida.getCell() != null){
            aStar = new AStar(numL, numC, agents.get(0).getCell(), saida.getCell());
            runEnviroment();
        }
    }


    private List<Cell> getDistanceToAgent(Cell encomenda, int responsibleAgent) {
        aStar.setInitialNode(getCellCloses(agents.get(responsibleAgent).getCell()));
        aStar.setFinalNode(getCellCloses(encomenda));
        List<Cell> path = aStar.findPath();
        return path;
    }

    private List<Cell> getDistanceCells(Cell cell1, Cell cell2) {
        aStar.setInitialNode(getCellCloses(cell1));
        //aStar.setInitialNode(cell1);
        aStar.setFinalNode(getCellCloses(cell2));
        List<Cell> path = aStar.findPath();
        return path;
    }

    private List<Cell> getDistanceToExit(Cell encomenda) {
        aStar.setInitialNode(getCellCloses(encomenda));
        aStar.setFinalNode(getCellCloses(saida.getCell()));
        List<Cell> path = aStar.findPath();
        return path;
    }

    public void getManhattandistance() {
        for (Encomenda a : encomendas) {
            Cell inicial = getCellCloses(a.getCell());
            for (Encomenda ab : encomendas) {
                Cell destino = getCellCloses(ab.getCell());
                int distance = Math.abs(inicial.getLine() - destino.getLine()) + Math.abs(inicial.getColumn() - destino.getColumn());
                System.out.println(a.getCell() + "|" + ab.getCell() + "distance =" + distance);
            }
        }
    }


    public void getDistanciasOtimizada() {
        List<Item> items = GASingleton.getInstance().getItems();
        for (int ii = 0; ii < encomendas.size() - 1; ii++) {


            Cell inicial = getCellCloses(encomendas.get(ii).getCell());
            for (int i = ii + 1; i < encomendas.size(); i++) {
                Cell destino = getCellCloses(encomendas.get(i).getCell());
                aStar.setInitialNode(inicial);
                aStar.setFinalNode(destino);
                List<Cell> caminho = aStar.findPath();

                //agents.add(new Robo(caminho.get(0), caminho));
                // int aux = aStar.findPath2()-1; // -1 pq ele conta a celula (inicial) onde se encontra
//                System.out.println(encomendas.get(ii).getCell().toString() + "|" + encomendas.get(i).getCell().toString() + "distancia=" + (caminho.size() - 1));
                for (Item item : items) {
                    if (item.name.equals(encomendas.get(ii).getCell().toString())) {
                        for (Item item2 : items) {
                            if (item2.name.equals(encomendas.get(i).getCell().toString())) {
                                GASingleton.getInstance().addDistanceToMatrix(item.positionINMATRIX, item2.positionINMATRIX, caminho.size() - 1);
                                GASingleton.getInstance().addDistanceToMatrix(item2.positionINMATRIX, item.positionINMATRIX, caminho.size() - 1);
                            }
                        }
                    }
                }

                //System.out.println(encomendas.get(ii).getCell().toString()+"|"+encomendas.get(i).getCell().toString()+"distancia="+ aux);
                countNumeroIterecoes++;
            }
        }
        //GASingleton.getInstance().printMatrix(null);
    }

    public void getDistancias() {

        for (Encomenda a : encomendas) {
            Cell inicial = getCellCloses(a.getCell());
            for (Encomenda ab : encomendas) {
                Cell destino = getCellCloses(ab.getCell());
                // aStar = new AStar(numLines, numColumns, inicial, destino);
                //aStar.setBlocks(prateleiras);
                aStar.setInitialNode(inicial);
                aStar.setFinalNode(destino);
                int aux = aStar.findPath2() - 1; // -1 pq ele conta a celula inicial onde se encontra
                System.out.println(a.getCell().toString() + "|" + ab.getCell().toString() + "distancia=" + aux);
                countNumeroIterecoes++;

            }
        }
    }

    // estamos a considerar que que as prateleiras têm 2 quadriculas de largura,
    public Cell getCellCloses(Cell prateleira) {
        if (ALLIGNMENT == 0) { //HORIZONTAL
            if (grid[prateleira.getLine()][prateleira.getColumn()].hasPrateleira()) {
                if (prateleira.getLine() - 1 < 0) {
                    return grid[prateleira.getLine() + 1][prateleira.getColumn()];
                }
                if (prateleira.getLine() + 1 >= grid.length) {
                    return grid[prateleira.getLine() - 1][prateleira.getColumn()];
                }
                if (!grid[prateleira.getLine() - 1][prateleira.getColumn()].hasPrateleira()) {
                    return grid[prateleira.getLine() - 1][prateleira.getColumn()];
                }
                return grid[prateleira.getLine() + 1][prateleira.getColumn()];
            }
        }
        if (ALLIGNMENT == 1) { //VERTICAL
            if (grid[prateleira.getLine()][prateleira.getColumn()].hasPrateleira()) {
                if (prateleira.getColumn() - 1 < 0) {
                    return grid[prateleira.getLine()][prateleira.getColumn() + 1];
                }
                if (prateleira.getColumn() + 1 >= grid.length) {
                    return grid[prateleira.getLine()][prateleira.getColumn() - 1];
                }
                if (!grid[prateleira.getLine()][prateleira.getColumn() - 1].hasPrateleira()) {
                    return grid[prateleira.getLine()][prateleira.getColumn() - 1];
                }
                return grid[prateleira.getLine()][prateleira.getColumn() + 1];
            }
        }
        return grid[prateleira.getLine()][prateleira.getColumn()];
    }


    public void run() {
        for (int i = 0; i < numIterations; i++) {
            for (Agent agent : agents) {
                agent.act(this);

                fireUpdatedEnvironment();
            }
        }
    }

    public Cell getNorthCell(Cell cell) {
        if (cell.getLine() > 0) {
            return grid[cell.getLine() - 1][cell.getColumn()];
        }
        return null;
    }

    public Cell getSouthCell(Cell cell) {
        if (cell.getLine() < grid.length - 1) {
            return grid[cell.getLine() + 1][cell.getColumn()];
        }
        return null;
    }

    public Cell getEastCell(Cell cell) {
        if (cell.getColumn() < grid[0].length - 1) {
            return grid[cell.getLine()][cell.getColumn() + 1];
        }
        return null;
    }

    public Cell getWestCell(Cell cell) {
        if (cell.getColumn() > 0) {
            return grid[cell.getLine()][cell.getColumn() - 1];
        }
        return null;
    }

    public int getNumLines() {
        return grid.length;
    }

    public int getNumColumns() {
        return grid[0].length;
    }

    public final Cell getCell(int linha, int coluna) {
        return grid[linha][coluna];
    }

    public Color getCellColor(int linha, int coluna) {
        return grid[linha][coluna].getColor();
    }

    //listeners
    private final ArrayList<EnvironmentListener> listeners = new ArrayList<EnvironmentListener>();

    public synchronized void addEnvironmentListener(EnvironmentListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void fireUpdatedEnvironment() {
        for (EnvironmentListener listener : listeners) {
            listener.environmentUpdated();
        }
    }

    private int createPath(List<Cell> path, int agentIdx) {
        List<Cell> agentPath = new ArrayList<>();
        for (Cell c : path) {
            if (agentPath.isEmpty()) {
                agentPath.addAll(getDistanceToAgent(c, agentIdx));
            } else {
                int auxSize = agentPath.size() - 1;
                /*System.out.println(agentPath.get(auxSize).toString()+"-- First");
                System.out.println(c.toString()+"-- End");*/

                List<Cell> auxCell = getDistanceCells(agentPath.get(auxSize), c);
                auxCell.remove(0);
                agentPath.addAll(auxCell);

            }
        }
        List<Cell> auxPath = getDistanceToExit(agentPath.get(agentPath.size() - 1));
        auxPath.remove(0);
        agentPath.addAll(auxPath);
        //System.out.println(agentPath.size() - 1);
        this.agents.get(agentIdx).addPath(agentPath);
        /*for (Cell c: agentPath){ // ver o caminho
            System.out.print(c.toString());
        }*/
        return agentPath.size() - 1;
    }

    public List<Double> showPath(List<Item> items, boolean show) {
        this.fitnessType = GASingleton.getInstance().getFitnessType();
        for (Agent a : agents) {
            a.clearPath();
        }
        List<Cell> path = new ArrayList<>();
        //System.out.println("Enviroment:");
        List<Double> times = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) { //cell

            if (items.get(i).cell.getColumn() != -1) {
                path.add(items.get(i).getCell());
            } else { //agent
                if (!path.isEmpty()) {
                    int agentIdx = Integer.parseInt(items.get(i).name);
                    agents.get(agentIdx).setNumPicks(path.size());
                    try {
                        times.add((double) createPath(path, agentIdx));
                    }catch (IndexOutOfBoundsException e){
                        swapAllinment();
                        times.add((double) createPath(path, agentIdx));
                    }
                    path = new ArrayList<>();
                }
            }
            if (!path.isEmpty() && i == items.size() - 1) {
                agents.get(Integer.parseInt(GASingleton.getInstance().getMissingAgent(items))).setNumPicks(path.size());
                times.add((double) createPath(path, Integer.parseInt(GASingleton.getInstance().getMissingAgent(items))));
            }
        }


        List<Robo> movingAgents = removeNonMovingAgents();
        Collision c = null;
        Double iteration = 0.0;
        do {
            c = checkCollisions(movingAgents);
            fixCollision(c, iteration.intValue(), show ? 1 : 0);
            iteration++;
            if (iteration > 20) {
                Double highest = 0.0;
                for (Double i : times) {
                    if (i > highest) {
                        highest = i;
                    }
                }
                times.add(times.indexOf(highest), highest + 40);
                times.remove(highest);
                break;
            }
        } while (c != null);

        times.add(iteration - 1);

        //picksPerAgent
        int counter = 0;
        int totalNumPicks = 0;
        for (Robo a : agents) {
            if (a.getNumPicks() > 0) {
                totalNumPicks += a.getNumPicks();
                counter++;
                a.setNumPicks(0);
            }
            a.setNumPicks(0);
        }
        if (totalNumPicks == 0) {
            System.out.println("picks 0");
        }
        double avgNumPicks = totalNumPicks / counter;
        times.add(avgNumPicks);
        if (show) {
            generatePathsMatrix(movingAgents, show);
            run();
        }


        return times;
    }

    private void swapAllinment(){
        if(ALLIGNMENT == 1){
            ALLIGNMENT = 0;
        }
        else{
            ALLIGNMENT = 1;
        }
    }

    private void fixCollision(Collision c, int iteration, int show) {
        if (c == null) {

        } else {
            c.setAgent(chooseAgentByFitnessType(c));
            int lowest = Integer.MAX_VALUE;
            List<Cell> path = c.getAgent().getPath();
            if ((path.indexOf(c.getCell()) - 1 - show) < 0) {
                return;
            }
            Cell beforeCollision = path.get(path.indexOf(c.getCell()) - 1 - show);
            Cell compensate = path.get(c.getAgent().getPath().indexOf(c.getCell()) - show);
            path.add(path.lastIndexOf(c.getCell()) - show, compensate);
            path.add(path.lastIndexOf(c.getCell()) - show, beforeCollision);
            c.getAgent().addPath(path);
        }
    }

    private Agent chooseAgentByFitnessType(Collision c) {
        if (fitnessType == BIGGER_PATH_PRIORITY) {
            int lowest = Integer.MAX_VALUE;
            Agent lowestA = null;
            for (Agent a : c.getAgents()) {
                if (a.getPath().size() < lowest) {
                    lowestA = a;
                    lowest = a.getPath().size();
                }
            }
            return lowestA;
        } else if (fitnessType == SMALLER_PATH_PRIORITY) {
            int highest = Integer.MIN_VALUE;
            Agent highestA = null;
            for (Agent a : c.getAgents()) {
                if (a.getPath().size() > highest) {
                    highestA = a;
                    highest = a.getPath().size();
                }
            }
            return highestA;

        } else if (fitnessType == CLOSEST_TO_EXIT_PRIORITY) {
            int distance_exit = Integer.MAX_VALUE;
            Agent closestAgent = null;
            for (Agent a : c.getAgents()) {
                int distance = a.getPath().size() - a.getPath().lastIndexOf(c.getCell());
                if (distance < distance_exit) {
                    distance_exit = distance;
                    closestAgent = a;
                }
            }
            return closestAgent;
        }
        return null;
    }

    private List<Robo> removeNonMovingAgents() {
        List<Robo> moving = new ArrayList<>();
        for (Robo a : agents) {
            if (a.getPath() != null) {
                moving.add(a);
            }
        }
        return moving;
    }

    private Collision checkCollisions(List<Robo> agents) {
        int highest = 0;
        for (Agent a : agents) {
            if (highest < a.getPath().size()) {
                highest = a.getPath().size();
            }
        }
        String[][] pathsMatrix = generatePathsMatrix(agents, false);
        Collision collision = null;

        for (int i = 0; i < pathsMatrix.length - 1; i++) {
            List<String> commonCells = new ArrayList<>(Arrays.asList(pathsMatrix[i]));
            List<String> nextCommonCells = new ArrayList<>(Arrays.asList(pathsMatrix[i + 1]));
            for (String c : commonCells) {
                List<Agent> collisionAgents = new ArrayList<>();
                if (c != null && commonCells.indexOf(c) != commonCells.lastIndexOf(c) && !c.equals("-----")) {
                    collisionAgents.add(agents.get(commonCells.indexOf(c)));
                    collisionAgents.add(agents.get(commonCells.lastIndexOf(c)));
                    for (Cell celll : agents.get(commonCells.indexOf(c)).getPath()) {
                        if (celll.toString().equals(c)) {
                            collision = new Collision(celll, collisionAgents);
                        }
                    }
                }
                if (nextCommonCells.contains(c) && commonCells.get(nextCommonCells.indexOf(c)).equals(nextCommonCells.get(commonCells.indexOf(c))) && !c.equals("-----")) {
                    collisionAgents.add(agents.get(commonCells.indexOf(c)));
                    collisionAgents.add(agents.get(commonCells.lastIndexOf(c)));
                    for (Cell cell : agents.get(nextCommonCells.indexOf(c)).getPath()) {
                        if (cell.toString().equals(c)) {
                            Cell celll = agents.get(nextCommonCells.indexOf(c)).getPath().get(agents.get(nextCommonCells.indexOf(c)).getPath().indexOf(cell) - 1 <= 0 ? 0 : agents.get(nextCommonCells.indexOf(c)).getPath().indexOf(cell));
                            collision = new Collision(celll, collisionAgents);
                        }
                    }
                }
            }
        }
        return collision;
    }

    private String[][] generatePathsMatrix(List<Robo> agents, boolean show) {

        int highest = 0;
        for (Agent a : agents) {
            if (a.getPath() != null && a.getPath().size() > highest) {
                highest = a.getPath().size();
            }
        }


        String[][] pathsMatrix = new String[highest][agents.size()];
        for (Agent a : agents) {
            for (int i = 0; i < highest; i++) {
                try {
                    pathsMatrix[i][agents.indexOf(a)] = a.getPath() == null ? a.getCell().toString() : a.getPath().get(i).toString();
                } catch (IndexOutOfBoundsException e) {
                    pathsMatrix[i][agents.indexOf(a)] = "-----";
                }
            }
        }

        for (int i = 0; i < pathsMatrix.length; i++) {
            for (int j = 0; j < pathsMatrix[i].length; j++) {
                if (pathsMatrix[i][j] == null) {
                    pathsMatrix[i][j] = "-----";
                }
            }
        }
        if (show)
            GASingleton.getInstance().printMatrix(pathsMatrix);

        return pathsMatrix;
    }

    public void generatePickings(int seed, int numPicks, int numAgents) {
        for (int ii = 0; ii < numPicks; ii++) {
            int random = new Random(seed + ii).nextInt(prateleiras.size());
            encomendas.add(new Encomenda(prateleiras.get(random)));
        }
        for (int i = 0; i < numAgents; i++) {
            agents.add(new Robo(getCell(grid.length - 1, grid[0].length - 1 - i)));
        }
    }

    private class Collision {
        private Cell cell;
        private Integer cellIDX;
        private Agent agent;
        private List<Agent> agents;

        public List<Agent> getAgents() {
            return agents;
        }

        public void setAgents(List<Agent> agents) {
            this.agents = agents;
        }

        public Collision(Cell cell, List<Agent> agents) {
            this.cell = cell;
            this.agents = agents;
        }

        public Cell getCell() {
            return cell;
        }

        public void setCell(Cell cell) {
            this.cell = cell;
        }

        public Integer getCellIDX() {
            return cellIDX;
        }

        public void setCellIDX(Integer cellIDX) {
            this.cellIDX = cellIDX;
        }

        public Agent getAgent() {
            return agent;
        }

        public void setAgent(Agent agent) {
            this.agent = agent;
        }
    }

}

