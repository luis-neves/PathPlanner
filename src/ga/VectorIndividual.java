package ga;

import picking.Item;
import utils.Graphs.Graph;
import utils.Graphs.GraphNode;

import java.util.List;
import java.util.Map;

public abstract class VectorIndividual<P extends Problem, I extends VectorIndividual> extends Individual<P, I> {

    protected Item[] genome;

    public VectorIndividual(P problem, List<Item> items, double prob1s) {
        super(problem);
        genome = GASingleton.getInstance().getItems().toArray(new Item[GASingleton.getInstance().getItems().size()]);
    }

    public VectorIndividual(VectorIndividual<P, I> original) {
        super(original);
        this.genome = new Item[original.genome.length];
        System.arraycopy(original.genome, 0, genome, 0, genome.length);
    }

    @Override
    public int getNumGenes() {
        return genome.length;
    }

    @Override
    public Item getGene(int index) {
        return genome[index];
    }

    private String printTaskedAgents() {
        String str = "";
        for (Map.Entry<GraphNode, List<GraphNode>> entry : getTaskedAgents().entrySet()) {
            GraphNode agent = entry.getKey();
            List<GraphNode> agentPath = entry.getValue();
            List<Float> costs = getTaskedAgentsCosts().get(agent);
            List<Float> costsTime = getTaskedAgentsCostsTime().get(agent);
            str += "\nAgent " + agent.getType().toLetter() + agent.getGraphNodeId() + "\n\t";

            for (int i = 0; i < costs.size(); i++) {
                str += "["+costs.get(i).toString() + "]";
            }
            str+= "Size: "+ costs.size() + "\n\t";
            for (int i = 0; i < costsTime.size(); i++) {
                str += " " + costsTime.get(i).toString()+ " ";
            }
            str+= "\n\t";
            for (int i = 0; i < agentPath.size(); i++) {
                str += "[" + agentPath.get(i).getType().toLetter() + agentPath.get(i).getGraphNodeId() + "]";
            }
            str+= "Size: "+ agentPath.size();

            if (agentPath.isEmpty()) {
                str += "Empty Path";
            }
        }
        return str;
    }

    @Override
    public String printGenome() {
        if (GASingleton.getInstance().isNodeProblem()) {
            String str = "";
            for (int i = 0; i < getGenome().length; i++) {
                str += "[" + getGenome()[i].name + "]" + (i == (getGenome().length - 1) ? "" : ",");
            }
            str += ",[" + GASingleton.getInstance().getLastAgent().getType().toLetter() + GASingleton.getInstance().getLastAgent().getGraphNodeId() + "]";
            str += printTaskedAgents();
            return str;
        } else {
            String itemsStr = "";
            String itensAgent = "";
            for (int i = 0; i < genome.length; i++) {
                if (genome[i].cell.getColumn() != -1) {
                    // item
                    if (!itensAgent.equals("")) itensAgent += ", ";
                    itensAgent += genome[i].name;
                    if (i == genome.length - 1) {
                        itemsStr += " \n Agente " + GASingleton.getInstance().getMissingAgentString() + ": " + itensAgent;
                    }
                    continue;
                }
                // agent
                itemsStr += " \n Agente " + genome[i].name + ": " + itensAgent;
                itensAgent = "";

            }
            return itemsStr;
        }
    }


    public void setGene(int index, Item value) {
        genome[index] = value;
    }

    @Override
    public void swapGenes(VectorIndividual other, int index) {
        int auxI = 0;
        for (int i = 0; i < genome.length; i++) {
            if (genome[i].name.equals(other.genome[index].name)) {
                auxI = i;
            }
        }
        Item aux = genome[index];
        Item replace = genome[auxI];
        genome[index] = other.genome[index];
        genome[auxI] = aux;
        for (int i = 0; i < other.genome.length; i++) {
            if (other.genome[i].name.equals(aux.name)) {
                other.genome[i] = replace;
            }
        }
        other.genome[index] = aux;
    }
}
