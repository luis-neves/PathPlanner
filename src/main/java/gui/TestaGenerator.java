package gui;

import newWarehouse.Warehouse;
import orderpicking.GNode;
import whgraph.ARWGraph;
import whgraph.GraphGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestaGenerator {

    public static String read_xml_from_file(String fileName)
            throws IOException {
        String contents = "";
        contents = new String(Files.readAllBytes(Paths.get(fileName)));

        return contents;
    }


    static void write_modelador_xml_to_file(String fileName, String str)
            throws IOException {
        FileOutputStream outputStream = new FileOutputStream(fileName);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
    }

    public static void main(String[] args) {

        Warehouse newWarehouse = new Warehouse();
        try {
            String xmlContent = read_xml_from_file("warehouse_model_lab.xml");
            newWarehouse.createFromXML(xmlContent);

            GraphGenerator gerador = new GraphGenerator(newWarehouse,new ArrayList<GNode>(),0.4f);
            ARWGraph graph = gerador.createGraph();
            String xmlString = graph.generateXMLGraphString();

            write_modelador_xml_to_file("graph_gen_lab.xml",xmlString);

            newWarehouse.Print();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
