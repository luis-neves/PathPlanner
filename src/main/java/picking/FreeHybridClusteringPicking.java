package picking;

import ga.Problem;
import utils.Graphs.GraphNode;

import java.util.HashMap;
import java.util.List;

public class FreeHybridClusteringPicking extends Picking {

    private HashMap<GraphNode, List<GraphNode>> map;

    public FreeHybridClusteringPicking(HashMap<GraphNode, List<GraphNode>> map) {
        super();
        this.map = map;
    }


    @Override
    public PickingIndividual getNewIndividual() {
        return new PickingIndividual(this, map);
    }
}
