package gui.utils;


import WHDataStruct.Prefab;
import WHDataStruct.PrefabManager;
import WHDataStruct.Rack;
import WHDataStruct.Structure;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BackgroundSurface extends JPanel {
    private HashMap<Integer, LinkedList<Shape>> shapes;
    private LinkedList<Shape> drawables;
    private static final Integer PREFAB_RACK = 0;
    private static final Integer PREFAB_STRUCTURE = 1;
    private final double SENSIBILITY = 0.5;//m
    private int width;
    private int height;
    public float AMPLIFY;
    PrefabManager prefabManager;

    public BackgroundSurface(PrefabManager prefabmanager, int width, int height) {
        this.shapes = shapes;

        this.prefabManager=prefabmanager;
        this.width=width;
        this.height=height;
        this.AMPLIFY=Math.min(((float)width)/prefabManager.getWidth(),((float)height)/prefabManager.getDepth());


        shapes = generateShapes();

    }

    public BackgroundSurface() {
        this.shapes = new HashMap<Integer, LinkedList<Shape>>();
        this.AMPLIFY=new Float(1);
        this.prefabManager=null;

    }

    public void setPrefabs(PrefabManager prefabmanager){
        this.prefabManager = prefabmanager;
        this.AMPLIFY=Math.min((float)width/prefabmanager.getWidth(),(float)height/prefabmanager.getDepth());
        shapes=generateShapes();

        repaint();
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
        if (prefabManager!=null) {
            this.AMPLIFY = Math.min(((float) getSize().width) / prefabManager.getWidth(), ((float) getSize().height) / prefabManager.getDepth());
            shapes = generateShapes();
            drawables = new LinkedList<>();
            Shape r = makeLine(0, scale(prefabManager.getDepth()), scale(prefabManager.getWidth()),
                    scale(prefabManager.getDepth()));
            drawables.add(r);
            r = makeLine(scale(prefabManager.getWidth()), 0, scale(prefabManager.getWidth()),
                    scale(prefabManager.getDepth()));
            drawables.add(r);

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            paintBackground(g2);
            Color[] colors = {Color.CYAN, Color.LIGHT_GRAY};

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
                g2.fill(s);
            }
        }

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


    public HashMap<Integer, LinkedList<Shape>> generateShapes() {
        HashMap<Integer, LinkedList<Shape>> shapes = new HashMap<>();
        LinkedList<Shape> racks = new LinkedList<>();
        LinkedList<Shape> structures = new LinkedList<>();
        LinkedList<Prefab> allprefabs = prefabManager.getAllPrefabs();
        for (Prefab prefab : allprefabs) {
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

                tx.rotate(Math.toRadians(prefab.getRotation().getZ()), scale(posx(prefab)),
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
    public double posx(Prefab prefab){
        //Inverte a origem das coordenadas se no modelo for o canto superior direito
        return prefabManager.getWidth()-prefab.getPosition().getX();
        //return prefab.getPosition().getX()
    }
}
