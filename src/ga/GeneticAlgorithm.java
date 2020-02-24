package ga;

import armazem.Cell;
import armazem.Environment;
import ga.geneticOperators.Mutation;
import ga.geneticOperators.Recombination;
import ga.selectionMethods.SelectionMethod;
import gui.SimulationPanel;
import utils.Graphs.GraphNode;

import java.util.*;

public class GeneticAlgorithm<I extends Individual, P extends Problem<I>> {

    public static Random random;
    private final int populationSize;
    private final int maxGenerations;
    private final SelectionMethod<I, P> selection;
    private final Recombination<I> recombination;
    private final Mutation<I> mutation;
    private int t;
    private Population<I, P> population;
    private boolean stopped;
    private I bestInRun;

    public GeneticAlgorithm(
            int populationSize,
            int maxGenerations,
            SelectionMethod<I, P> selection,
            Mutation<I> mutation,
            Recombination<I> recombination,
            Random rand) {

        random = rand;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.selection = selection;
        this.mutation = mutation;
        this.recombination = recombination;
    }

    public GeneticAlgorithm(
            int populationSize,
            int maxGenerations,
            SelectionMethod<I, P> selection,
            Mutation<I> mutation,
            Recombination<I> recombination,
            Random rand,
            int agents,
            int picks,
            int[][] grid,
            int seed) {

        random = rand;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.selection = selection;
        this.mutation = mutation;
        this.recombination = recombination;
        SimulationPanel.environment = new Environment(grid, true, seed, picks, agents);
    }

    public I run(P problem) {
        t = 0;
        population = new Population<>(populationSize, problem);
        if (GASingleton.getInstance().isNodeProblem()) {

        } else {
            population = placeAgents(population);
        }

        bestInRun = population.evaluate();

        fireGenerationEnded(new GAEvent(this));

        while (!stopCondition(t)) {
            Population<I, P> populationAux = selection.run(population);
            recombination(populationAux);
            mutation(populationAux);
            population = generateNewPopulation(population, populationAux);
            I bestInGen = population.evaluate();
            if (bestInGen.compareTo(bestInRun) > 0) {
                bestInRun = (I) bestInGen.clone();
            }
            t++;
            fireGenerationEnded(new GAEvent(this));
        }

        if (GASingleton.getInstance().isNodeProblem()){
            GASingleton.getInstance().getSimulationPanel().runPath(bestInRun.getResults());
            //GASingleton.getInstance().generateXMLPath(bestInRun.getTaskedAgents());
        }
        else {
            GASingleton.getInstance().setFinalItemSet(Arrays.asList(bestInRun.getGenome()), true);
        }
        fireRunEnded(new GAEvent(this));

        return bestInRun;
    }

    private Population<I, P> placeAgents(Population<I, P> populationAux) {
        List<Integer> agents = new ArrayList<>();
        int in = 0;
        do {
            try {
                for (Cell c : populationAux.getIndividual(0).getGenome()[in].agents) {
                    agents.add(populationAux.getIndividual(0).getGenome()[in].agents.indexOf(c));
                }
            } catch (Exception e) {
                in++;
            }

        } while (agents.size() < 1);


        for (int i = 0; i < populationSize; i++) {
            List<Integer> indexes = new ArrayList<>();
            for (int j = 0; j < populationAux.getIndividual(i).getGenome().length; j++) {
                if (populationAux.getIndividual(i).getGenome()[j].name == " | ") {
                    indexes.add(j);
                }
            }
            //Collections.shuffle(agents);
            for (Integer index : indexes) {
                populationAux.getIndividual(i).setGene(index, agents.get(indexes.indexOf(index)).toString());
            }
            //System.out.println(populationAux.getIndividual(i).printGenome());
        }
        return populationAux;
    }

    private void mutation(Population<I, P> population) {
        for (int i = 0; i < populationSize; i++) {
            mutation.run(population.getIndividual(i));
        }
    }

    private boolean stopCondition(int t) {
        return stopped || t == maxGenerations;
    }

    private void recombination(Population<I, P> population) {
        for (int i = 0; i < populationSize; i += 2) {
            if (random.nextDouble() < recombination.getProbability()) {
                recombination.run(population.getIndividual(i), population.getIndividual(i + 1));
            }
        }
    }


    public Population generateNewPopulation(Population<I, P> presentPopulation, Population<I, P> nextPopulation) {
        return nextPopulation;
    }

    public int getGeneration() {
        return t;
    }

    public I getBestInGeneration() {
        return population.getBest();
    }

    public double getAverageFitness() {
        return population.getAverageFitness();
    }

    public I getBestInRun() {
        return bestInRun;
    }

    public void stop() {
        stopped = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Population size:" + populationSize + "\n");
        sb.append("Max generations:" + maxGenerations + "\n");
        sb.append("Selection:" + selection + "\n");
        sb.append("Recombination:" + recombination + "\n");
        sb.append("Mutation:" + mutation + "\n");
        return sb.toString();
    }

    //Listeners
    private final transient List<GAListener> listeners = new ArrayList<>(3);

    public synchronized void removeAGListener(GAListener listener) {
        if (listeners != null && listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public synchronized void addGAListener(GAListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void fireGenerationEnded(GAEvent e) {
        for (GAListener listener : listeners) {
            listener.generationEnded(e);
        }
        if (e.isStopped()) {
            stop();
        }
    }

    public void fireRunEnded(GAEvent e) {
        for (GAListener listener : listeners) {
            listener.runEnded(e);
        }
    }
}
