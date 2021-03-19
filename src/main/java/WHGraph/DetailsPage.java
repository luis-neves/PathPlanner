package WHGraph;

import WHDataStruct.Prefab;
import WHDataStruct.PrefabManager;

import WHDataStruct.Rack;
import WHDataStruct.Structure;
import WHGraph.Graphs.*;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;


public class DetailsPage extends JFrame {

    private static final int LINE_PIXEL_SENSIBILITY = 5;
    private static final int NODE_SIZE = 5;
    public Line_Type line_type = Line_Type.SIMPLE;
    public Node_Action node_action = Node_Action.DRAW;
    public PaintSurface surface;
    PrefabManager prefabManager;
    WHGraph.Graphs.ARWGraph ARWGraph = new ARWGraph();
    public static final float AMPLIFY = 50;
    private static final Integer PREFAB_RACK = 0;
    private static final Integer PREFAB_STRUCTURE = 1;
    LinkedList<Prefab> allPrefabs;

    //Where the GUI is created:
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;

    public DetailsPage(PrefabManager prefabManager) throws HeadlessException {
        this.prefabManager = new PrefabManager(prefabManager);
        this.setTitle("Details");

        this.setSize(new Dimension(scale(prefabManager.getWidth()*3 / 2), scale(prefabManager.getDepth()*3/2)));

        fillAllPrefabs();
        HashMap<Integer, LinkedList<Shape>> shapes = generateShapes(allPrefabs);
        setLayout(new BorderLayout());
        setupMenuBar(ARWGraph);
        surface = new PaintSurface(shapes, prefabManager, ARWGraph);

        JPanel panelButtonsNorth = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel product_text = new JLabel("Line Type");
        panelButtonsNorth.add(product_text, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JButton buttonSimpleLine = new JButton("Simple");
        panelButtonsNorth.add(buttonSimpleLine, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        JButton buttonProductLine = new JButton("Product");
        panelButtonsNorth.add(buttonProductLine, gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        JButton buttonExitLine = new JButton("Exit");
        panelButtonsNorth.add(buttonExitLine, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel node_action_label = new JLabel("Node Action");
        panelButtonsNorth.add(node_action_label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JButton button_draw = new JButton("Draw");
        panelButtonsNorth.add(button_draw, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        JButton button_remove = new JButton("Remove");
        panelButtonsNorth.add(button_remove, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        JButton button_generateProduct = new JButton("Generate Product");
        panelButtonsNorth.add(button_generateProduct, gbc);


        gbc.gridwidth = 2;

        buttonSimpleLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                line_type = Line_Type.SIMPLE;
            }
        });
        buttonProductLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                line_type = Line_Type.PRODUCT;
            }
        });
        buttonExitLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                line_type = Line_Type.EXIT;
            }
        });

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

        button_generateProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                node_action = Node_Action.GENERATE_PRODUCT;
            }
        });

        add(panelButtonsNorth, BorderLayout.NORTH);
        add(surface, BorderLayout.CENTER);
        this.setVisible(true);

        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    ARWGraph.removeNode(ARWGraph.getLastNode());
                    surface.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });

    }

    private void setupMenuBar(WHGraph.Graphs.ARWGraph ARWGraph) {
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
                exportGraph(ARWGraph);
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
                surface.repaint();
            }
        });
        menu.add(menuItem);
        this.setJMenuBar(menuBar);
    }

    private void LoadGraph() {

        JFileChooser fc = new JFileChooser(new File("."));
        fc.setSelectedFile(new File("graph.xml"));
        int returnVal = fc.showOpenDialog(this);
        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                ARWGraph.readGraphFile(file);
                repaint();
            }
        } catch (NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!",
                    JOptionPane.ERROR_MESSAGE);
        }


    }

    private void exportGraph(WHGraph.Graphs.ARWGraph ARWGraph) {
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


    private class PaintSurface extends JComponent {
        private final HashMap<Integer, LinkedList<Shape>> shapes;
        private final LinkedList<Shape> drawables;
        private final WHGraph.Graphs.ARWGraph ARWGraph;

        Point startDrag, endDrag;
        ARWGraphNode start_node;
        ARWGraphNode end_node;

        public PaintSurface(HashMap<Integer, LinkedList<Shape>> shapes, PrefabManager prefabManager, WHGraph.Graphs.ARWGraph ARWGraph) {
            this.shapes = shapes;
            drawables = new LinkedList<>();
            this.ARWGraph = ARWGraph;
            //WALLS
            Shape r = makeLine(0, scale(prefabManager.getDepth()), scale(prefabManager.getWidth()),
                    scale(prefabManager.getDepth()));
            drawables.add(r);
            r = makeLine(scale(prefabManager.getWidth()), 0, scale(prefabManager.getWidth()),
                    scale(prefabManager.getDepth()));
            drawables.add(r);


            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startDrag = new Point(e.getX(), e.getY());
                    endDrag = startDrag;
                    if (node_action == Node_Action.REMOVE) {
                        ARWGraphNode node = ARWGraph.findClosestNode(prefabManager.getWidth()-descale(e.getX()),
                                descale(e.getY()), descale(LINE_PIXEL_SENSIBILITY));
                        if (node != null) {
                            ARWGraph.removeNode(node);
                        }
                    } else if (node_action == Node_Action.GENERATE_PRODUCT) {
                        find_rack_gen_product(e.getX(), e.getY());
                    }
                    repaint();
                }

                public void mouseReleased(MouseEvent e) {
                    if (startDrag.x == e.getX() && startDrag.y == e.getY() || node_action == Node_Action.REMOVE || node_action == Node_Action.GENERATE_PRODUCT) {
                        if (line_type == Line_Type.PRODUCT && startDrag.x == e.getX() && startDrag.y == e.getY()) {
                            ARWGraphNode node = ARWGraph.findClosestNode(prefabManager.getWidth()-descale(startDrag.x),
                                    descale(startDrag.y), descale(LINE_PIXEL_SENSIBILITY * 2));
                            if (node != null)
                                node.setContains_product(true);
                        }
                    } else {
                        ARWGraphNode node = ARWGraph.findClosestNode(prefabManager.getWidth()-descale(startDrag.x),
                                descale( startDrag.y),descale( LINE_PIXEL_SENSIBILITY * 2));
                        if (node == null) {
                            ARWGraph.createGraphNode((prefabManager.getWidth()- descale(startDrag.x)),
                                    descale(startDrag.y), GraphNodeType.SIMPLE);
                            start_node = ARWGraph.getLastNode();
                        } else {
                            start_node = node;
                            startDrag = new Point(scale(prefabManager.getWidth()-start_node.getLocation().getX()),
                                    scale(start_node.getLocation().getY()));
                        }

                        int x1 = startDrag.x;
                        int y1 = startDrag.y;
                        int x2 = e.getX();
                        int y2 = e.getY();
                        if (Math.abs(startDrag.x - e.getX()) < LINE_PIXEL_SENSIBILITY) {
                            x2 = x1;
                        }
                        if (Math.abs(y1 - y2) < LINE_PIXEL_SENSIBILITY) {
                            y2 = y1;
                        }
                        endDrag.x = x2;
                        endDrag.y = y2;

                        node = ARWGraph.findClosestNode(prefabManager.getWidth()-descale(endDrag.x), descale(endDrag.y),
                                descale(LINE_PIXEL_SENSIBILITY * 2));
                        if (node == null) {
                            ARWGraph.createGraphNode((prefabManager.getWidth()-descale(endDrag.x)),
                                    descale(endDrag.y), line_type == Line_Type.EXIT ? GraphNodeType.EXIT : GraphNodeType.SIMPLE);
                            end_node = ARWGraph.getLastNode();
                        } else {
                            end_node = node;
                            endDrag.x = scale( prefabManager.getWidth()-end_node.getLocation().getX());
                            endDrag.y = scale(end_node.getLocation().getY());
                            node.setType(line_type == Line_Type.EXIT ? GraphNodeType.EXIT : GraphNodeType.SIMPLE);
                            endDrag = new Point(scale( prefabManager.getWidth()-end_node.getLocation().getX()),
                                    scale(end_node.getLocation().getY()));
                        }
                        ARWGraph.makeNeighbors(start_node, end_node, line_type == Line_Type.PRODUCT);
                        //Shape r = makeLine(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                        //drawables.add(r);
                        startDrag = null;
                        endDrag = null;
                        repaint();
                    }
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    endDrag = new Point(e.getX(), e.getY());
                    repaint();
                }
            });
        }

        private void find_rack_gen_product(int x, int y) {
            Rack rack = prefabManager.findRack((int)(prefabManager.getWidth()-descale(x)),(int)descale(y), descale(LINE_PIXEL_SENSIBILITY));
            if (rack != null) {
                int new_x = Math.round((int) rack.getShape().getBounds().getCenterX());
                int new_y = Math.round((int) rack.getShape().getBounds().getCenterY());
                int id = Integer.parseInt(rack.getCode().split("RC")[1]);
                ARWGraph.createGraphNodeOnClosestEdge(new ARWGraphNode(id, new_x, new_y, GraphNodeType.PRODUCT));
            }
        }

        private void paintBackground(Graphics2D g2) {
            g2.setPaint(Color.LIGHT_GRAY);
            for (int i = 0; i < getSize().width; i += 10) {
                Shape line = new Line2D.Float(i, 0, i, getSize().height);
                g2.draw(line);
            }

            for (int i = 0; i < getSize().height; i += 10) {
                Shape line = new Line2D.Float(0, i, getSize().width, i);
                g2.draw(line);
            }


        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paintBackground(g2);
            Color[] colors = {Color.CYAN, Color.LIGHT_GRAY};
            int colorIndex = 0;

            g2.setStroke(new BasicStroke(2));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            for (Map.Entry<Integer, LinkedList<Shape>> entry : shapes.entrySet()) {
                Integer color_index = entry.getKey();
                LinkedList<Shape> shapes = entry.getValue();
                for (Shape s : shapes) {
                    g2.setPaint(Color.BLACK);
                    g2.draw(s);
                    g2.setPaint(colors[color_index]);
                    g2.fill(s);
                }
            }


            for (Shape s : drawables) {
                g2.setPaint(Color.BLACK);
                g2.draw(s);
                //g2.setPaint(Color.RED);
                g2.fill(s);
            }
            for (ARWGraphNode node : ARWGraph.getGraphNodes()) {
                g2.setPaint(Color.BLACK);
                if (node.contains_product())
                    g2.setPaint(Color.BLUE);
                g2.drawOval(scale(prefabManager.getWidth()-node.getLocation().getX()) - (NODE_SIZE / 2),
                        scale( node.getLocation().getY() )- (NODE_SIZE / 2), NODE_SIZE, NODE_SIZE);
                g2.drawString(node.printName(), scale( prefabManager.getWidth()-node.getLocation().getX()) + (NODE_SIZE),
                        scale( node.getLocation().getY()) - (NODE_SIZE));
            }
            for (Edge e : ARWGraph.getEdges()) {
                Shape r = makeLine(scale(prefabManager.getWidth()-e.getStart().getLocation().getX()), scale( e.getStart().getLocation().getY()),
                        scale(prefabManager.getWidth()-e.getEnd().getLocation().getX()), scale(e.getEnd().getLocation().getY()));
                g2.setPaint(e.isProduct_line() ? Color.BLUE : Color.DARK_GRAY);
                g2.draw(r);
            }

            if (startDrag != null && endDrag != null) {
                g2.setPaint(Color.DARK_GRAY);
                Shape r = makeLine(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                g2.draw(r);
            }
        }

        private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
            return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        }

        private Line2D.Float makeLine(int x1, int y1, int x2, int y2) {
            if (isSensibleX(x1, x2)) {
                x2 = x1;
            }
            if (isSensibleY(y1, y2)) {
                y2 = y1;
            }
            return new Line2D.Float(x1, y1, x2, y2);
        }

        private boolean isSensibleX(int x1, int x2) {
            return Math.abs(x1 - x2) < LINE_PIXEL_SENSIBILITY;
        }

        private boolean isSensibleY(int y1, int y2) {
            return Math.abs(y1 - y2) < LINE_PIXEL_SENSIBILITY;
        }

    }

    private enum Node_Action {
        DRAW, REMOVE, GENERATE_PRODUCT
    }

    public double posx(Prefab prefab){
        //Inverte a origem das coordenadas se no modelo for o canto superior direito
        return prefabManager.getWidth()-prefab.getPosition().getX();
        //return prefab.getPosition().getX()
    }


    public HashMap<Integer, LinkedList<Shape>> generateShapes(LinkedList<Prefab> allPrefabs) {
        HashMap<Integer, LinkedList<Shape>> shapes = new HashMap<>();
        LinkedList<Shape> racks = new LinkedList<>();
        LinkedList<Shape> structures = new LinkedList<>();

        for (Prefab prefab : allPrefabs) {
            //Está a desenhar da esquerda  para a direita, pelo que é necessário corrigir a posição
            //inicial : ver se dá para desenhar o retangulo ao contrário, o que tornaria o
            //código mais imune.
            Rectangle2D rec = new Rectangle(scale(posx(prefab)-prefab.getSize().getX()),
                    scale(prefab.getPosition().getY()),
                    scale(prefab.getSize().getX()), scale(prefab.getSize().getY()));
            if (prefab.getRotation().hasZvalue()) {
                AffineTransform tx = new AffineTransform();
                //Nas áreas rodadas a posicao indica o canto inferior esquerdo, nao sendo necessario
                //corrigir.
                tx.rotate(- Math.toRadians(prefab.getRotation().getZ()), scale(posx(prefab)),
                //tx.rotate(Math.toRadians(prefab.getRotation().getZ()), scale(prefab.getPosition().getX()),
                        scale(prefab.getPosition().getY()));
                Shape newShape = tx.createTransformedShape(rec);
                if (prefab instanceof Rack) {
                    racks.add(newShape);
                }
                if (prefab instanceof Structure) {
                    structures.add(newShape);
                }
                prefab.setShape(newShape);
            } else {
                if (prefab instanceof Rack) {
                    racks.add(rec);
                }
                if (prefab instanceof Structure) {
                    structures.add(rec);
                }
                prefab.setShape(rec);
            }
        }
        shapes.put(PREFAB_RACK, racks);
        shapes.put(PREFAB_STRUCTURE, structures);
        return shapes;
    }


    public int scale(double measure){
        return (int) ((measure)*AMPLIFY);
    }


    public float descale(int measure){
        return (float) measure/AMPLIFY;
    }


    public void fillAllPrefabs() {
        allPrefabs = new LinkedList<>();
        allPrefabs.addAll(prefabManager.getRacks());
        allPrefabs.addAll(prefabManager.getStructures());
        allPrefabs.addAll(prefabManager.getDevices());
        allPrefabs.addAll(prefabManager.getDevices());
    }

    public LinkedList<Prefab> getAllPrefabs() {
        return allPrefabs;
    }

}
