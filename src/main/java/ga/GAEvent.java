package ga;

import clustering.Clustering;

public class GAEvent {

    GeneticAlgorithm source;
    Clustering cl_source;
    private boolean stopped;
    
    public GAEvent(GeneticAlgorithm source) {
        this.source = source;
    }
    public GAEvent(Clustering source) {
        this.cl_source = source;
    }
    
    public boolean isStopped(){
        return stopped;
    }
    
    public void setStopped(boolean stopped){
        this.stopped = stopped;
    }
    
    public GeneticAlgorithm getSource(){
        return source;
    }
}
