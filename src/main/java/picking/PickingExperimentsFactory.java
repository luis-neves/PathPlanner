package picking;

import clustering.Clustering;
import experiments.*;
import ga.GAListener;
import ga.GASingleton;
import ga.GeneticAlgorithm;
import ga.geneticOperators.*;
import ga.selectionMethods.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import statistics.MultipleGaListener;
import statistics.StatisticBestAverage;
import statistics.StatisticBestInRun;

public class PickingExperimentsFactory extends ExperimentsFactory {

    private int populationSize;
    public int maxGenerations;
    public int numAgents;
    private int numPicks;
    private SelectionMethod<PickingIndividual, Picking> selection;
    private Recombination<PickingIndividual> recombination;
    private Mutation<PickingIndividual> mutation;

    private Picking picking;

    private Experiment<PickingExperimentsFactory, Picking> experiment;
    private int num_columns;
    private float time_weight;
    private float col_weight;

    public PickingExperimentsFactory(File configFile) throws IOException {
        super(configFile);
    }

    @Override
    public Experiment buildExperiment() throws IOException {
        numRuns = Integer.parseInt(getParameterValue("Runs"));
        populationSize = Integer.parseInt(getParameterValue("Population size"));
        maxGenerations = Integer.parseInt(getParameterValue("Max generations"));
        numAgents = Integer.parseInt(getParameterValue("Num Agents"));
        numPicks = Integer.parseInt(getParameterValue("Num Picks"));
        num_columns = Integer.parseInt(getParameterValue("Num Columns"));
        time_weight = Float.parseFloat(getParameterValue("Time Weight"));
        heuristic = (getParameterValue("Heuristic"));
        col_weight = 1 - time_weight;


        //SELECTION
        switch (getParameterValue("Selection")) {
            case "tournament":
                int tournamentSize = Integer.parseInt(getParameterValue("Tournament size"));
                selection = new Tournament<>(populationSize, tournamentSize);
                break;
            case "roulette wheel":
                selection = new RouletteWheel<>(populationSize);
            case "rank":
                selection = new Ranking<>(populationSize);
        }

        //RECOMBINATION
        double recombinationProbability = Double.parseDouble(getParameterValue("Recombination probability"));
        switch (getParameterValue("Recombination")) {
            case "one_cut":
                recombination = new RecombinationOneCut<>(recombinationProbability);
                break;
            case "PMX":
                recombination = new RecombinationPMX<>(recombinationProbability);
                break;
            case "CX":
                recombination = new RecombinationCX<>(recombinationProbability);
                break;
            case "OX":
                recombination = new RecombinationOX<>(recombinationProbability);
                break;
        }
        //MUTATION
        double mutationProbability = Double.parseDouble(getParameterValue("Mutation probability"));
        switch (getParameterValue("Mutation")) {
            case "inversion":
                mutation = new MutationInversion<>(mutationProbability);
                break;
            case "random":
                mutation = new MutationFirstLast<>(mutationProbability);
            case "swap":
                mutation = new MutationSwap<>(mutationProbability);
        }

        /*if (getParameterValue("Mutation").equals("binary")) {
            mutation = new MutationBinary<>(mutationProbability);
        }*/

        //FITNESS TYPE
        //int fitnessType = Integer.parseInt(getParameterValue("Fitness type"));


        //PROBLEM 
        picking = Picking.buildExperiment(num_columns, numAgents, numPicks, this.numRuns);
        //picking.setFitnessType(fitnessType);

        String experimentTextualRepresentation = buildExperimentTextualRepresentation();
        String experimentHeader = buildExperimentHeader();
        String experimentConfigurationValues = buildExperimentValues();

        experiment = new Experiment<>(
                this,
                numRuns,
                picking,
                experimentTextualRepresentation,
                experimentHeader,
                experimentConfigurationValues);

        statistics = new ArrayList<>();

        for (String statisticName : statisticsNames) {
            ExperimentListener statistic = buildStatistic(statisticName, experimentHeader);
            statistics.add(statistic);
            experiment.addExperimentListener(statistic);
        }

        return experiment;
    }

    @Override
    public GeneticAlgorithm generateGAInstance(int seed) {
        GeneticAlgorithm<PickingIndividual, Picking> ga;

        ga = new GeneticAlgorithm<>(
                populationSize,
                maxGenerations,
                selection,
                mutation,
                recombination,
                new Random(seed),
                numAgents,
                numPicks,
                num_columns,
                time_weight,
                seed);

        GASingleton.getInstance().setSeed(seed);
        for (ExperimentListener statistic : statistics) {
            ga.addGAListener((GAListener) statistic);
        }
        return ga;
    }

    @Override
    public GeneticAlgorithm generateGAxNInstance(int seed) {
        GeneticAlgorithm<PickingIndividual, Picking> ga;

        ga = new GeneticAlgorithm<>(
                populationSize,
                maxGenerations,
                selection,
                mutation,
                recombination,
                new Random(seed),
                numAgents,
                numPicks,
                num_columns,
                time_weight,
                seed);

        List<GAListener> listeners = new ArrayList<>();
        for (ExperimentListener ex : statistics) {
            listeners.add((GAListener) ex);
        }
        MultipleGaListener listener = new MultipleGaListener(1, numAgents, listeners, populationSize, maxGenerations, selection, recombination, mutation);
        ga.addGAListener(listener);

        return ga;
    }

    @Override
    public GeneticAlgorithm generateHybridGAInstance(int seed) {
        GeneticAlgorithm hybridGA = new GeneticAlgorithm<HybridPickingIndividual, HybridClusterPicking>(
                populationSize,
                maxGenerations * numAgents,
                selection.makeHybrid(),
                mutation.makeHybrid(),
                recombination.makeHybrid(),
                new Random(seed));

        for (ExperimentListener statistic : statistics) {
            hybridGA.addGAListener((GAListener) statistic);
        }
        return hybridGA;
    }

    public Clustering generateCLInstance(int seed) {
        Clustering cl = new Clustering(seed);
        for (ExperimentListener statistic : statistics) {
            cl.addGAListener((GAListener) statistic);
        }
        return cl;
    }

    private ExperimentListener buildStatistic(
            String statisticName,
            String experimentHeader) {
        if (statisticName.equals("BestIndividual")) {
            return new StatisticBestInRun(experimentHeader);
        }
        if (statisticName.equals("BestAverage")) {
            return new StatisticBestAverage(numRuns, experimentHeader);
        }
        return null;
    }

    private String buildExperimentTextualRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("Population size:" + populationSize + "\r\n");
        sb.append("Max generations:" + maxGenerations + "\r\n");
        sb.append("Selection:" + selection + "\r\n");
        sb.append("Recombination:" + recombination + "\r\n");
        sb.append("Recombination prob.: " + recombination.getProbability() + "\r\n");
        sb.append("Mutation:" + mutation + "\r\n");
        sb.append("Mutation prob.: " + mutation.getProbability());
        return sb.toString();
    }

    private String buildExperimentHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("Population size:" + "\t");
        sb.append("Max generations:" + "\t");
        sb.append("Selection:" + "\t");
        sb.append("Recombination:" + "\t");
        sb.append("Recombination prob.:" + "\t");
        sb.append("Mutation:" + "\t");
        sb.append("Mutation prob.:" + "\t");
        sb.append("Agents:" + "\t");
        sb.append("Picks:" + "\t");
        sb.append("Columns:" + "\t");
        sb.append("TW:" + "\t");
        sb.append("CW:" + "\t");
        sb.append("Heuristic:" + "\t");
        return sb.toString();
    }

    private String buildExperimentValues() {
        StringBuilder sb = new StringBuilder();
        sb.append(populationSize + "\t");
        sb.append(maxGenerations + "\t");
        sb.append(selection + "\t");
        sb.append(recombination + "\t");
        sb.append(recombination.getProbability() + "\t");
        sb.append(mutation + "\t");
        sb.append(mutation.getProbability() + "\t");
        sb.append(numAgents + "\t");
        sb.append(numPicks + "\t");
        sb.append(num_columns + "\t");
        sb.append(time_weight + "\t");
        sb.append(col_weight + "\t");
        sb.append(heuristic + "\t");
        return sb.toString();
    }
}
