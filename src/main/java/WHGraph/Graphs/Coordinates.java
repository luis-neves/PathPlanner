package WHGraph.Graphs;


public class Coordinates {
    private float x;
    private float y;
    private float z;
    WHGraph.Graphs.Coordinates amplified;
    public Coordinates() {

    }
    public Coordinates(WHGraph.Graphs.Coordinates clone) {
        if (clone!=null) {
            x = clone.getX();
            y = clone.getY();
            z = clone.getZ();
            if (clone.amplified!=null)
                amplified = new WHGraph.Graphs.Coordinates(clone.amplified);
        }
    }
    @Override
    public Object clone() {
        return new WHGraph.Graphs.Coordinates(x,y,z);
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

    public Coordinates(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        amplified = new WHGraph.Graphs.Coordinates();
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

    public boolean isSame(WHGraph.Graphs.Coordinates location) {
        return this.getX() == location.getX() && this.getY() == location.getY() && this.getZ() == location.getZ();
    }

    public void amplifyINT(float amplifyX, float amplifyY) {
        this.x = Math.round(x * amplifyX);
        this.y = Math.round(y * amplifyY);
    }

    public boolean hasValue() {
        return x != 0 || y != 0 || z != 0;
    }

    public boolean hasZvalue() {
        return z != 0;
    }

    public WHGraph.Graphs.Coordinates amplified(float amplify) {
        amplified.x = x*amplify;
        amplified.y = y*amplify;
        amplified.z = z*amplify;
        return amplified;
    }
    public WHGraph.Graphs.Coordinates amplified() {
        return amplified;
    }

}

