package arwdatastruct;

public class Agent {
    private String id;
    private float initialX;
    private float initialY;

    public Agent(String id, float initialX, float initialY) {
        this.id = id;
        this.initialX = initialX;
        this.initialY = initialY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getInitialX() {
        return initialX;
    }

    public void setInitialX(float initialX) {
        this.initialX = initialX;
    }

    public float getInitialY() {
        return initialY;
    }

    public void setInitialY(float initialY) {
        this.initialY = initialY;
    }
}
