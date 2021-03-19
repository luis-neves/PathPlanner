package WHDataStruct;

public class Structure extends Prefab {
    private int stype;

    public Structure() {

    }

    public int getStype() {
        return stype;
    }
    public String getStypeSTR() {
        switch (stype){
            case 0:
                return "Square Pillar";
            case 1:
                return "Round Pillar";
            case 2:
                return "Restricted Area";
            case 3:
                return "Fixed Machine";
        }
        return null;
    }

    public void setStype(int stype) {
        this.stype = stype;
    }

    public Structure(Prefab prefab, int stype){
        super(prefab.getId(),prefab.getType(),prefab.getName(),prefab.getSize());
        this.stype = stype;
    }

    @Override
    public String toString() {
        return "Structure " + super.toString() + "\t" + getStypeSTR();
    }
}
