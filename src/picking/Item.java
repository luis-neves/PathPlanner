package picking;

import armazem.Cell;
import utils.Graphs.GraphNode;

import java.util.List;

public class Item {

    public String name;
    public int positionINMATRIX;
    public int exit;
    public List<Cell> agents;
    public List<Integer> agentDistance;
    public Cell cell;
    public GraphNode node;

    public void setPosition(Cell cell) {
        this.cell = cell;
    }

    public int getLine() {
        return this.cell.getLine();
    }

    public int getColumn() {
        return this.cell.getColumn();
    }

    public Item(String name, List<String> items, List<Float> distance, Float agentDistance, Float exitDistance) {
        this.name = name;
    }
    public Item(String name, GraphNode node){
        this.name = name;
        this.node = node;
    }

    public Item(String name, int positionINMATRIX, int line, int column, List<Cell> agent, List<Integer> agentDistance, int exit) {
        this.name = name;
        this.positionINMATRIX = positionINMATRIX;
        this.cell = new Cell(line,column);
        this.agents = agent;
        this.agentDistance = agentDistance;
        this.exit = exit;
    }

    public Integer getLowestAgentDistance(boolean index) {
        Integer lowest = Integer.MAX_VALUE;
        Integer ind = -1;
        if (positionINMATRIX == -1) {
            return -1;
        } else {
            for (Integer i : agentDistance) {
                if (i < lowest) {
                    lowest = i;
                    ind = agentDistance.indexOf(i);
                }
            }
            if (index == false) {
                return lowest;

            } else {
                return ind;
            }
        }
    }

    @Override
    public String toString() {
        return "\n" + name + "\t";
    }

    public void setName(String value) {
        this.name = value;
    }

    public Cell getCell() {
        return cell;
    }
}
