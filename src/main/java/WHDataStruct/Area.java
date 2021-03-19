package WHDataStruct;


import java.util.LinkedList;

public class Area {
    private int gridType;
    private int gridNumber;
    private LinkedList<SmallArea> areas;
    private String wmsCode;

    public Area(int gridType, int gridNumber, LinkedList<SmallArea> areas) {
        this.gridType = gridType;
        this.gridNumber = gridNumber;
        this.areas = areas;
    }

    public Area() {

    }

    public int getGridType() {
        return gridType;
    }

    public void setGridType(int gridType) {
        this.gridType = gridType;
    }

    public int getGridNumber() {
        return gridNumber;
    }

    public void setGridNumber(int gridNumber) {
        this.gridNumber = gridNumber;
    }

    public LinkedList<SmallArea> getAreas() {
        return areas;
    }

    public void setAreas(LinkedList<SmallArea> areas) {
        this.areas = areas;
    }

    @Override
    public String toString() {
        return "\n\t\t\t Area: (" + getGridTypeSTR() + "," + getGridNumber() + ")" + printSmallAreas();
    }

    private String printSmallAreas() {
        String area = "";
        if(areas != null){

            for(int i = 0; i < areas.size(); i++){
                area +=  areas.get(i).toString();
            }
            return area;
        }
        return "";
    }

    private String getGridTypeSTR() {
        switch (gridType){
            case 0:
                return "1x1, No Division";
            case 1:
                return "1xN";
            case 2:
                return "2xN";
        }
        return null;
    }
}

