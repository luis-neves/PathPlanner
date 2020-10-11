package statistics;

import experiments.ExperimentEvent;
import experiments.ExperimentListener;
import ga.*;
import ga.geneticOperators.Mutation;
import ga.geneticOperators.Recombination;
import ga.selectionMethods.SelectionMethod;
import gui.MainFrame;
import picking.Item;
import picking.Picking;
import picking.PickingIndividual;
import utils.Graphs.EnvironmentNodeGraph;
import utils.Graphs.FitnessNode;
import utils.Graphs.FitnessResults;
import utils.Graphs.GraphNode;

import java.util.*;

public class MultipleGaListener implements GAListener {

    int ga_iteration;
    int max_agents;
    List<GAListener> statistics;


    private int populationSize;
    private int maxGenerations;
    private SelectionMethod<PickingIndividual, Picking> selection;
    private Recombination<PickingIndividual> recombination;
    private Mutation<PickingIndividual> mutation;
    private List<PickingIndividual> bestIndividuals;

    HashMap<GraphNode, List<FitnessNode>> fullPath;
    HashMap<GraphNode, List<GraphNode>> agentsOnly;

    public MultipleGaListener(int ga_iteration, int max_agents, List<GAListener> statistics, int populationSize, int maxGenerations, SelectionMethod<PickingIndividual, Picking> selection, Recombination<PickingIndividual> recombination, Mutation<PickingIndividual> mutation) {
        this.ga_iteration = ga_iteration;
        this.max_agents = max_agents;
        this.statistics = statistics;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.selection = selection;
        this.recombination = recombination;
        this.mutation = mutation;

        fullPath = new HashMap<>();
        agentsOnly = new HashMap<>();
        bestIndividuals = new ArrayList<>();
    }


    @Override
    public void generationEnded(GAEvent e) {

    }

    @Override
    public void runEnded(GAEvent e) {
        ga_iteration++;
        bestIndividuals.add((PickingIndividual) e.getSource().getBestInRun());
        fullPath.putAll(e.getSource().getBestInRun().getResults().getTaskedAgentsFullNodes());
        agentsOnly.putAll(e.getSource().getBestInRun().getResults().getTaskedAgentsOnly());
        if (ga_iteration - 1 == max_agents) {
            Individual biggest_task_ind = null;
            float fitness = 0;
            float time = 0;

            for (Individual ind : bestIndividuals) {
                if (ind.getFitness() > fitness) {
                    biggest_task_ind = ind;
                    fitness = ind.getResults().getFitness();
                    time = ind.getResults().getTime();
                }
            }

            FitnessResults bestInRun = new FitnessResults();
            bestInRun.setTaskedAgentsFullNodes(fullPath);
            bestInRun.setTaskedAgentsOnly(agentsOnly);
            bestInRun.setTaskedAgentsFullNodesNoPackages(fullPath);
            bestInRun.setTime(time);
            bestInRun.setFitness(fitness);
            EnvironmentNodeGraph.checkColisions2(bestInRun);
            biggest_task_ind.setResults(bestInRun);

            GeneticAlgorithm ga = e.getSource();
            ga.setBestInRun(biggest_task_ind);
            e.setSource(ga);

            GASingleton.getInstance().fixMultipleGAs();
            GASingleton.getInstance().setTaskMap(null);
            GASingleton.getInstance().setMultipleGA(false);

            for (GAListener listener : statistics) {
                listener.runEnded(e);
            }
            return;
        }
        int i = 1;
        for (Map.Entry<GraphNode, List<GraphNode>> entry : GASingleton.getInstance().getTaskMap().entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> agentTask = entry.getValue();
            List<Item> items = new ArrayList<>();
            if (i == ga_iteration) {
                GeneticAlgorithm<PickingIndividual, Picking> myGA = new GeneticAlgorithm<PickingIndividual, Picking>(
                        populationSize,
                        maxGenerations,
                        selection,
                        mutation,
                        recombination,
                        new Random(GASingleton.getInstance().getSeed()));
                myGA.addGAListener(MultipleGaListener.this);
                for (GraphNode node : agentTask) {
                    items.add(new Item(node));
                }
                myGA.run(new Picking(items));

                return;
            }
            i++;
        }
    }

    @Override
    public void experimentEnded(ExperimentEvent event) {

    }
}
