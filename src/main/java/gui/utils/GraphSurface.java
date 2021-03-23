package gui.utils;

import WHDataStruct.PrefabManager;
import WHGraph.Graphs.ARWGraph;
import WHGraph.Graphs.ARWGraphNode;
import WHGraph.Graphs.Edge;
import WHGraph.Graphs.GraphNodeType;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

public class GraphSurface extends LayerUI<JPanel> {
    private ARWGraph arwgraph;
    private double SENSIBILITY;
    private final int NODE_SIZE;
    Point startDrag, endDrag;
    public float AMPLIFY;
    public PrefabManager prefabManager;


    public GraphSurface(ARWGraph ARWGraph, PrefabManager prefabmanager, double sensibility, int node_size, float amplify) {
        this.arwgraph = ARWGraph;
        this.SENSIBILITY = sensibility;
        this.NODE_SIZE=node_size;
        this.prefabManager=prefabmanager;
        this.AMPLIFY=amplify;
    }

    public GraphSurface(){
     NODE_SIZE=5;
    }

    public void setArwgraph(ARWGraph arwgraph) {
        this.arwgraph = arwgraph;
    }

    public void setPrefabManager(PrefabManager prefabManager) {
        this.prefabManager = prefabManager;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer jlayer = (JLayer) c;
    }

    @Override
    public void uninstallUI(JComponent c) {
        JLayer jlayer = (JLayer) c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();

            super.paint(g2, c);
            if ((prefabManager!=null)&&(arwgraph!=null) ) {
                this.AMPLIFY = Math.min(((float) c.getSize().width) / prefabManager.getWidth(), ((float) c.getSize().height) / prefabManager.getDepth());

                if (arwgraph != null) {
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
