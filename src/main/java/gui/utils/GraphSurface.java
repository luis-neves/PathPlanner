package gui.utils;

import newwarehouse.Warehouse;
import whgraph.ARWGraph;
import whgraph.ARWGraphNode;
import whgraph.Edge;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.geom.Line2D;

public class GraphSurface extends LayerUI<JPanel> {
    private ARWGraph arwgraph;
    private double SENSIBILITY;
    private final int NODE_SIZE;
    Point startDrag, endDrag;
    public float AMPLIFY;
    public Warehouse warehouse;


    public GraphSurface(ARWGraph ARWGraph, Warehouse warehouse, double sensibility, int node_size, float amplify) {
        this.arwgraph = ARWGraph;
        this.SENSIBILITY = sensibility;
        this.NODE_SIZE=node_size;
        this.warehouse =warehouse;
        this.AMPLIFY=amplify;
    }

    public GraphSurface(){
     NODE_SIZE=5;
    }

    public void setArwgraph(ARWGraph arwgraph) {
        this.arwgraph = arwgraph;
    }

    public void setPrefabManager(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.arwgraph.clear();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();

            super.paint(g2, c);
            if ((warehouse !=null)&&(arwgraph!=null) ) {
                this.AMPLIFY = Math.min(((float) c.getSize().width) / warehouse.getWidth(), ((float) c.getSize().height) / warehouse.getDepth());

                if (arwgraph != null) {
                    for (ARWGraphNode node : arwgraph.getGraphNodes()) {
                        g2.setPaint(Color.BLACK);
                        if (node.contains_product())
                            g2.setPaint(Color.BLUE);
                        g2.drawOval(scale(warehouse.getWidth() - node.getLocation().getX()) - (NODE_SIZE / 2),
                                scale(node.getLocation().getY()) - (NODE_SIZE / 2), NODE_SIZE, NODE_SIZE);
                        g2.drawString(node.printName(), scale(warehouse.getWidth() - node.getLocation().getX()) + (NODE_SIZE),
                                scale(node.getLocation().getY()) - (NODE_SIZE));
                    }
                    for (Edge e : arwgraph.getEdges()) {
                        Shape r = makeLine(scale(warehouse.getWidth() - e.getStart().getLocation().getX()), scale(e.getStart().getLocation().getY()),
                                scale(warehouse.getWidth() - e.getEnd().getLocation().getX()), scale(e.getEnd().getLocation().getY()));
                        g2.setPaint(Color.DARK_GRAY);
                        g2.draw(r);
                    }

                }
            }
     //   }
    }


    public Line2D.Float makeLine(int x1, int y1, int x2, int y2) {
        if (isSensibleX(x1, x2)) {
            x2 = x1;
        }
        if (isSensibleY(y1, y2)) {
            y2 = y1;
        }
        return new Line2D.Float(x1, y1, x2, y2);
    }

    private boolean isSensibleX(int x1, int x2) {
        return Math.abs(x1 - x2) < scale(SENSIBILITY);
    }

    private boolean isSensibleY(int y1, int y2) {
        return Math.abs(y1 - y2) < scale(SENSIBILITY);
    }


    public int scale(double measure){
        return (int) ((measure)*AMPLIFY);
    }


    public float descale(int measure){
        return (float) measure/AMPLIFY;
    }


}
