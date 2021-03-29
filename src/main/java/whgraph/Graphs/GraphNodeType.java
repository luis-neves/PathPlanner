package whgraph.Graphs;

public enum GraphNodeType {
    SIMPLE, AGENT, PRODUCT, DELIVERING, EXIT;

    public char toLetter() {
        return toString().toCharArray()[0];
    }
}
