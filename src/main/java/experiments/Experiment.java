package experiments;

import armazem.Environment;
import clustering.Clustering;
import ga.GASingleton;
import ga.GeneticAlgorithm;
import ga.Problem;
import gui.SimulationPanel;
import picking.HybridClusterPicking;
import picking.HybridPickingIndividual;
import weka.core.pmml.jaxbbindings.Cluster;

import java.util.ArrayList;
import java.util.List;

public class Experiment<E extends ExperimentsFactory, P extends Problem> {

    private final E factory;
    private final int numRuns;
    private GeneticAlgorithm ga;
    private Clustering cl;
    private GeneticAlgorithm<HybridPickingIndividual, HybridClusterPicking> hybridGA;

    private final P problem;
    private final String experimentTextualRepresentation;
    private final String experimentHeader;
    private final String experimentValues;

    public Experiment(
            E factory,
            int numRuns,
            P problem,
            String experimentTextualRepresentation,
            String experimentHeader,
            String experimentValues) {
        this.factory = factory;
        this.numRuns = numRuns;
        this.problem = problem;
        this.experimentTextualRepresentation = experimentTextualRepresentation;
        this.experimentHeader = experimentHeader;
        this.experimentValues = experimentValues;
    }

    public void run() throws Exception {
        for (int run = 0; run < numRuns; run++) {
            ga = factory.generateGAInstance(run + 1);
            if (factory.heuristic.equals("K-Means")) {
                GASingleton.getInstance().setDefaultGA(ga);
                GASingleton.getInstance().setDefaultBestInRun(problem.getNewIndividual());
                cl = factory.generateCLInstance(run + 1);
                cl.run(problem);
            }else if(factory.heuristic.equals("Hybrid")){
                cl = factory.generateCLInstance(run + 1);
                hybridGA = factory.generateHybridGAInstance(run+1);
                hybridGA.run(new HybridClusterPicking(cl.generateClusters(run+1)));
            }
            else if (factory.heuristic.equals("GA")) {
                ga = factory.generateGAInstance(run + 1);
                ga.run(problem);
            } else {
                //ga.run(problem);
            }
        }
        GASingleton.getInstance().clearData();
        try {
            fireExperimentEnded();
        } catch (
                NullPointerException e) {
            e.printStackTrace();
        }

    }

    public String getExperimentTextualRepresentation() {
        return experimentTextualRepresentation;
    }

    public String getExperimentHeader() {
        return experimentHeader;
    }

    public String getExperimentValues() {
        return experimentValues;
    }

    //listeners
    final private List<ExperimentListener> listeners = new ArrayList<>(10);

    public synchronized void addExperimentListener(ExperimentListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void fireExperimentEnded() {
        for (ExperimentListener listener : listeners) {
            listener.experimentEnded(new ExperimentEvent(this));
        }
    }

    @Override
    public String toString() {
        return experimentTextualRepresentation;
    }
}
