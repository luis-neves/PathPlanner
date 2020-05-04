package statistics;

import experiments.Experiment;
import experiments.ExperimentEvent;
import ga.GAEvent;
import ga.GAListener;
import ga.GeneticAlgorithm;
import ga.Individual;
import ga.Problem;
import utils.Maths;

import java.io.File;

public class StatisticBestAverage<E extends Individual, P extends Problem<E>> implements GAListener  {

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
        if(!file.exists()){
            utils.FileOperations.appendToTextFile("statistic_average_fitness.xls", experimentHeader + "\t" + "Average:" + "\t" + "StdDev:" + "\t" +"Average Collisions"+ "\t" + "StdDev Collisions"+ "\t" + "Avg PicksPerAgent"+ "\t" +"StdDev PicksPerAgent"+"\r\n");
        }
    }

    @Override
    public void generationEnded(GAEvent e) {
    }

    @Override
    public void runEnded(GAEvent e) {
        GeneticAlgorithm<E, P> ga = e.getSource();
        values[run] = ga.getBestInRun().getFitness();
        collisionValues[run] = (double) ga.getBestInRun().getCollisions();
        picksAgentValues[run] = ga.getBestInRun().getPicksPerAgent();
        run++;
    }

    @Override
    public void experimentEnded(ExperimentEvent e) {

        double average = Maths.average(values);
        double averageCollisions = Maths.average(collisionValues);
        double avaragePicksPerAgent = Maths.average(picksAgentValues);
        double sd = Maths.standardDeviation(values, average);
        double sdCollisions = Maths.standardDeviation(collisionValues, averageCollisions);
        double sdPicksPerAgent = Maths.standardDeviation(picksAgentValues, avaragePicksPerAgent);

        String experimentConfigurationValues = ((Experiment) e.getSource()).getExperimentValues();

        utils.FileOperations.appendToTextFile("statistic_average_fitness.xls", experimentConfigurationValues + "\t" + average + "\t" + sd + "\t" + averageCollisions + "\t" + sdCollisions +"\t" + avaragePicksPerAgent +"\t" + sdPicksPerAgent + "\r\n");
    }
}
