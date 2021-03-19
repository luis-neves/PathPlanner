package WHDataStruct;

public class Marker extends Prefab {

    private int mktype;

    public Marker() {

    }

    public Marker(int id, int type, String name, Size size) {
        super(id, type, name, size);
    }

    public Marker(Prefab prefab, int mktype){
        super(prefab.getId(),prefab.getType(),prefab.getName(),prefab.getSize());
        this.mktype = mktype;
    }

    @Override
    public String toString() {
        return "Marker " + super.toString() + "\n\t (" + getId() + "," + getType() + "," + getName() + ")" + getMkTypeSTR();
    }

    private String getMkTypeSTR() {
        switch (mktype){
            case 0:
                return "Marker";
        }
        return null;
    }
}

