package ga;

import armazem.Cell;
import armazem.Environment;
import ga.geneticOperators.Mutation;
import ga.geneticOperators.Recombination;
import ga.selectionMethods.SelectionMethod;
import gui.MainFrame;
import gui.SimulationPanel;
import picking.Item;
import statistics.MultipleGaListener;
import utils.Graphs.FitnessNode;
import utils.Graphs.FitnessResults;
import utils.Graphs.GraphNode;

import java.lang.reflect.Array;
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
    private int lastGentListIndex = -1;
    private List<GraphNode> baseGenome;

    public int getPopulationSize() {
        return populationSize;
    }

    public SelectionMethod<I, P> getSelection() {
        return selection;
    }

    public Recombination<I> getRecombination() {
        return recombination;
    }

    public Mutation<I> getMutation() {
        return mutation;
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }

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

    public GeneticAlgorithm(
            int populationSize,
            int maxGenerations,
            SelectionMethod<I, P> selection,
            Mutation<I> mutation,
            Recombination<I> recombination,
            Random rand,
            int agents,
            int picks,
            int num_columns,
            float time_weight,
            int seed) {

        random = rand;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.selection = selection;
        this.mutation = mutation;
        this.recombination = recombination;

        GASingleton.getInstance().setTimeWeight(time_weight);
        GASingleton.getInstance().setColisionWeight(1 - time_weight);
        GASingleton.getInstance().getSimulationPanel().generateExperimentGraph(num_columns, agents, picks, seed);
    }

    public I run(P problem) {
        t = 0;
        //System.out.println("\n" + this.toString());
        if (GASingleton.getInstance().getTaskMap() != null) {
            Item[] problemItems = problem.getNewIndividual().getGenome(-1);
            if (problemItems.length == 0) {
                bestInRun = (I) GASingleton.getInstance().getDefaultBestInRun();
                bestInRun.setResults(new FitnessResults(-1));
                this.lastGentListIndex = GASingleton.getInstance().addLastGenGA(this, lastGentListIndex);
                fireRunEnded(new GAEvent(this));
                return bestInRun;
            }
            baseGenome = new ArrayList<>();
            for (int i = 0; i < problemItems.length; i++) {
                baseGenome.add(problemItems[i].node);
            }

            this.lastGentListIndex = GASingleton.getInstance().addLastGenGA(this, lastGentListIndex);
        }
        population = new Population<>(populationSize, problem);
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

            //System.out.println("GA " + lastGentListIndex + " gen " + this.getGeneration());
            fireGenerationEnded(new GAEvent(this));
        }

        if (GASingleton.getInstance().getCm() != null) {
            GASingleton.getInstance().generateXMLPath(bestInRun.results.getTaskedAgentsFullNodes());
        }

        if (GASingleton.getInstance().isNodeProblem()) {
            GASingleton.getInstance().setBestInRun(bestInRun.results);
            //GASingleton.getInstance().getSimulationPanel().runPath(bestInRun.getResults());
        } else {
            GASingleton.getInstance().setFinalItemSet(Arrays.asList(bestInRun.getGenome(-1)), true);
        }

        fireRunEnded(new GAEvent(this));

        return bestInRun;
    }

    private Population<I, P> placeAgents(Population<I, P> populationAux) {
        List<Integer> agents = new ArrayList<>();
        int in = 0;
        do {
            try {
                for (Cell c : populationAux.getIndividual(0).getGenome(-1)[in].agents) {
                    agents.add(populationAux.getIndividual(0).getGenome(-1)[in].agents.indexOf(c));
                }
            } catch (Exception e) {
                in++;
            }

        } while (agents.size() < 1);


        for (int i = 0; i < populationSize; i++) {
            List<Integer> indexes = new ArrayList<>();
            for (int j = 0; j < populationAux.getIndividual(i).getGenome(-1).length; j++) {
                if (populationAux.getIndividual(i).getGenome(-1)[j].name == " | ") {
                    indexes.add(j);
                }
            }
            //Collections.shuffle(agents);
            for (Integer index : indexes) {
                //populationAux.getIndividual(i).setGene(index, agents.get(indexes.indexOf(index)).toString());
            }
            //System.out.println(populationAux.getIndividual(i).printGenome());
        }
        return populationAux;
    }

    private void mutation(Population<I, P> population) {
        for (int i = 0; i < populationSize; i++) {
            if (GeneticAlgorithm.random.nextDouble() < mutation.getProbability()) {
                mutation.run(population.getIndividual(i));
            }
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
        if (GASingleton.getInstance().getTaskMap() != null)
            GASingleton.getInstance().addLastGenGA(e.source, e.source.lastGentListIndex);
        for (GAListener listener : listeners) {
            listener.generationEnded(e);
        }
        if (e.isStopped()) {
            stop();
        }
    }

    public void fireRunEnded(GAEvent e) {
        if (!listeners.get(0).getClass().equals(MainFrame.class) && !listeners.get(0).getClass().equals(MultipleGaListener.class)) {
            if (GASingleton.getInstance().getTaskMap() != null) {
                GASingleton.getInstance().fixMultipleGAs();
                GASingleton.getInstance().setTaskMap(null);
                GASingleton.getInstance().setMultipleGA(false);
            }
        }
        for (GAListener listener : listeners) {
            listener.runEnded(e);
        }
    }

    public List<GraphNode> getBaseGenome() {
        return baseGenome;
    }

    public void setBaseGenome(List<GraphNode> baseGenome) {
        this.baseGenome = baseGenome;
    }

    public void setBestInRun(I bestInRun) {
        this.bestInRun = bestInRun;
        this.bestInRun.fitness = bestInRun.results.getFitness();
    }
}
