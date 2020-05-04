package utils.warehouse;

public enum PrefabType {
    RACK,STRUCTURE,DEVICE, UNKNOWN;


    public static PrefabType parse(int value){
        switch (value){
            case 0:
                return RACK;
            case 1:
                return STRUCTURE;
            case 2:
                return DEVICE;
        }
        return  UNKNOWN;
    }

    public static int getInt(PrefabType type) {
        switch (type){
            case RACK:
                return 0;
            case STRUCTURE:
                return 1;
            case DEVICE:
                return 2;
        }
        return -1;
    }
}

