package gui;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.LayerUI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import ARWDataStruct.Agent;
import ARWDataStruct.DataStruct;

import ARWDataStruct.Order;
import WHDataStruct.*;
import WHGraph.Graphs.ARWGraph;
import gui.utils.*;
import net.sf.jni4net.Bridge;


import orderpicking.PickingOrders;
import org.json.JSONObject;

import classlib.BusMessage;
import classlib.CommunicationManager;
import classlib.TopicsConfiguration;
import classlib.Util;
import communication.ARW_CheckBus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class PathPlanner extends JFrame  {
    private JMenuBar menubar;
    private final BackgroundSurface background;
    private LayerUI<JPanel> graphsurface;

    private final JTextArea Consola;
    private final JTextField numtasks;
    private final JTextField numops;
    private JLabel alertanovoxml;
    private PrefabManager warehouse;
    private static ARWGraph arwgraph;
    Timer time;
    PacManSurface pacmansurface;
    CommunicationManager cm;
    ARW_CheckBus Checkbus;
    String Last_tarefa;
    DataStruct dados ;
    static PrintStream printOut;

    public static String CLIENT_ID = "planeador";
    public static double SENSIBILITY = 0.5;

    public static final String ERP_ID = "ERP";
    public static final String RA_ID = "ra";
    public static final String LOC_APROX_ID = "locaproximada";
    public static final String MODELADOR_ID = "modelador";
    public static final int NUM_OPERATORS = 1;
    public static final String OP_ID = "1";
    public static final String WAREHOUSE_FILE = "warehouse_model_lab.xml";
    public static final String GRAPH_FILE = "graph2.xml";
    public static final String TOPIC_UPDATEXML="mod_updateXML2";
    public static final String TOPIC_ACKXML="mod_updateXMLstatus";
    public static final String TOPIC_OPAVAIL="available";
    public static final String TOPIC_NEWOP="newOperator";
    public static final String TOPIC_GETTASK="getTarefa";
    public static final String TOPIC_ENDTASK="endTask";
    public static final String TOPIC_NEWTASK="newTask";
    public static final String TOPIC_CONCLUDETASK="setTarefaFinalizada";

    public int CHECK_ERP_PERIOD = 5; //MINUTOS


    public PathPlanner() {
        super("ARWARE Path Planner v0.1");

        setLayout(new BorderLayout());

        setupMenuBar();

        warehouse = WHDataFuncs.readPrefabXML(WAREHOUSE_FILE);
        dados= new DataStruct();

        if (warehouse != null){

            background = new BackgroundSurface(warehouse, 600,400);
            dados.setPrefab(warehouse);

            File file = new File(GRAPH_FILE);
            arwgraph=new ARWGraph();
            if (file!=null) {
                arwgraph.readGraphFile(file);
                if (arwgraph != null)
                    dados.setGraph(arwgraph);
            }
        }
        else
            background = new BackgroundSurface();

        graphsurface = new GraphSurface(arwgraph, warehouse, 0.5, 5, background.AMPLIFY);
        pacmansurface= new PacManSurface(background, 15);
        JLayer<JPanel> jlayer = new JLayer<JPanel>(background,graphsurface);

        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(pacmansurface, BorderLayout.CENTER);

        jlayer.setGlassPane(pane);
        pane.setOpaque(false);
        pane.setVisible(true);

        Consola = new JTextArea(2,40);

        JLabel et_tasks = new JLabel("Tarefas pendentes");
        JLabel et_ops = new JLabel("Operadores disponíveis");
        numtasks = new JTextField("0");
        numops = new JTextField("0");
        numtasks.setEditable(false);
        numops.setEditable(false);
        alertanovoxml=new JLabel("");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(et_tasks);
        panel.add(numtasks);
        panel.add(new JLabel("      "));
        panel.add(et_ops);
        panel.add(numops);
        panel.add(alertanovoxml);

        add(panel, BorderLayout.NORTH);
        //add(pacmansurface,BorderLayout.CENTER);
        add(jlayer, BorderLayout.CENTER);


        add(Consola, BorderLayout.PAGE_END);

        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                printOut.flush();
                printOut.close();
                System.exit(0);
            }
        });
        pack();
        setSize(950, 500);
        initComponents();
        setVisible(true);
    }

    private void avisaNovoXML(){
        alertanovoxml.setText("Novo Modelo de armazem disponível");
        alertanovoxml.setForeground(Color.red);
    }

    private void retiravisoXML(){
        alertanovoxml.setText("");
        alertanovoxml.setForeground(Color.gray);
    }
    private void setupMenuBar() {
        JMenu menu;
        JMenuItem menuItem;
        menubar = new JMenuBar();

//Build the first menu.
        menu = new JMenu("Data");
        menu.setMnemonic(KeyEvent.VK_D);
        menu.getAccessibleContext().setAccessibleDescription(
                "File options");
        menubar.add(menu);

//a group of JMenuItems
        menuItem = new JMenuItem("Load Warehouse",
                KeyEvent.VK_W);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load Warehouse Model");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadWarehouse();
                background.setPrefabs(warehouse);
                dados.setPrefab(warehouse);
                //arwgraph=null;
            }
        });


        menuItem = new JMenuItem("Load graph",
                KeyEvent.VK_G);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load graph of path nodes");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Load GRAPH XML
                LoadGraph();
                //surface.repaint();
                repaint();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Edit graph",
                KeyEvent.VK_G);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Edit graph of path nodes");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (warehouse != null) {
                    if (arwgraph == null)
                        arwgraph = new ARWGraph();
                    GraphEditor frame = new GraphEditor(warehouse, arwgraph, SENSIBILITY);
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            dados.setGraph(arwgraph);

                            repaint();
                        }
                    });
                }
                repaint();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Load Tasksim",
                KeyEvent.VK_S);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Load Simulated Task");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String xmlstring= null;
                try {
                    xmlstring = read_xml_from_file("tarefa.xml");
                    handleTask(xmlstring);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });

        //Build the settings menu.
        menuItem = new JMenuItem("Settings");
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Settings");
        menubar.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Temporário aviso de settings");
                SettingsDialog settingsDialog=new SettingsDialog(CHECK_ERP_PERIOD,SENSIBILITY,CLIENT_ID);

                CHECK_ERP_PERIOD=settingsDialog.CHECK_ERP_PERIOD;
                SENSIBILITY= settingsDialog.SENSIBILITY;
                CLIENT_ID=settingsDialog.CLIENT_ID;
                //time.cancel();
                time.schedule(new CheckERP(), 0, TimeUnit.MINUTES.toMillis(CHECK_ERP_PERIOD));
                repaint();

            }
        });
        this.setJMenuBar(menubar);
    }

    private void updateDados(){

        numtasks.setText(dados.getPendingorders().toString());
        numops.setText(dados.getAvailableAgents().toString());
        repaint();
    }

    private void LoadWarehouse() {

        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setSelectedFile(new File(WAREHOUSE_FILE));
        FileNameExtensionFilter filter= new FileNameExtensionFilter(WAREHOUSE_FILE,"xml");
        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
                warehouse = WHDataFuncs.readPrefabXML(fc.getSelectedFile().getName());
                if (warehouse!=null)
                    dados.setPrefab(warehouse);
                if (arwgraph!=null)
                    arwgraph.clear();
                File source = new File(fc.getSelectedFile().getName());
                File dest = new File(WAREHOUSE_FILE);
            try {
                Files.copy(source.toPath(),dest.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            background.repaint();
            retiravisoXML();
                repaint();
        }

    }

    private void LoadGraph() {

        JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        fc.setSelectedFile(new File(GRAPH_FILE));
        FileNameExtensionFilter filter= new FileNameExtensionFilter(GRAPH_FILE,"xml");
        fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(this);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                arwgraph.readGraphFile(file);
                if (arwgraph!=null)
                    dados.setGraph(arwgraph);
                repaint();
            }
        } catch (NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!",
                    JOptionPane.ERROR_MESSAGE);
        }


    }

    private void initComponents(){

        Checkbus = new ARW_CheckBus();
        //dados = new DataStruct();

        cm = new CommunicationManager(CLIENT_ID, new TopicsConfiguration(), Checkbus);
        Checkbus.SetCommunicationManager(cm); // Está imbricado. Tentar ver se é possível alterar!
        Checkbus.addPropertyChangeListener(new PropertyChangeListener() {
                                               @Override
                                               public void propertyChange(PropertyChangeEvent evt) {
                                                   handleMessages((BusMessage) evt.getNewValue());
                                               }
                                           }
        );

        cm.SubscribeContentAsync(TOPIC_UPDATEXML,MODELADOR_ID);


        System.out.println("CLIENT_ID: " + CLIENT_ID);
        System.out.println("ERP_ID: " + ERP_ID);
        System.out.println("RA_ID: " + RA_ID + "[MAC]");
        System.out.println("LOC_APROX_ID: " + LOC_APROX_ID);
        System.out.println("MODELADOR_ID: " + MODELADOR_ID);




        time = new Timer(); // Instantiate Timer Object

        //Faz um pedido ao ERP a cada 5 minutos - VER SE SERÁ O TEMPO ADEQUADO
        time.schedule(new CheckERP(), 0, TimeUnit.MINUTES.toMillis(CHECK_ERP_PERIOD));

    }

    public void PedeTarefa(){
        this.Consola.append("Pedida tarefa"+'\n');
        this.cm.SendMessageAsync(Util.GenerateId(), "request", TOPIC_GETTASK, ERP_ID, "PlainText", "Dá-me uma tarefa!", "1");
    }


    public void EnviaTarefa(String agentid, String xmlstring){
        try {
            playPacman(agentid,xmlstring);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        this.Consola.append("A enviar tarefa para "+agentid+"\n");
            cm.SendMessageAsync(Util.GenerateId(), "request", TOPIC_NEWTASK, agentid, "application/xml", xmlstring, "1");

    }

    public void ConcluiTarefa(String xmlstring) {

            //A Linha abaixo é temporária - substituir por reconstrução da tarefa
            xmlstring = Last_tarefa;
            this.Consola.append("Enviada a conclusão de tarefa\n");
            cm.SendMessageAsync(Util.GenerateId(), "response", TOPIC_CONCLUDETASK, ERP_ID, "application/xml", xmlstring, "1");


    }
    public void handleMessages(BusMessage busMessage){
        String xml_str;
        Float[] position={(float)0.0,(float)0.0};
        switch (busMessage.getMessageType()) {
            case "request":
                System.out.println("REQUEST message ready to be processed.");
                String identificador=busMessage.getInfoIdentifier();
                switch (identificador) {

                    case TOPIC_OPAVAIL:
                        xml_str = busMessage.getContent();
                        System.out.println(xml_str);//Provisoriamente para teste
                        Consola.setText(xml_str+"\n");
                        String[] split = busMessage.getFromTopic().split("Topic");
                        String agentid = split[0];

                        JSONObject obj = new JSONObject(xml_str);
//                        Float posx,posy;
                        if (obj != null) {
//                            posx=(Float) obj.get("posicaox");
  //                          posy=(Float) obj.get("posicaoy");

                            if (obj.has("posicaox"))
                                position[0] = Float.parseFloat(obj.get("posicaox").toString());
                            if (obj.has("posicaoy"))
                                position[1] = Float.parseFloat(obj.get("posicaoy").toString());
                            if ((warehouse==null)||(position[0]>warehouse.getWidth())||(position[1]>warehouse.getDepth())){
                                cm.SendMessageAsync((Integer.parseInt(busMessage.getId()) + 1) + "", "response",
                                        busMessage.getInfoIdentifier(), split[0], "application/json", "{'response':'not ACK'}", "1");
                                pacmansurface.addAgent(agentid,new Point2D.Float(position[0],position[1]));
                            }
                            //String disponivel = obj.get("available").toString();

                        }
                        else
                            cm.SendMessageAsync((Integer.parseInt(busMessage.getId()) + 1) + "", "response",
                                    busMessage.getInfoIdentifier(), split[0], "application/json", "{'response':'not ACK'}", "1");

                        if (dados.checkagent(agentid)) {
                            dados.releaseUpdate(agentid, position[0],
                                    position[1]);
                        } else {
                            dados.newAgent(new Agent(agentid, position[0],
                                    position[1]));
                        }


                        cm.SendMessageAsync((Integer.parseInt(busMessage.getId()) + 1) + "", "response",
                                busMessage.getInfoIdentifier(), split[0], "application/json", "{'response':'ACK'}", "1");

                        xml_str = dados.HandleTasks(agentid);
                        if (xml_str.equals("") == false) {
                            EnviaTarefa(agentid, xml_str);
                        } else
                            PedeTarefa();

                        break;

                    case TOPIC_NEWOP:

                        xml_str = busMessage.getContent();
                        System.out.println(xml_str);//Provisoriamente para teste
                        Consola.setText(xml_str+"\n");
                        split = busMessage.getFromTopic().split("Topic");
                        agentid = split[0];


                        try {//Falta o processo de envio em bocados
                            String xml_armazem = read_xml_from_file(WAREHOUSE_FILE);
                            cm.SendMessageAsync((Integer.parseInt(busMessage.getId()) + 1) + "", "response",
                                    busMessage.getInfoIdentifier(), split[0], "application/xml", xml_armazem, "1");
                        } catch (IOException e) {
                            cm.SendMessageAsync((Integer.parseInt(busMessage.getId()) + 1) + "", "response",
                                    busMessage.getInfoIdentifier(), split[0], "application/json", "{'response':'ACK'}", "1");
                        }

                        break;

                    case TOPIC_ENDTASK:
                        xml_str = busMessage.getContent();
                        System.out.println(xml_str);//Provisoriamente para teste
                        Consola.setText("Operador concluiu tarefa\n");
                        split = busMessage.getFromTopic().split("Topic");

                        try {

                            PickingOrders concludedpicks = new PickingOrders();
                            concludedpicks.parseTarefaXML(xml_str);
                            for (Order order: concludedpicks.getOrders())
                                dados.concludeOrder(order.id);
                            xml_str=concludedpicks.toXML();
                            write_modelador_xml_to_file("tarefa_concluida.xml", xml_str);
                            ConcluiTarefa(xml_str);

                            System.out.println("Succesfully saved concluded task>");
                        } catch (IOException e) {
                            System.out.println("Error while saving concluded task");
                            System.out.println(e.getMessage());
                        }
                        cm.SendMessageAsync((Integer.parseInt(busMessage.getId()) + 1) + "", "response",
                                busMessage.getInfoIdentifier(), split[0], "application/json", "{'response':'ACK'}", "1");
                        System.out.println("Enviou Ack!");

                }
                break;

            case "response":
                System.out.println("RESPONSE message ready to be processed.");
                if (busMessage.getInfoIdentifier().equals(TOPIC_GETTASK) && busMessage.getDataFormat().equals("application/xml")) {
                    //GET ALL ORDERS FROM ERP
                    Last_tarefa=busMessage.getContent();
                    System.out.println(Last_tarefa);
                    //this.Consola.setText(Last_tarefa);
                    try {
                        handleTask(Last_tarefa);
                        System.out.println("Succesfully saved tarefa.xml");
                    } catch (IOException e) {
                        System.out.println("Error while saving tarefa.xml");
                        System.out.println(e.getMessage());
                    }

                } else if (busMessage.getInfoIdentifier().equals(TOPIC_NEWTASK) /*&& busMessage.getDataFormat().equals("application/json")*/) {
                    String json_str = busMessage.getContent();

                    System.out.println(json_str);//Provisoriamente para teste
                    System.out.println("Tarefa bem recebida");
                    //Tratar Json para saber se tarefa ficou atribuída
                }
                else if (busMessage.getInfoIdentifier().equals(TOPIC_CONCLUDETASK) /*&& busMessage.getDataFormat().equals("application/json")*/) {
                    String json_str = busMessage.getContent();

                    System.out.println(json_str);//Provisoriamente para teste
                    System.out.println("Acknowledge de tarefa concluida recebido");

                }
                break;
            case "stream":
                System.out.println("STREAM message ready to be processed.");
                switch (busMessage.getInfoIdentifier())
                {
                    case "updateXML":

                        xml_str = busMessage.getContent();
                        JSONObject coderollsJSONObject = new JSONObject(xml_str);
                        System.out.println(xml_str);//Provisoriamente para teste
                        String id="";
                        if (coderollsJSONObject.get("id")!=null)
                            id = coderollsJSONObject.get("id").toString();
                        else
                            id="model";
                        String npart=coderollsJSONObject.get("nPart").toString();
                        String totalparts=coderollsJSONObject.get("totalParts").toString();
                        String content=coderollsJSONObject.get("xmlPart").toString();
                        System.out.println("n de partes: "+totalparts+" parte: "+npart);
                        completeXML(id,Integer.parseInt(npart),Integer.parseInt(totalparts),content);

                        break;
                    default:
                        //TODO: do nothing, isn't important. You can print a message for debug purposes.
                        System.out.println("Saiu default");
                        break;
                }
                break;
        }
        updateDados();

    }

    Hashtable<Integer,String> xmlparts;
    String lastid;

    public void completeXML(String id,int npart, int totalparts, String part){
        if (xmlparts==null){
            xmlparts=new Hashtable<Integer,String>();
            lastid=id;
        }
        if ((!xmlparts.containsKey(npart))&&id.equals(lastid) ){
            xmlparts.put(npart, part);
            if (xmlparts.size() == totalparts) {
                String xmlmessage = "";
                for (Integer i = 0; i < totalparts; i++) {
                    xmlmessage = xmlmessage + xmlparts.get(i);
                    System.out.println("Adicionou");

                }
                Consola.setText("Recebeu XML\n");
                String xmlanswer = new JSONObject()
                        .put("id",id)
                        .put("ack","OK").toString();

                cm.SendMessageAsync(Util.GenerateId(), "response", "mod_updateXMLstatus", MODELADOR_ID, "application/json",
                        xmlanswer, "1");
                xmlparts = null;
                lastid="";
                try {
                    write_modelador_xml_to_file("warehouse_recebido.xml", xmlmessage);
                    avisaNovoXML();
                    System.out.println("Succesfully saved warehouse_recebido.xml");

                } catch (IOException e) {
                    System.out.println("Error while saving warehouse_recebido.xml");
                    System.out.println(e.getMessage());

                }
            } else {
                System.out.println("Waiting for the remaining " + new Integer(totalparts - xmlparts.size()).toString() + " parts of the warehouse model.");
            }
        }
        else{

            String xmlanswer = new JSONObject()
                    .put("id",id)
                    .put("ack","ERROR").toString();
            System.out.println(xmlanswer);
            cm.SendMessageAsync(Util.GenerateId(), "response", "mod_updateXMLstatus", MODELADOR_ID, "application/json",
                    xmlanswer, "1");
            xmlparts=null;

        }
    }

    public void handleTask(String xmlstring) throws IOException {
            String agentid="ra1";//Serve para ter um nome mas será substituido em HandleTasks.
            dados.addTask(xmlstring);
            write_modelador_xml_to_file("tarefa.xml", xmlstring);
            String xml_str= dados.HandleTasks(agentid);
            if (xml_str.equals("")==false){
                EnviaTarefa(agentid,xml_str);
        }
            updateDados();
    }

    public String read_xml_from_file(String fileName)
            throws IOException {
        String contents="";
        contents = new String(Files.readAllBytes(Paths.get(fileName)));


        return contents;
    }

    public class CheckERP extends TimerTask {


        public void run() {
            try {

                PedeTarefa();

            } catch (Exception ex) {
                System.out.println("error running thread " + ex.getMessage());
            }
        }
    }

    public void write_modelador_xml_to_file(String fileName, String str)
            throws IOException {
        FileOutputStream outputStream = new FileOutputStream(fileName);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
    }

    public void playPacman(String agentid, String content) throws IOException, SAXException {

        String posx;
        String posy;
        System.out.println(content);
        DocumentBuilder db = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is = new InputSource(new StringReader(content));

        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();
        NodeList path_nodes = doc.getElementsByTagName("Node");
        NodeList pick_nodes = doc.getElementsByTagName("Tarefa");


        for (int j = 0; j < pick_nodes.getLength(); j++) {
            Element element = (Element) pick_nodes.item(j);

            if (element.getNodeType()== Node.ELEMENT_NODE) {
                String wmscode=element.getElementsByTagName("Origem").item(0).getTextContent();
                String productid=element.getElementsByTagName("LinhaOrdem").item(0).getTextContent();
                Rack rack=warehouse.findRackBywmsCode(wmscode);

                float xx= rack.getPosition().getX();
                float yy=rack.getPosition().getY();

                pacmansurface.addProduct(productid,new Point2D.Float(xx,yy));


            }
        }
        repaint();
        for (int j = 0; j < path_nodes.getLength(); j++) {
            Element element = (Element) path_nodes.item(j);

            if (element.getNodeType() == Node.ELEMENT_NODE) {
                posx = element.getElementsByTagName("x").item(0).getTextContent();
                posy = element.getElementsByTagName("y").item(0).getTextContent();
                posx = posx.replace(',', '.');
                posy = posy.replace(',', '.');
                float xx = Float.parseFloat(posx);
                float yy = Float.parseFloat(posy);

                pacmansurface.updateAgent(agentid, new Point2D.Float(xx, yy));
                repaint();
                for (int i = 0; i < element.getElementsByTagName("Tarefa").getLength(); i++) {
                    pacmansurface.removeProduct(element.getElementsByTagName("LinhaOrdem").item(i).getTextContent());
                }


                repaint();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        pacmansurface.removeAgent(agentid);
        repaint();

    }


    public static void main(String[] args) {

        final String dir = System.getProperty("user.dir");
        //SERVICE BUS
        OutputStream output = null;
        try {
            output = new FileOutputStream(dir + "\\logPathPlanner.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        printOut = new PrintStream(output);

        System.setOut(printOut);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        Bridge.setVerbose(true);
        Bridge.setDebug(true);
        try {
            Bridge.init();
            File proxyAssembyFile = new File(dir +"/ClassLib.j4n.dll");
            Bridge.LoadAndRegisterAssemblyFrom(proxyAssembyFile);
        } catch (Exception e) {
            try{
                File proxyAssembyFile = new File(dir +"/lib/ClassLib.j4n.dll");
                Bridge.LoadAndRegisterAssemblyFrom(proxyAssembyFile);
            }catch (Exception e2){
                System.out.println("Error");
                e2.printStackTrace();
            }
        }

        new PathPlanner().setVisible(true);

    }
}