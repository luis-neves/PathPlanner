package newWarehouse;

public enum Prefabtype {
    RACK, STRUCTURE, FLOORAREA, EXIT;

    public char toLetter() {
        return toString().toCharArray()[0];
    }
}
