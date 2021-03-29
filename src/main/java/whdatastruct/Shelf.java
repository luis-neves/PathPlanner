package whdatastruct;

public class Shelf {
    private int sID;
    private double sThick;
    private double sHeight;
    private String code;
    private String codeD;
    private Area area;
    private String wmsCode;


    public void setCode(String code) {
        this.code = code;
    }


    public void setCodeD(String codeD) {
        this.codeD = codeD;
    }

    public String getwmsCode() {
        return wmsCode;
    }

    public void setwmsCode(String wmsCode) {
        this.wmsCode = wmsCode;
    }

/*
    public Shelf() {

    }
*/
    public int getsID() {
        return sID;
    }

    public void setsID(int sID) {
        this.sID = sID;
    }

    public double getsThick() {
        return sThick;
    }

    public void setsThick(double sThick) {
        this.sThick = sThick;
    }

    public double getsHeight() {
        return sHeight;
    }

    public void setsHeight(double sHeight) {
        this.sHeight = sHeight;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "\n\t\t Shelf (" + getsID() +
                "," + getsThick() +
                "," + getsHeight() +
                ")" + getArea().toString();
    }
}
