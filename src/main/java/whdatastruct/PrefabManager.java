package whdatastruct;

import whgraph.Graphs.Coordinates;

import java.util.Hashtable;
import java.util.LinkedList;

public class PrefabManager {

    LinkedList prefabList;
    LinkedList<Rack> racks;
    Hashtable<String,Integer> wmscodes;
    LinkedList<Structure> structures;
    LinkedList<Device> devices;
    LinkedList<Marker> markers;

    public Config config;

    public PrefabManager(whdatastruct.PrefabManager clone){
        this.prefabList=new LinkedList(clone.prefabList);
        this.racks=new LinkedList<Rack> (clone.racks);
        this.structures=new LinkedList<Structure>(clone.structures);
        this.devices=new LinkedList<Device> (clone.devices);
        this.markers=new LinkedList<Marker> (clone.markers);
        this.wmscodes =new Hashtable<>(clone.wmscodes);
        this.config=new Config(clone.config);
    }
    public PrefabManager(LinkedList prefabList, Config config) {
        this.prefabList = prefabList;
        this.racks = new LinkedList<>();
        this.structures = new LinkedList<>();
        this.devices = new LinkedList<>();
        this.markers = new LinkedList<>();
        this.wmscodes = new Hashtable<String, Integer>();
        this.config = config;
    }

    public LinkedList<Rack> getRacks() {
        return racks;
    }

    public LinkedList<Structure> getStructures() {
        return structures;
    }

    public LinkedList<Device> getDevices() {
        return devices;
    }

    public float getWidth(){
        return config.getWidth();
    }

    public float getDepth(){
        return config.getDepth();
    }

    public Coordinates getstartConfig(){
        return config.getStartConfig();
    }

    public boolean checkInBoundaries(float x, float y){
        return config.checkInBoundaries(x,y);
    }

    public Prefab findPrefabID(Integer prefabID) {
        for (Object prefab : prefabList) {
            Prefab pr = (Prefab) prefab;
            if (pr.getId() == prefabID) {
                return pr;
            }
        }
        return null;
    }

    public void addRack(Rack rack) {
        this.racks.add(rack);
        wmscodes =rack.getwmsCodesShelves(wmscodes);
    }

    public void addDevice(Device device) {
        this.devices.add(device);
    }

    public void addMarker(Marker marker) {
        this.markers.add(marker);
    }

    public void addStructure(Structure structure) {
        this.structures.add(structure);
    }

    public Rack findRack(int x, int y, float sensibility) {
        for (int i = 0; i < getRacks().size(); i++) {
            Rack rack = getRacks().get(i);
            if (rack.getShape() != null) {
                int new_x = Math.round((int) rack.getShape().getBounds().getCenterX());
                int new_y = Math.round((int) rack.getShape().getBounds().getCenterY());
                if (x - sensibility < new_x && new_x < x + sensibility) {
                    if (y - sensibility < new_y && new_y < y + sensibility) {
                        return rack;
                    }
                }
            }
        }
        return null;
    }

    public Rack findRackByID(int id) {
        for (Rack rack : racks) {
            if (id == Integer.parseInt(rack.getCode().split("RC")[1])){
                return rack;
            }
        }
        return null;
    }

    public boolean checkWms(String wms){
        return wmscodes.containsKey(wms);
    }

    public Rack findRackBywmsCode(String wms){
        Integer id= wmscodes.get(wms);
        return findRackByID(id);
    }


    public LinkedList<Prefab> getAllPrefabs() {
        LinkedList<Prefab> allPrefabs;
        allPrefabs = new LinkedList<>();
        allPrefabs.addAll(getRacks());
        allPrefabs.addAll(getStructures());
        allPrefabs.addAll(getDevices());
        return allPrefabs;
    }
}
