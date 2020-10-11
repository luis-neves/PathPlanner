package utils.warehouse;

import java.util.Comparator;

public class Prefab implements Cloneable {
    private int id;
    private PrefabType type;
    private String name;
    private Size size;
    private String code = "";
    private String codeD = "";
    private Coordenates position;
    private Coordenates rotation;

    @Override
    public Prefab clone() throws CloneNotSupportedException {
        return (Prefab) super.clone();
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

    public Coordenates getPosition() {
        return position;
    }

    public void setPosition(Coordenates position) {
        this.position = position;
    }

    public Coordenates getRotation() {
        return rotation;
    }

    public void setRotation(Coordenates rotation) {
        this.rotation = rotation;
    }

    public Prefab(int id, int type, String name, Size size) {
        this.id = id;
        this.type = PrefabType.parse(type);
        this.name = name;
        this.size = size;
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
