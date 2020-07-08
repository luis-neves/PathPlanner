package picking;

import armazem.Environment;
import ga.GASingleton;
import ga.Problem;
import gui.SimulationPanel;
import utils.Graphs.GraphNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HybridClusterPicking implements Problem<HybridPickingIndividual> {

    public static final int SIMPLE_FITNESS = 0;
    public static final int PENALTY_FITNESS = 1;
    private HashMap<GraphNode, List<GraphNode>> items;
    private int fitnessType = SIMPLE_FITNESS;
    private double maxVP;

    public HybridClusterPicking(HashMap<GraphNode, List<GraphNode>> items) {
        if (items == null) {
            throw new IllegalArgumentException();
        }
        this.items = items;
        maxVP = computeMaxVP();
    }
    /*
    public static HybridClusterPicking buildKnapsackFromMemory() {
        return new HybridClusterPicking(GASingleton.getInstance().getItems());
    }*/

    @Override
    public HybridPickingIndividual getNewIndividual() {
        return new HybridPickingIndividual(this, items);
    }

    public int getNumItems() {
        return items.size();
    }

    public Item getItem(int index) {
        return null;
    }


    public int getFitnessType() {
        return fitnessType;
    }

    public void setFitnessType(int fitnessType) {
        this.fitnessType = fitnessType;
    }

    public double getMaxVP() {
        return maxVP;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# of itens: ");
        sb.append(items.size());
        sb.append("\n");
        sb.append("\n");
        sb.append("Items:");
        /*
        sb.append("\nId\tWeight\tValue");
        for (Item item : items) {
            sb.append(item);
        */
        return sb.toString();
    }

    private double computeMaxVP() {
        /*double max = items[0].value / items[0].weight;
        for (int i = 1; i < items.length; i++) {
            double divVP = items[i].value / items[i].weight;
            if (divVP > max) {
                max = divVP;
            }
        }*/
        return 0;
    }


    public static HybridClusterPicking buildKnapsackExperiment() {
        return new HybridClusterPicking(GASingleton.getInstance().getItemMap());
    }


}
