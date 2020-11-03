package utils.warehouse;

public class Coordenates {
    private float x;
    private float y;
    private float z;
    Coordenates amplified;
    public Coordenates() {
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Coordenates(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        amplified = new Coordenates();
        amplified.x = x;
        amplified.y = y;
        amplified.z = z;
    }

    @Override
    public String toString() {
        return " XYZ (" + x + "," + y + "," + z + ")";
    }
    public String printOnlyValues(){
        return Math.round(x) +"," + Math.round(y) + "," + Math.round(z);
    }

    public boolean isSame(Coordenates location) {
        return this.getX() == location.getX() && this.getY() == location.getY() && this.getZ() == location.getZ();
    }

    public void amplifyINT(float amplifyX, float amplifyY) {
        this.x = Math.round(x * amplifyX);
        this.y = Math.round(y * amplifyY);
    }

    public boolean hasValue() {
        if (x != 0 || y != 0 || z != 0){
            return true;
        }
        return false;
    }

    public boolean hasZvalue() {
        if (z != 0){
            return true;
        }
        return false;
    }

    public Coordenates amplified(float amplify) {
        amplified.x = x*amplify;
        amplified.y = y*amplify;
        amplified.z = z*amplify;
        return amplified;
    }
    public Coordenates amplified() {
        return amplified;
    }

    }
