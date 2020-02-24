package utils.warehouse;

public class SmallArea {
    private int aID;
    private Size size;
    private String code;
    private String codeD;
    private boolean product;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeD() {
        return codeD;
    }

    public void setCodeD(String codeD) {
        this.codeD = codeD;
    }

    public boolean isProduct() {
        return product;
    }

    public void setProduct(boolean product) {
        this.product = product;
    }

    public SmallArea(int aID, Size size) {
        this.aID = aID;
        this.size = size;
    }

    public SmallArea() {

    }

    public int getaID() {
        return aID;
    }

    public void setaID(int aID) {
        this.aID = aID;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "\n\t\t\t\t\t SmallArea (" + aID + "," + size.toString() + ")" + " " + code + " " + codeD +  " Product: " + isProduct();
    }
}
