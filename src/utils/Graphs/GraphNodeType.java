package utils.Graphs;

public enum GraphNodeType {
    SIMPLE, AGENT, PRODUCT, EXIT;

    public char toLetter() {
        return toString().toCharArray()[0];
    }
}
