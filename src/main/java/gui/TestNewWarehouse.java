package gui;

import gui.utils.BackgroundSurface;
import gui.utils.GraphSurface;
import newwarehouse.Warehouse;

import whgraph.ARWGraph;



import javax.swing.*;
import java.awt.*;

import java.io.IOException;


import static xmlutils.XMLfuncs.read_xml_from_file;
import static xmlutils.XMLfuncs.write_xml_to_file;


public class TestNewWarehouse extends JFrame {
    private Warehouse newwarehouse;

    public TestNewWarehouse() throws IOException {
        super("ARWARE Data Structure TestBed");

        setSize(950, 500);
        setLayout(new BorderLayout());

        newwarehouse=new Warehouse();
        newwarehouse.geraWarehouse(40f,30f,2f,3f,3,8,1f);

            newwarehouse.Print();

        ARWGraph grafo = new ARWGraph();

        grafo.createGraph(newwarehouse,2f);

        BackgroundSurface background = new BackgroundSurface(newwarehouse, false,950);
        GraphSurface graphsurface = new GraphSurface(grafo, newwarehouse,  5);
        JLayer<JPanel> jlayer = new JLayer<>(background, graphsurface);
        add(jlayer, BorderLayout.CENTER);

        String xmlstring=grafo.generateXMLGraphString();

        write_xml_to_file("graph_gen_lab.xml",xmlstring);
        repaint();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) {
        try {
            new TestNewWarehouse().setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}