package whdatastruct;

import whgraph.Graphs.Coordinates;

public class Config {
    private float width;
    private float height;
    private float depth;
    Coordinates startConfig;

    public Config(float width, float height, float depth, Coordinates startConfig) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.startConfig = startConfig;
    }

    public Config() {
        startConfig=new Coordinates();
    }

    public Config(whdatastruct.Config clone) {
        this.width=clone.width;
        this.height=clone.height;
        this.depth=clone.depth;
        this.startConfig=new Coordinates(startConfig);

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

    public Coordinates getStartConfig() {
        return startConfig;
    }

    public boolean checkInBoundaries(float x, float y){
        return ((x>=0)&&(x<width)&&(y>=0)&&(y<depth));
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

