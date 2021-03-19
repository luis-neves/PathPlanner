package WHDataStruct;

public class Device extends Prefab {
    private int devType;

    public Device() {

    }

    public int getDevType() {
        return devType;
    }

    public void setDevType(int devType) {
        this.devType = devType;
    }

    public Device(Prefab prefab, int stype){
        super(prefab.getId(),prefab.getType(),prefab.getName(),prefab.getSize());
        this.devType = stype;
    }
    @Override
    public String toString() {
        return "Device " + super.toString() + "\t" + getDevTypeSTR();
    }

    private String getDevTypeSTR() {
        switch (devType){
            case 0:
                return "Smart Glasses";
            case 1:
                return "Pallet Truck";
            case 2:
                return "User";
        }
        return null;
    }

}

