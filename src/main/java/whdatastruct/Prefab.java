package whdatastruct;

import whgraph.Coordinates;

import java.awt.*;

public class Prefab implements Cloneable {
    private int id;
    private PrefabType type;
    private String name;
    private Size size;
    private String code = "";
    private String codeD = "";
    private Coordinates position;
    private Coordinates rotation;
    private Shape shape;

    @Override
    public whdatastruct.Prefab clone() throws CloneNotSupportedException {
        return (whdatastruct.Prefab) super.clone();
    }

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

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public Coordinates getRotation() {
        return rotation;
    }

    public void setRotation(Coordinates rotation) {
        this.rotation = rotation;
    }

    public Prefab(int id, int type, String name, Size size) {
        this.id = id;
        this.type = PrefabType.parse(type);
        this.name = name;
        this.size = size;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Prefab() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return PrefabType.getInt(type);
    }

    public void setType(int type) {
        this.type = PrefabType.parse(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }



    @Override
    public String toString() {
        return  getId() + " , " + type.toString() + " , "+ getName()+ " , " + (code == null ? "\t code null" : "\t" + code) +(codeD == null ? "\tcoded null" : "\t" +codeD) + " pos" + (position == null ? "\t coords null" : position.toString()) + " rot"  + (rotation == null ? "\t rotation null" : rotation.toString())+ getSize().toString();
    }
}

