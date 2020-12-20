package picking;

import armazem.Environment;
import ga.GASingleton;
import ga.Problem;
import gui.SimulationPanel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Picking implements Problem<PickingIndividual> {

    private List<Item> items;

    public Picking(List<Item> items) {
        if (items == null) {
            throw new IllegalArgumentException();
        }

        this.items = items;
    }

    public static Picking buildKnapsackFromMemory() {
        return new Picking(GASingleton.getInstance().getItems());
    }

    @Override
    public PickingIndividual getNewIndividual() {
        return new PickingIndividual(this, items);
    }

    public int getNumItems() {
        return items.size();
    }

    public Item getItem(int index) {
        return (index >= 0 && index < items.size()) ? items.get(index) : null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# of itens: ");
        sb.append(items.size());
        sb.append("\n");
        sb.append("\n");
        sb.append("Items:");
        sb.append("\nId\tWeight\tValue");
        for (Item item : items) {
            sb.append(item);
        }
        return sb.toString();
    }

    public static Picking buildFromFile(File file) throws IOException {
        java.util.Scanner f = new java.util.Scanner(file);
        String filecontent = "";
        List<Item> items = new ArrayList<>();
        while (f.hasNextLine()) {
            filecontent += f.nextLine();
        }
        String[] itemsArrayUF = filecontent.split(",");
        int size = Integer.parseInt(itemsArrayUF[0]);
        int seed = Integer.parseInt(itemsArrayUF[1]);
        int agents = Integer.parseInt(itemsArrayUF[2]);
        int packages = Integer.parseInt(itemsArrayUF[3]);
        SimulationPanel.environment = new Environment(size, size, seed, agents, packages);
        return new Picking(GASingleton.getInstance().getItems());
    }

    public static Picking buildExperiment(int num_columns, int agents, int picks, int seed) {
        GASingleton.getInstance().getSimulationPanel().generateExperimentGraph(num_columns, agents, picks, seed);
        return new Picking(GASingleton.getInstance().getItems());
    }


}
