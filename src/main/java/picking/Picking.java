package picking;

import armazem.Environment;
import ga.GASingleton;
import ga.Problem;
import gui.SimulationPanel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Picking implements Problem<PickingIndividual> {

    public static final int SIMPLE_FITNESS = 0;
    public static final int PENALTY_FITNESS = 1;
    private List<Item> items;
    private double prob1s;
    private int fitnessType = SIMPLE_FITNESS;
    private double maxVP;

    public Picking(List<Item> items) {
        if (items == null) {
            throw new IllegalArgumentException();
        }
        this.items = items;
        this.prob1s = prob1s;
        maxVP = computeMaxVP();
    }

    public static Picking buildKnapsackFromMemory() {
        return new Picking(GASingleton.getInstance().getItems());
    }

    @Override
    public PickingIndividual getNewIndividual() {
        return new PickingIndividual(this, items, prob1s);
    }

    public int getNumItems() {
        return items.size();
    }

    public Item getItem(int index) {
        return (index >= 0 && index < items.size()) ? items.get(index) : null;
    }


    public double getProb1s() {
        return prob1s;
    }

    public void setProb1s(double prob1s) {
        this.prob1s = prob1s;
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
        sb.append("\nId\tWeight\tValue");
        for (Item item : items) {
            sb.append(item);
        }
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

    public static Picking buildKnapsack(File file) throws IOException {
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

    public static Picking buildKnapsackExperiment() {

        return new Picking(GASingleton.getInstance().getItems());
    }


}
