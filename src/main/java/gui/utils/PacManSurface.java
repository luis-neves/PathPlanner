package gui.utils;

import WHGraph.Graphs.ARWGraphNode;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


public class PacManSurface extends JComponent  /*LayerUI<JPanel> */{

    Point2D.Float start_node;
    Point2D.Float current_node;
    Hashtable<String,Point2D.Float> operadores;
    Hashtable<String,Point2D.Float> produtos;

    BackgroundSurface background;
    int node_size;

    public PacManSurface(BackgroundSurface background, int node_size) {
        this.start_node=new Point2D.Float((float)1.0,(float)0.0);
        this.current_node=start_node;
        this.background=background;
        this.node_size=node_size;
        operadores=new Hashtable<>();
        produtos=new Hashtable<>();
    }

    public void addAgent(String agentid, Point2D.Float node){
        operadores.put(agentid,node);
    }

    public void removeAgent(String agentid){
        operadores.remove(agentid);
    }

    public void updateAgent(String agentid, Point2D.Float node){
        addAgent(agentid,node);
    }

    public void addProduct(String productid, Point2D.Float node){
        produtos.put(productid,node);
    }

    public void removeProduct(String productid){
        produtos.remove(productid);
    }

    public void setCurrent_node(Point2D.Float node){
        current_node=node;
    }
/*
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
    public void paint(Graphics g, JComponent c) {*/

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        //super.paint(g2, c);
        g2.setPaint(Color.BLUE);

        Set<String> keys = operadores.keySet();

        //Obtaining iterator over set entries
        Iterator<String> itr = keys.iterator();

        //Displaying Key and value pairs
        while (itr.hasNext()) {
            // Getting Key
            String agentid = itr.next();
            Point2D.Float pos=operadores.get(agentid);
            g2.drawString(agentid.substring(0,3),background.scale(background.posx(pos.getX())),
                    background.scale(pos.getY()));
            g2.fillOval(background.scale(background.posx(pos.getX())),
                    background.scale(pos.getY()), node_size, node_size);
        }

        keys=produtos.keySet();
        for (String productid: keys){
            Point2D.Float pos=produtos.get(productid);
            g2.setPaint(Color.GREEN);
            g2.fillOval(background.scale(background.posx(pos.getX())),
                    background.scale(pos.getY()), 3*node_size/4, 3*node_size/4);
        }
    }


}