package gui;

import gui.utils.BackgroundSurface;

import newwarehouse.Warehouse;


import javax.swing.*;
import java.awt.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;



public class TestNewWarehouse extends JFrame {
    private Warehouse newwarehouse;

    public TestNewWarehouse() {
        super("ARWARE Data Structure TestBed");

        setSize(950, 500);
        setLayout(new BorderLayout());

        newwarehouse=new Warehouse();
        try {
            String xmlcontent=read_xml_from_file("warehouse_model_old_.xml");
            newwarehouse.createFromXML(xmlcontent);
            newwarehouse.Print();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BackgroundSurface background = new BackgroundSurface(newwarehouse, 800,400);
        add(background, BorderLayout.CENTER);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public String read_xml_from_file(String fileName)
            throws IOException {
        String contents="";
        contents = new String(Files.readAllBytes(Paths.get(fileName)));


        return contents;
    }

    public static void main(String[] args) {
        new TestNewWarehouse().setVisible(true);
    }
}