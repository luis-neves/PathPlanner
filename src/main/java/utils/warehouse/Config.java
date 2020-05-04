package utils.warehouse;

public class Config {
    private float width;
    private float height;
    private float depth;
    Coordenates startConfig;

    public Config(float width, float height, float depth, Coordenates startConfig) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.startConfig = startConfig;
    }

    public Config() {

    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }


    public Config(float width, float height, float depth, float x, float y, float z) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "Config : " + "\n\t" + width + "\n\t" + height + "\n\t" + depth  + "\n\t" + startConfig.toString();
    }
}
