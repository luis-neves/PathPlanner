package gui.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Hashtable;


public class PacManSurface extends JComponent  {

    Point2D.Float start_node;
    Hashtable<String,Point2D.Float> operadores;
    Hashtable<String,Point2D.Float> produtos;

    BackgroundSurface background;
    int node_size;

    public PacManSurface(BackgroundSurface background, int node_size) {
        this.start_node=new Point2D.Float((float)1.0,(float)0.0);
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


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        //super.paint(g2, c);
        g2.setPaint(Color.BLUE);

        //Displaying Key and value pairs
        for(String agentid: operadores.keySet()){

            Point2D.Float pos=operadores.get(agentid);
            g2.drawString(agentid.substring(0,3),background.scale(background.posx(pos.getX())),
                    background.scale(pos.getY()));
            g2.fillOval(background.scale(background.posx(pos.getX()))-node_size/2,
                    background.scale(pos.getY())-node_size/2, node_size, node_size);
        }

        for (String productid: produtos.keySet()){
            Point2D.Float pos=produtos.get(productid);
            g2.setPaint(Color.GREEN);
            g2.fillOval(background.scale(background.posx(pos.getX())),
                    background.scale(pos.getY()), 3*node_size/4, 3*node_size/4);
        }
    }


}