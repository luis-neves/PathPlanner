package WHDataStruct;

import WHGraph.Graphs.Coordinates;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface WHDataFuncs {


    static PrefabManager readPrefabXML(String filename) {
        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.print("Root element: ");
            System.out.println(doc.getDocumentElement().getNodeName());
            Element warehouse = doc.getDocumentElement();
            LinkedList prefabList = new LinkedList<>();
            Config config = new Config();
            for (int i = 0; i < warehouse.getChildNodes().getLength(); i++) {

                if (warehouse.getChildNodes().item(i).getNodeName().equals("xmlInfo")) {
                    XMLInfo xmlInfo = parseXMLInfoNode(warehouse.getChildNodes().item(i));
                    System.out.println(xmlInfo.toString() + "\n");
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("config")) {
                    config = parseConfigNode(warehouse.getChildNodes().item(i));
                    System.out.println(config.toString() + "\n");
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("prefabs")) {
                    prefabList = parsePrefabs(warehouse.getChildNodes().item(i));
                }
            }
            //FILL
            PrefabManager prefabManager = new PrefabManager(prefabList, config);
            //TODO
            for (int i = 0; i < warehouse.getChildNodes().getLength(); i++) {
                if (warehouse.getChildNodes().item(i).getNodeName().equals("racks")) {
                    prefabManager = parseRacks(warehouse.getChildNodes().item(i), prefabManager);
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("structures")) {
                    prefabManager = parseStructuresExtra(warehouse.getChildNodes().item(i), prefabManager);
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("devices")) {
                    //prefabManager = parseDevicesExtra(warehouse.getChildNodes().item(i), prefabManager);
                }
                if (warehouse.getChildNodes().item(i).getNodeName().equals("markers")) {
                    //prefabManager = parseMarkersExtra(warehouse.getChildNodes().item(i), prefabManager);
                }
            }


            return prefabManager;

        } catch (Exception ex) {
            // ex.printStackTrace();
            System.out.println("Não existe o ficheiro"+filename);
        }
        return null;
    }

    static XMLInfo parseXMLInfoNode(Node item) {
        String created = "";
        String modified = "";
        Byte updating = 0;
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            switch (item.getChildNodes().item(i).getNodeName()) {
                case "created":
                    created = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "modified":
                    modified = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "Updating":
                    updating = Byte.valueOf(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
            }
        }
        return new XMLInfo(created, modified, updating);
    }

    static Config parseConfigNode(Node item) {
        String width = "";
        String height = "";
        String depth = "";
        Coordinates coordinates = new Coordinates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            switch (item.getChildNodes().item(i).getNodeName()) {
                case "width":
                    width = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "height":
                    height = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "depth":
                    depth = item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue();
                    break;
                case "startConfig":
                    for (int j = 0; j < item.getChildNodes().item(i).getChildNodes().getLength(); j++) {
                        String startConfigChildNode = item.getChildNodes().item(i).getChildNodes().item(j).getNodeName();
                        switch (startConfigChildNode) {
                            case "positionX":
                                coordinates.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                                break;
                            case "positionY":
                                coordinates.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                                break;
                            case "positionZ":
                                coordinates.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                                break;
                        }
                    }
                    break;
            }

        }
        return new Config(Float.parseFloat(width), Float.parseFloat(height), Float.parseFloat(depth), coordinates);
    }


    static PrefabManager parseRacks(Node item, PrefabManager prefabManager) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabManager = parseRackEntry(item.getChildNodes().item(i), prefabManager);
            }
        }
        return prefabManager;
    }

    static PrefabManager parseRackEntry(Node item, PrefabManager prefabManager) {
        int prefabIDX = -1;
        Rack rack = new Rack();
        Coordinates position = new Coordinates();
        Coordinates rotation = new Coordinates();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String rackEntry = item.getChildNodes().item(i).getNodeName();
            boolean break_flag = false;
            switch (rackEntry) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    try {
                        rack = (Rack) prefabManager.findPrefabID(prefabID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break_flag = true;
                    break;
            }
            if (break_flag == true) {
                break;
            }
        }

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String rackEntry = item.getChildNodes().item(i).getNodeName();
            switch (rackEntry) {
                case "rCode":
                    rack.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "rCodeD":
                    rack.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "positionX":
                    position.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionY":
                    position.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionZ":
                    position.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationX":
                    rotation.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationY":
                    rotation.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationZ":
                    rotation.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "row":
                    rack.setRow(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "shelves":
                    rack = parseRackShelvesExtras(item.getChildNodes().item(i), rack);
                    break;
            }
        }
        rack.setPosition(position);
        rack.setRotation(rotation);
        prefabManager.addRack(rack);
        return prefabManager;
    }

    static Rack parseRackShelvesExtras(Node item, Rack rack) {
        String prefabChild = "";
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                rack = parseRackShelvesExtrasEntry(item.getChildNodes().item(i), rack);
            }
        }
        return rack;
    }

    static Rack parseRackShelvesExtrasEntry(Node item, Rack rack) {
        String rackshelfEntry = "";
        Shelf shelf = new Shelf();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            rackshelfEntry = item.getChildNodes().item(i).getNodeName();
            switch (rackshelfEntry) {
                case "sPrefabID":
                    int id = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    for (int j = 0; j < rack.getShelves().size(); j++) {
                        if (rack.getShelves().get(j).getsID() == id) {
                            shelf = rack.getShelves().get(j);
                        }
                    }
                    break;
            }
        }
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            rackshelfEntry = item.getChildNodes().item(i).getNodeName();
            switch (rackshelfEntry) {
                case "sCode":
                    shelf.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "sCodeD":
                    shelf.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "areas":
                    shelf = parseRackShelfExtra(item.getChildNodes().item(i), shelf);
                    break;
            }
        }
        return rack;
    }

    static Shelf parseRackShelfExtra(Node item, Shelf shelf) {

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String shelfSTR = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(shelfSTR)) {
                shelf = parseShelfEntryExtra(item.getChildNodes().item(i), shelf);
            }
        }
        return shelf;
    }

    static Shelf parseShelfEntryExtra(Node item, Shelf shelf) {
        String shelfSTR = "";
        SmallArea area = new SmallArea();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            shelfSTR = item.getChildNodes().item(i).getNodeName();
            switch (shelfSTR) {
                case "aPrefabID":
                    int id = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    for (int j = 0; j < shelf.getArea().getAreas().size(); j++) {
                        if (shelf.getArea().getAreas().get(j).getaID() == id) {
                            area = shelf.getArea().getAreas().get(j);
                        }
                    }
                    break;
            }
        }
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            shelfSTR = item.getChildNodes().item(i).getNodeName();
            switch (shelfSTR) {
                case "aCode":
                    area.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "aCodeD":
                    area.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "wmsCode":
                    shelf.setwmsCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "aProduct":
                    area.setProduct(Boolean.parseBoolean(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        return shelf;
    }

    static LinkedList<Prefab> parsePrefabs(Node item) {
        LinkedList<Prefab> prefabs = new LinkedList<Prefab>();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                Prefab prefab = parsePrefabEntry(item.getChildNodes().item(i));
                prefabs.add(prefab);
            }
        }
        return prefabs;
    }

    static Prefab parsePrefabEntry(Node item) {
        Prefab prefab = new Prefab();
        Size size = new Size();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabEntry = item.getChildNodes().item(i).getNodeName();
            switch (prefabEntry) {
                case "ID":
                    prefab.setId(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "type":
                    prefab.setType(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "name":
                    prefab.setName(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "sizeX":
                    size.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeY":
                    size.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeZ":
                    size.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        prefab.setSize(size);
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            switch (prefab.getType()) {
                case 0: //RACK
                    if (item.getChildNodes().item(i).getNodeName().equals("shelves")) {
                        prefab = new Rack(prefab, parseRackShelves(item.getChildNodes().item(i)));
                    }
                    break;
                case 1: //Structure
                    if (item.getChildNodes().item(i).getNodeName().equals("sType")) {
                        prefab = new Structure(prefab, Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    }
                    break;
                case 2: //Device
                    if (item.getChildNodes().item(i).getNodeName().equals("devType")) {
                        prefab = new Device(prefab, Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    }
                    break;
                case 3: //Marker
                    if (item.getChildNodes().item(i).getNodeName().equals("mkType")) {
                        prefab = new Marker(prefab, Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    }
                    break;
            }
        }

        return prefab;
    }

    static LinkedList<Shelf> parseRackShelves(Node item) {
        LinkedList<Shelf> shelves = new LinkedList<>();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            if (item.getChildNodes().item(i).getNodeName().equals("entry")) { //shelves entry
                Shelf shelf = new Shelf();

                for (int j = 0; j < item.getChildNodes().item(i).getChildNodes().getLength(); j++) {
                    String entryChild = item.getChildNodes().item(i).getChildNodes().item(j).getNodeName();
                    switch (entryChild) {
                        case "sID":
                            shelf.setsID(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                            break;
                        case "sThick":
                            shelf.setsThick(Double.parseDouble(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                            break;
                        case "sHeight":
                            shelf.setsHeight(Double.parseDouble(item.getChildNodes().item(i).getChildNodes().item(j).getChildNodes().item(0).getNodeValue()));
                            break;
                        case "areas":
                            shelf.setArea(parseAreaFromNode(item.getChildNodes().item(i).getChildNodes().item(j)));
                            break;
                    }
                }
                shelves.add(shelf);
            }
        }
        return shelves;
    }

    static Area parseAreaFromNode(Node item) {
        Area area = new Area();
        LinkedList<SmallArea> smallAreas = new LinkedList<>();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String areaChild = item.getChildNodes().item(i).getNodeName();
            switch (areaChild) {
                case "gridType":
                    area.setGridType(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "gridNumber":
                    area.setGridNumber(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "entry":
                    smallAreas.add(parseSmallAreaFromNode(item.getChildNodes().item(i)));
                    break;
            }
        }
        area.setAreas(smallAreas);
        return area;
    }

    static SmallArea parseSmallAreaFromNode(Node item) {
        SmallArea smallArea = new SmallArea();
        Size size = new Size();
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String smallAreaChild = item.getChildNodes().item(i).getNodeName();
            switch (smallAreaChild) {
                case "aID":
                    smallArea.setaID(Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeX":
                    size.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeY":
                    size.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "sizeZ":
                    size.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        smallArea.setSize(size);
        return smallArea;
    }


    static PrefabManager parseStructuresExtra(Node item, PrefabManager prefabManager) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabManager = parseStructuresExtraExtry(item.getChildNodes().item(i), prefabManager);
            }
        }
        return prefabManager;
    }

    static PrefabManager parseMarkersExtra(Node item, PrefabManager prefabManager) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabManager = parseMarkersExtraEntry(item.getChildNodes().item(i), prefabManager);
            }
        }
        return prefabManager;
    }

    static PrefabManager parseDevicesExtra(Node item, PrefabManager prefabManager) {
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            String prefabChild = item.getChildNodes().item(i).getNodeName();
            if ("entry".equals(prefabChild)) {
                prefabManager = parseDevicesExtraEntry(item.getChildNodes().item(i), prefabManager);
            }
        }
        return prefabManager;
    }

    static PrefabManager parseMarkersExtraEntry(Node item, PrefabManager prefabManager) {
        String markerSTR = "";
        Marker marker = new Marker();


        Coordinates position = new Coordinates();
        Coordinates rotation = new Coordinates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            markerSTR = item.getChildNodes().item(i).getNodeName();
            switch (markerSTR) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    try {
                        marker = (Marker) prefabManager.findPrefabID(prefabID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            markerSTR = item.getChildNodes().item(i).getNodeName();
            switch (markerSTR) {
                case "mkCode":
                    marker.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "positionX":
                    position.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionY":
                    position.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionZ":
                    position.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationX":
                    rotation.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationY":
                    rotation.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationZ":
                    rotation.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        marker.setPosition(position);
        marker.setRotation(rotation);
        prefabManager.addMarker(marker);
        return prefabManager;
    }

    static PrefabManager parseDevicesExtraEntry(Node item, PrefabManager prefabManager) {
        String structureSTR = "";
        Device device = new Device();

        Coordinates position = new Coordinates();
        Coordinates rotation = new Coordinates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    try {
                        device = (Device) prefabManager.findPrefabID(prefabID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "stCode":
                    device.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "stCodeD":
                    device.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "positionX":
                    position.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionY":
                    position.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionZ":
                    position.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationX":
                    rotation.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationY":
                    rotation.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationZ":
                    rotation.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        device.setPosition(position);
        device.setRotation(rotation);
        prefabManager.addDevice(device);
        return prefabManager;
    }

    static PrefabManager parseStructuresExtraExtry(Node item, PrefabManager prefabManager) {
        String structureSTR = "";
        Structure structure = new Structure();

        Coordinates position = new Coordinates();
        Coordinates rotation = new Coordinates();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "prefabID":
                    Integer prefabID = Integer.parseInt(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    try {
                        structure = (Structure) prefabManager.findPrefabID(prefabID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            structureSTR = item.getChildNodes().item(i).getNodeName();
            switch (structureSTR) {
                case "stCode":
                    structure.setCode(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "stCodeD":
                    structure.setCodeD(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue());
                    break;
                case "positionX":
                    position.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionY":
                    position.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "positionZ":
                    position.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationX":
                    rotation.setX(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationY":
                    rotation.setY(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
                case "rotationZ":
                    rotation.setZ(Float.parseFloat(item.getChildNodes().item(i).getChildNodes().item(0).getNodeValue()));
                    break;
            }
        }
        structure.setPosition(position);
        structure.setRotation(rotation);
        prefabManager.addStructure(structure);
        return prefabManager;
    }


}
