package gui;

import newwarehouse.Warehouse;
import orderpicking.GNode;
import pathfinder.Graph;
import whgraph.ARWGraph;
import whgraph.GraphGenerator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class TestaGenerator {

    public static String read_xml_from_file(String fileName)
            throws IOException {
        String contents="";
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

        Warehouse newwarehouse=new Warehouse();
        try {
            String xmlcontent=read_xml_from_file("warehouse_model_lab.xml");
            newwarehouse.createFromXML(xmlcontent);


            GraphGenerator gerador=new GraphGenerator(newwarehouse,new ArrayList<GNode>(),0.4f);
            ARWGraph grafo = gerador.createGraph();
            String xmlstring=grafo.generateXMLGraphString();

            write_modelador_xml_to_file("graph_gen_lab.xml",xmlstring);

            newwarehouse.Print();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
