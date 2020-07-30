package ga.KMeans;

import armazem.AStar;
import ga.GASingleton;
import utils.Graphs.Graph;
import utils.Graphs.GraphNode;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.neighboursearch.PerformanceStats;

import java.util.List;

public class AstarDistance extends EuclideanDistance {


    public AstarDistance() {
    }


    @Override
    public double distance(Instance first, Instance second) {
        AStar aStar = new AStar(GASingleton.getInstance().getProblemGraph());
        Double x = first.value(0);
        Double y = first.value(1);
        GraphNode firstNode = GASingleton.getInstance().getProblemGraph().findClosestNode(x.floatValue(), y.floatValue());
        x = second.value(0);
        y = second.value(1);
        GraphNode secondNode = GASingleton.getInstance().getProblemGraph().findClosestNode(x.floatValue(), y.floatValue());
        aStar.setInitialGraphNode(firstNode);
        aStar.setFinalGraphNode(secondNode);
        double sum = 0;
        List<GraphNode> path = aStar.findGraphPath(GASingleton.getInstance().getProblemGraph().getProducts());
        for (int i = 0; i < path.size() - 1; i++) {
            if (i == 0) {
                sum += path.get(i).getDistance((float) first.value(0), (float) first.value(1));
            }
            if (i == path.size() - 2) {
                sum += path.get(i).getDistance(path.get(i + 1));
                sum += path.get(i + 1).getDistance((float) second.value(0), (float) second.value(1));
            } else {
                sum += path.get(i).getDistance(path.get(i + 1));
            }
        }
        //probemGraph.findNode();
        return sum;
    }

    @Override
    public double distance(Instance first, Instance second, PerformanceStats stats) {
        return this.distance(first, second);
    }
}
