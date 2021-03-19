package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import ARWDataStruct.Agent;
import ARWDataStruct.DataStruct;

import WHDataStruct.*;
import WHGraph.Graphs.GraphGenerator;
import WHGraph.Graphs.ARWGraph;

import gui.utils.GraphEditor;
import orderpicking.GNode;

import pathfinder.Graph;
import pathfinder.RouteFinder;


public class ARWDataStrTestBed extends JFrame {
    private final JButton ReadWareHouseXML;
    private final JButton ReadTaskXML;
    private final JButton ReadGraphXML;
    private final JButton EditGraphXML;
    private JButton CreateAssignedTaskXML;
    private final JTextArea Consola;
    private PrefabManager warehouse;
    private ARWGraph arwgraph;


    public ARWDataStrTestBed() {
        super("ARWARE Data Structure TestBed");

        ReadWareHouseXML= new JButton("Lê XML do Armazem") ;
        ReadTaskXML = new JButton("Executa Tarefa a partir dos XML") ;
        ReadGraphXML = new JButton("Novo Editor de Grafo") ;
        EditGraphXML= new JButton("Cria/Edita Grafo") ;

        Consola = new JTextArea(20,80);

        setLayout(new FlowLayout());

        add(ReadWareHouseXML);
        add(ReadTaskXML);
        add(ReadGraphXML);
        add(EditGraphXML);

        add(Consola);

        ReadWareHouseXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // delegate to event handler method
                warehouse = WHDataFuncs.readPrefabXML("warehouse_model.xml");
                //System.out.println(warehouse.getRacks().toString());
            }
        });

        ReadGraphXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // delegate to event handler method
                if (warehouse != null){
                    if (arwgraph==null)
                        arwgraph=new ARWGraph();
                    GraphEditor frame = new GraphEditor(warehouse,arwgraph, 0.5);
                    //GraphEditor_old frame = new GraphEditor_old(warehouse,arwgraph);

                }

            }
        });

        EditGraphXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // delegate to event handler method
                if (warehouse != null) {

                    ArrayList<GNode> startnodes = new ArrayList();
                    startnodes.add(new GNode("0","0",2,0.1));

                    GraphGenerator graphgenerator = new GraphGenerator(warehouse, startnodes, (float)0.8);
                    ARWGraph newgraph=graphgenerator.CreateNodes();
                    //graphgenerator.testa();


                    GraphEditor frame = new GraphEditor(warehouse, newgraph, 0.5);

                }
            }
        });

        ReadTaskXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                // delegate to event handler method
                try {
                    testaSolver();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        setSize(950, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public String read_xml_from_file(String fileName)
            throws IOException {
        String contents="";
        contents = new String(Files.readAllBytes(Paths.get(fileName)));


        return contents;
    }

    public void testaSolver() throws IOException {
        //Constroi grafo com todos os nós, incluindo produtos
        Graph<GNode> grafo;
        RouteFinder<GNode> routeFinder;
        //String[] produtos={};

        DataStruct dados = new DataStruct();

        dados.setPrefabFromFile("warehouse_model.xml");
        if ((arwgraph==null)||(arwgraph.getNumberOfgraphNodes()==0)) {
            arwgraph.readGraphFile(new File("graph2.xml"));
        }

        dados.setGraph(arwgraph);

        Agent agent = new Agent("ra1",0,0);
        dados.newAgent(agent);

        String xmltask = read_xml_from_file("tarefa.xml");

        dados.addTask(xmltask);


        System.out.println("Tour:\n " +dados.HandleTasks(agent.id));


    }

    public static void main(String[] args) {
        new ARWDataStrTestBed().setVisible(true);
    }
}