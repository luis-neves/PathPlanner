package gui;

import utils.warehouse.PrefabManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class DetailsPage extends JFrame {

    private static final int GRID_TO_PANEL_GAP = 20;
    private static final int MAX_WIDTH = 1600;
    private static final int MAX_HEIGHT = 800;

    PrefabManager prefabManager;
    Image image;
    Graphics2D gfx;
    DrawPanel environmentPanel;

    public DetailsPage(PrefabManager prefabManager) throws HeadlessException {
        this.prefabManager = prefabManager;
        this.setTitle("Details");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
        this.setVisible(true);
        prefabManager.fixSizesToInteger();
        //prefabManager.changeAxis();
        LinkedList<Shape> shapes = prefabManager.generateShapes();
        prefabManager.fixRotation();
        environmentPanel = new DrawPanel(shapes);
        environmentPanel.setSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
        environmentPanel.setBackground(Color.white);
        setLayout(new BorderLayout());
        add(environmentPanel, BorderLayout.CENTER);
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

}
