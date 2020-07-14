package ga.KMeans;

import armazem.AStar;
import utils.Graphs.Graph;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.Instance;

public class AstarDistance extends EuclideanDistance {

    private Graph probemGraph;

    public AstarDistance() {
    }

    public AstarDistance(Graph problemGraph) {
        this.probemGraph = problemGraph;
    }

    @Override
    public double distance(Instance first, Instance second) {
        AStar aStar = new AStar(probemGraph);

        DenseInstance val1 =(DenseInstance) first;
        Attribute attr = val1.attribute(0);
        //probemGraph.findNode();
        return super.distance(first, second);
    }
}
