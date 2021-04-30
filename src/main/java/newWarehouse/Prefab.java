package newWarehouse;

import java.awt.geom.Rectangle2D;

public class Prefab {
    public Prefabtype type;
    public Rectangle2D.Float area;
    public Float rotation;

    public Prefab() {
        area= new Rectangle2D.Float();
    }
}
