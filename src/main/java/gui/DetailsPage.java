package gui;

import utils.Graphs.FitnessNode;
import utils.Graphs.Graph;
import utils.Graphs.GraphNode;
import utils.Graphs.GraphNodeType;
import utils.warehouse.PrefabManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DetailsPage extends JFrame {

    private static final int GRID_TO_PANEL_GAP = 20;
    private static final int MAX_WIDTH = 1600;
    private static final int MAX_HEIGHT = 800;
    private static final int LINE_PIXEL_SENSIBILITY = 10;
    private static final int NODE_SIZE = 5;

    PrefabManager prefabManager;

    public DetailsPage(PrefabManager prefabManager) throws HeadlessException {
        this.prefabManager = prefabManager;
        this.setTitle("Details");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
        prefabManager.fixSizesToInteger();
        //prefabManager.changeAxis();
        //prefabManager.fixRotation(360);
        HashMap<Integer, LinkedList<Shape>> shapes = prefabManager.generateShapes();
        setLayout(new BorderLayout());
        add(new PaintSurface(shapes, prefabManager), BorderLayout.CENTER);
        this.setVisible(true);

    }

    public class DrawPanel extends JPanel {
        private LinkedList<Shape> shapes;

        public DrawPanel(LinkedList<Shape> shapes) {
            this.shapes = shapes;
        }// draws an x from the corners of the panel

        public void paintComponent(Graphics g) {
            // calls paintComponent to ensure the panel displys correctly
            super.paintComponent((Graphics2D) g);
            int width = getWidth();
            g = (Graphics2D) g;
            int height = getHeight();

            for (Shape shape : shapes) {
                ((Graphics2D) g).draw(shape);
            }
        } // end method paintComponent
    } // end class DrawPanel


    private class PaintSurface extends JComponent {
        private HashMap<Integer, LinkedList<Shape>> shapes;
        private LinkedList<Shape> drawables;
        private Graph graph;

        Point startDrag, endDrag;
        GraphNode start_node;
        GraphNode end_node;

        public PaintSurface(HashMap<Integer, LinkedList<Shape>> shapes, PrefabManager prefabManager) {
            this.shapes = shapes;
            drawables = new LinkedList<>();
            graph = new Graph();
            //WALLS
            Shape r = makeLine(0, Math.round(prefabManager.config.getDepth()) , Math.round(prefabManager.config.getWidth()), Math.round(prefabManager.config.getDepth()));
            drawables.add(r);
            r = makeLine(Math.round(prefabManager.config.getWidth()), 0 , Math.round(prefabManager.config.getWidth()), Math.round(prefabManager.config.getDepth()));
            drawables.add(r);


            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    startDrag = new Point(e.getX(), e.getY());
                    endDrag = startDrag;
                    repaint();
                }

                public void mouseReleased(MouseEvent e) {
                    if (startDrag.x == e.getX() && startDrag.y == e.getY()) {

                    } else {
                        GraphNode node = graph.findClosestNode(startDrag.x, startDrag.y, LINE_PIXEL_SENSIBILITY * 2);
                        if (node == null) {
                            graph.createGraphNode(startDrag.x, startDrag.y, GraphNodeType.SIMPLE);
                            start_node = graph.getLastNode();
                        } else {
                            start_node = node;
                            startDrag = new Point((int) start_node.getLocation().getX(), (int) start_node.getLocation().getY());
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

                        node = graph.findClosestNode(endDrag.x, endDrag.y, LINE_PIXEL_SENSIBILITY*2);
                        if (node == null) {
                            graph.createGraphNode(endDrag.x, endDrag.y, GraphNodeType.SIMPLE);
                            end_node = graph.getLastNode();
                        } else {
                            endDrag.x = (int) end_node.getLocation().getX();
                            endDrag.y = (int) end_node.getLocation().getY();
                            end_node = node;
                            endDrag = new Point((int) end_node.getLocation().getX(), (int) end_node.getLocation().getY());
                        }

                        Shape r = makeLine(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
                        drawables.add(r);
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
            Color[] colors = {Color.GREEN, Color.LIGHT_GRAY};
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
            //width = x
            //depth = y

            for (Shape s : drawables) {
                g2.setPaint(Color.BLACK);
                g2.draw(s);
                //g2.setPaint(Color.RED);
                g2.fill(s);
            }
            for (GraphNode node : graph.getGraphNodes()) {
                g2.drawOval((int) node.getLocation().getX() - (NODE_SIZE / 2), (int) node.getLocation().getY() - (NODE_SIZE / 2), NODE_SIZE, NODE_SIZE);
                g2.drawString(node.printName(), (int) node.getLocation().getX() + (NODE_SIZE), (int) node.getLocation().getY() - (NODE_SIZE));
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
}
