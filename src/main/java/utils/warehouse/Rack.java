package utils.warehouse;

import java.util.LinkedList;

public class Rack extends Prefab {
    LinkedList<Shelf> shelves = new LinkedList<Shelf>();
    private String row;

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public Rack() {

    }


    public LinkedList<Shelf> getShelves() {
        return shelves;
    }

    public void setShelves(LinkedList<Shelf> shelves) {
        this.shelves = shelves;
    }

    public Rack(int id, int type, String name, Size size) {
        super(id, type, name, size);
    }

    public Rack(Prefab prefab, LinkedList<Shelf> shelves) {
        super(prefab.getId(), prefab.getType(), prefab.getName(), prefab.getSize());
        this.shelves = shelves;
    }

    @Override
    public String toString() {
        return this.row + " " + "Rack " + super.toString() + "\n\t (" + getId() + "," + getType() + "," + getName() + ")" + printShelves();
    }

    private String printShelves() {
        String shelvesSTR = "";
        for (int i = 0; i < shelves.size(); i++) {
            shelvesSTR += shelves.get(i).toString();
        }
        return shelvesSTR;
    }
}
