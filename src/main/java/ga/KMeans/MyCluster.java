package ga.KMeans;

import java.awt.*;

public class MyCluster {
    private int id = -1;
    private Color color;

    public MyCluster(int id) {
        this.id = id;
        switch (id) {
            case 0:
                this.color = Color.blue;
                break;
            case 1:
                this.color = new Color(0,140,0);
                break;
            case 2:
                this.color = Color.MAGENTA;
                break;
            case 3:
                this.color = Color.RED;
                break;
            case 4:
                break;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
