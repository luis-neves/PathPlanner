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

    private HashMap<GraphNode, List<GraphNode>> items;
    public HybridClusterPicking(HashMap<GraphNode, List<GraphNode>> items) {
        if (items == null) {
            throw new IllegalArgumentException();
        }
        this.items = items;
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



}
