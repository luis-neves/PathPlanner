package gui;

import gui.utils.BackgroundSurface;
import gui.utils.GraphSurface;
import newwarehouse.Warehouse;
import orderpicking.GNode;
import whgraph.ARWGraph;
import whgraph.GraphGenerator;


import javax.swing.*;
import java.awt.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class TestNewWarehouse extends JFrame {
    private Warehouse newwarehouse;

    public TestNewWarehouse() throws IOException {
        super("ARWARE Data Structure TestBed");

        setSize(950, 500);
        setLayout(new BorderLayout());

        newwarehouse=new Warehouse();
        try {
            String xmlcontent=read_xml_from_file("warehouse_model_lab.xml");
            newwarehouse.createFromXML(xmlcontent);
            newwarehouse.Print();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ARWGraph grafo = new ARWGraph();
        GraphGenerator gerador=new GraphGenerator(newwarehouse,new ArrayList<GNode>(),0.9f);
        grafo = gerador.createGraph();

        BackgroundSurface background = new BackgroundSurface(newwarehouse, false);
        GraphSurface graphsurface = new GraphSurface(grafo, newwarehouse,  5);
        JLayer<JPanel> jlayer = new JLayer<>(background, graphsurface);
        add(jlayer, BorderLayout.CENTER);

        String xmlstring=grafo.generateXMLGraphString();

        write_modelador_xml_to_file("graph_gen_lab.xml",xmlstring);
        repaint();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    static void write_modelador_xml_to_file(String fileName, String str)
            throws IOException {
        FileOutputStream outputStream = new FileOutputStream(fileName);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
    }

    public String read_xml_from_file(String fileName)
            throws IOException {
        String contents="";
        contents = new String(Files.readAllBytes(Paths.get(fileName)));


        return contents;
    }

    public static void main(String[] args) {
        try {
            new TestNewWarehouse().setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}