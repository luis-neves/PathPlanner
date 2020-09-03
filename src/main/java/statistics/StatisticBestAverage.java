package statistics;

import experiments.Experiment;
import experiments.ExperimentEvent;
import ga.GAEvent;
import ga.GAListener;
import ga.GeneticAlgorithm;
import ga.Individual;
import ga.Problem;
import picking.PickingIndividual;
import utils.Maths;

import java.io.File;

public class StatisticBestAverage<E extends Individual, P extends Problem<E>> implements GAListener {

    private final double[] values;
    private final double[] times;
    private final double[] collisionValues;
    private final double[] picksAgentValues;
    private int run;

    public StatisticBestAverage(int numRuns, String experimentHeader) {
        values = new double[numRuns];
        times = new double[numRuns];
        collisionValues = new double[numRuns];
        picksAgentValues = new double[numRuns];
        File file = new File("statistic_average_fitness.xls");
        if (!file.exists()) {
            utils.FileOperations.appendToTextFile("statistic_average_fitness.xls", experimentHeader + "\t" + "Fitness(AVG):" + "\t" + "Time(AVG):" + "\t" + "Fitness StdDev:" + "\t" + "Time StdDev" + "\t" + "Collisions (AVG)" + "\t" + "Collisions StdDev" + "\t" + "stdDevPicksPerAgent (AVG)" + "\t" + "\r\n");
        }
    }

    @Override
    public void generationEnded(GAEvent e) {
    }

    @Override
    public void runEnded(GAEvent e) {
        GeneticAlgorithm<E, P> ga = e.getSource();
        values[run] = ga.getBestInRun().getFitness();
        times[run] = ga.getBestInRun().getResults().getTime();
        collisionValues[run] = (double) ga.getBestInRun().getResults().getNumCollisions();
        picksAgentValues[run] = (ga.getBestInRun()).pickDistributionStdDev();
        run++;
    }

    @Override
    public void experimentEnded(ExperimentEvent e) {

        double average = Maths.average(values);
        double averageTime = Maths.average(times);
        double averageCollisions = Maths.average(collisionValues);
        double avaragePicksPerAgent = Maths.average(picksAgentValues);
        double sd = Maths.standardDeviation(values, average);
        double sdTime = Maths.standardDeviation(times, averageTime);
        double sdCollisions = Maths.standardDeviation(collisionValues, averageCollisions);

        String experimentConfigurationValues = ((Experiment) e.getSource()).getExperimentValues();

        utils.FileOperations.appendToTextFile("statistic_average_fitness.xls", experimentConfigurationValues + "\t" + average + "\t" + averageTime + "\t" + sd + "\t" + sdTime + "\t" + averageCollisions + "\t" + sdCollisions + "\t" + avaragePicksPerAgent + "\r\n");
    }
}
