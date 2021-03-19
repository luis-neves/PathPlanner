package gui.utils;

import WHDataStruct.PrefabManager;
import WHGraph.Graphs.ARWGraph;
import WHGraph.Graphs.ARWGraphNode;
import WHGraph.Graphs.Edge;
import WHGraph.Graphs.GraphNodeType;
import WHGraph.Line_Type;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;


public class GraphEditor extends JDialog {

    private double SENSIBILITY = 0.5;
    private static int NODE_SIZE = 5;
    public Line_Type line_type = Line_Type.SIMPLE;
    public Node_Action node_action = Node_Action.DRAW;
    //public PaintSurface surface;
    private final BackgroundSurface background;
    //PrefabManager prefabManager;
    public ARWGraph arwgraph;
    public static float AMPLIFY = 50;


    //Where the GUI is created:
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;

    public GraphEditor(PrefabManager prefabManager, ARWGraph arwgraph, double sensibility) throws HeadlessException {
        prefabManager = new PrefabManager(prefabManager);
        arwgraph=arwgraph;
        this.setTitle("Graph Editor");
        this.SENSIBILITY=sensibility;
        AMPLIFY=Math.min(((float)getSize().width)/prefabManager.getWidth(),((float)getSize().height)/prefabManager.getDepth());
        setModal(true);
//        AMPLIFY=Math.round(1000/prefabManager.getWidth());
        background = new BackgroundSurface(prefabManager,1000,600);

        this.setSize(new Dimension(background.scale(prefabManager.getWidth()*11 / 10),
                background.scale(prefabManager.getDepth()*12/8)));
        setLayout(new BorderLayout());

        /*
        fillAllPrefabs();

        HashMap<Integer, LinkedList<Shape>> shapes = generateShapes(allPrefabs);
*/
        setupMenuBar(arwgraph);

        //surface = new PaintSurface(shapes, prefabManager, arwgraph);

        LayerUI<JPanel> graphsurface = new GraphSurfaceEd(arwgraph, prefabManager, SENSIBILITY, NODE_SIZE);
        JLayer<JPanel> jlayer = new JLayer<JPanel>(background,graphsurface);


        JPanel panelButtonsNorth = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel node_action_label = new JLabel("Node Action");
        panelButtonsNorth.add(node_action_label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JButton button_draw = new JButton("Draw");
        panelButtonsNorth.add(button_draw, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        JButton button_remove = new JButton("Remove");
        panelButtonsNorth.add(button_remove, gbc);


        gbc.gridwidth = 2;


        button_draw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                node_action = Node_Action.DRAW;
            }
        });
        button_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                node_action = Node_Action.REMOVE;
            }
        });

        add(panelButtonsNorth, BorderLayout.NORTH);

        add(jlayer, BorderLayout.CENTER);

        this.setVisible(true);

        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }


            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    getArwgraph().removeNode(getArwgraph().getLastNode());
                    background.repaint();
                    jlayer.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });


    }

    private void setupMenuBar(ARWGraph arwgraph) {
        menuBar = new JMenuBar();

//Build the first menu.
        menu = new JMenu("Graph");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "File options");
        menuBar.add(menu);

//a group of JMenuItems
        menuItem = new JMenuItem("Export",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Export graph");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //SAVE GRAPH XML
                exportGraph(arwgraph);
            }
        });
        menuItem = new JMenuItem("Import",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Import graph");
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
        this.setJMenuBar(menuBar);
    }

    public ARWGraph getArwgraph() {
        return arwgraph;
    }

    private void LoadGraph() {

        JFileChooser fc = new JFileChooser(new File("."));
        fc.setSelectedFile(new File("graph.xml"));
        int returnVal = fc.showOpenDialog(this);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                arwgraph.readGraphFile(file);
                repaint();
            }
        } catch (NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!",
                    JOptionPane.ERROR_MESSAGE);
        }


    }

    private void exportGraph(ARWGraph ARWGraph) {
        JFileChooser fc = new JFileChooser(new File("."));
        fc.setSelectedFile(new File("graph.xml"));
        int returnVal = fc.showSaveDialog(this);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                FileWriter fw = new FileWriter(fc.getSelectedFile());
                fw.write(ARWGraph.generateXMLGraphString());
                fw.close();
            }
        } catch (NoSuchElementException | IOException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    class GraphSurfaceEd extends GraphSurface{

        ARWGraphNode start_node;
        ARWGraphNode end_node;

        public GraphSurfaceEd(ARWGraph ARWGraph, PrefabManager prefabmanager,double sensibility, int node_size) {
            arwgraph = ARWGraph;
            SENSIBILITY = sensibility;
            NODE_SIZE=node_size;
            this.prefabManager=prefabmanager;
//            AMPLIFY=Math.min(((float)getSize().width)/prefabManager.getWidth(),((float)getSize().height)/prefabManager.getDepth());

        }

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            JLayer jlayer = (JLayer) c;
            jlayer.setLayerEventMask(
                        AWTEvent.MOUSE_EVENT_MASK |
                                AWTEvent.MOUSE_MOTION_EVENT_MASK
            );
        }

        @Override
        public void uninstallUI(JComponent c) {
            JLayer jlayer = (JLayer) c;
            jlayer.setLayerEventMask(0);
            super.uninstallUI(c);
        }

        @Override
        protected void processMouseEvent(MouseEvent e, JLayer l) {
            if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                startDrag = new Point(e.getX(), e.getY());
                endDrag = startDrag;
                if (node_action == Node_Action.REMOVE) {
                    ARWGraphNode node = arwgraph.findClosestNode(prefabManager.getWidth() - background.descale(e.getX()),
                            background.descale(e.getY()), (float) SENSIBILITY);
                    if (node != null) {
                        arwgraph.removeNode(node);
                    }
                }
                l.repaint();

            }

            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                if (!(startDrag.x == e.getX() && startDrag.y == e.getY() || node_action == Node_Action.REMOVE)) {

                    if (arwgraph.distancetoNeighborEdge((prefabManager.getWidth() - background.descale(startDrag.x)),
                            background.descale(startDrag.y)) < SENSIBILITY) {

                        //Insere um nó, se for necessário, na aresta mais próxima, se dentro da sensibilidade
                        //Dado que não é garantido que o nó seja o último, o id serve paar identificar o nó
                        //de início.
                        start_node = arwgraph.insertNode(new ARWGraphNode(arwgraph.getMaxIdNodes() + 1,
                                (prefabManager.getWidth() - background.descale(startDrag.x)),
                                background.descale(startDrag.y), GraphNodeType.SIMPLE));
                        startDrag = new Point(background.scale(prefabManager.getWidth() - start_node.getLocation().getX()),
                                background.scale(start_node.getLocation().getY()));
                    } else {
                        arwgraph.createGraphNode((prefabManager.getWidth() - background.descale(startDrag.x)),
                                background.descale(startDrag.y), GraphNodeType.SIMPLE);
                        start_node = arwgraph.getLastNode();
                    }


                    int x1 = startDrag.x;
                    int y1 = startDrag.y;
                    int x2 = e.getX();
                    int y2 = e.getY();
                    if (Math.abs(startDrag.x - e.getX()) < background.scale(SENSIBILITY)) {
                        x2 = x1;
                    }
                    if (Math.abs(y1 - y2) < background.scale(SENSIBILITY)) {
                        y2 = y1;
                    }
                    endDrag.x = x2;
                    endDrag.y = y2;
                    if (arwgraph.distancetoNeighborEdge((prefabManager.getWidth() - background.descale(endDrag.x)),
                            background.descale(endDrag.y)) < SENSIBILITY) {

                        //Insere um nó, se for necessário, na aresta mais próxima, se dentro da sensibilidade
                        //Dado que não é garantido que o nó seja o último, o id serve paar identificar o nó
                        //de início.
                        end_node = arwgraph.insertNode(new ARWGraphNode(arwgraph.getMaxIdNodes() + 1,
                                (prefabManager.getWidth() - background.descale(endDrag.x)),
                                background.descale(endDrag.y), GraphNodeType.SIMPLE));

                        endDrag = new Point(background.scale(prefabManager.getWidth() - end_node.getLocation().getX()),
                                background.scale(end_node.getLocation().getY()));
                    } else {
                        arwgraph.createGraphNode((prefabManager.getWidth() - background.descale(endDrag.x)),
                                background.descale(endDrag.y), GraphNodeType.SIMPLE);
                        end_node = arwgraph.getLastNode();
                    }

                    arwgraph.makeNeighbors(start_node, end_node, false);

                    startDrag = null;
                    endDrag = null;
                    l.repaint();
                }
            }
            if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                endDrag = new Point(e.getX(), e.getY());
                l.repaint();
            }

        }

        @Override
        public void paint(Graphics g, JComponent c) {

            AMPLIFY=Math.min(((float)c.getSize().width)/prefabManager.getWidth(),((float)c.getSize().height)/prefabManager.getDepth());
            Graphics2D g2 = (Graphics2D) g.create();

            super.paint(g2, c);
            if (arwgraph!=null) {
                for (ARWGraphNode node : arwgraph.getGraphNodes()) {
                    g2.setPaint(Color.BLACK);
                    if (node.contains_product())
                        g2.setPaint(Color.BLUE);
                    g2.drawOval(scale(prefabManager.getWidth() - node.getLocation().getX()) - (NODE_SIZE / 2),
                            scale(node.getLocation().getY()) - (NODE_SIZE / 2), NODE_SIZE, NODE_SIZE);
                    g2.drawString(node.printName(), scale(prefabManager.getWidth() - node.getLocation().getX()) + (NODE_SIZE),
                            scale(node.getLocation().getY()) - (NODE_SIZE));
                }
                for (Edge e : arwgraph.getEdges()) {
                    Shape r = makeLine(scale(prefabManager.getWidth() - e.getStart().getLocation().getX()), scale(e.getStart().getLocation().getY()),
                            scale(prefabManager.getWidth() - e.getEnd().getLocation().getX()), scale(e.getEnd().getLocation().getY()));
                    g2.setPaint(Color.DARK_GRAY);
                    g2.draw(r);
                }

                if (startDrag != null && endDrag != null) {
                    g2.setPaint(Color.DARK_GRAY);
                    Shape r = makeLine(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                    g2.draw(r);
                }
            }
        }


    }


}
