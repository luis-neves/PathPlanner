package whgraph;

import newWarehouse.Warehouse;

import orderpicking.GNode;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class GraphGenerator {
    private final Warehouse warehouse;
    private final ArrayList<GNode> startnodes;
    private final Rectangle2D.Float paredes;
    private final float corridorwidth;

    public GraphGenerator(Warehouse warehouse, ArrayList<GNode> startnodes, float corridorwidth) {
        this.warehouse = warehouse;
        this.startnodes = startnodes;
        this.corridorwidth = corridorwidth;
        paredes = new Rectangle2D.Float(0, 0, warehouse.getArea().x, warehouse.getArea().y);
    }


    public ArrayList<Point2D.Float> generateIntersections(){
        ArrayList<Shape> shapes = warehouse.generateShapes(corridorwidth);
        ArrayList<Point2D.Float> pontos = new ArrayList<>();
        Area walls= new Area(paredes);

        //Gera pontos em todos os vértices das áreas expandidas
        for(int i=0; i<shapes.size();i++){

               Area recti=new Area(shapes.get(i));
                pontos.add(new Point2D.Float((float)recti.getBounds2D().getMinX(), (float)recti.getBounds2D().getMinY()));
                pontos.add(new Point2D.Float((float)recti.getBounds2D().getMinX(), (float)recti.getBounds2D().getMaxY()));
                pontos.add(new Point2D.Float((float)recti.getBounds2D().getMaxX(), (float)recti.getBounds2D().getMinY()));
                pontos.add(new Point2D.Float((float)recti.getBounds2D().getMaxX(), (float)recti.getBounds2D().getMaxY()));
               if (walls.intersects(recti.getBounds2D())){
                   //E na intersecção das áreas expandidas com as paredes
                   Rectangle2D rect = walls.getBounds2D().createIntersection(recti.getBounds2D());
                   pontos.add(new Point2D.Float((float)rect.getMinX(), (float)rect.getMinY()));
                   pontos.add(new Point2D.Float((float)rect.getMinX(), (float)rect.getMaxY()));
                   pontos.add(new Point2D.Float((float)rect.getMaxX(), (float)rect.getMinY()));
                   pontos.add(new Point2D.Float((float)rect.getMaxX(), (float)rect.getMaxY()));

               }
               for (int j=i+1; j<shapes.size();j++){
                   //E na intersecção de cada área expandida com as restantes
                   Area rectj=new Area(shapes.get(j));
                   if (recti.intersects(rectj.getBounds2D())){
                       Rectangle2D rect = recti.getBounds2D().createIntersection(rectj.getBounds2D());
                       pontos.add(new Point2D.Float((float)rect.getMinX(), (float)rect.getMinY()));
                       pontos.add(new Point2D.Float((float)rect.getMinX(), (float)rect.getMaxY()));
                       pontos.add(new Point2D.Float((float)rect.getMaxX(), (float)rect.getMinY()));
                       pontos.add(new Point2D.Float((float)rect.getMaxX(), (float)rect.getMaxY()));

                   }
               }

        }

        //Elimina pontos redundantes
        Set<Point2D.Float> conjunto = new LinkedHashSet<Point2D.Float>();
        conjunto.addAll((Collection<? extends Point2D.Float>) pontos);
        pontos.clear();
        pontos.addAll((Collection<? extends Point2D.Float>) conjunto);

        //Elimina todos os pontos gerados em zonas proibidas
        shapes = warehouse.generateShapes(corridorwidth/2);
        for (int i=0; i<shapes.size();i++){
            Rectangle2D recti=new Area(shapes.get(i)).getBounds2D();
            for (int j=0; j< pontos.size();j++) {
                Point2D.Float ponto = pontos.get(j);
                if ((ponto.x>recti.getMinX())&&(ponto.x<recti.getMaxX())&&
                        (ponto.y>recti.getMinY())&&(ponto.y<recti.getMaxY())){
                    pontos.remove(j);
                    j--;
                }
            }
        }

        //Elimina todos os pontos gerados fora da sala
        for (int j=0; j< pontos.size();j++) {
            Point2D.Float ponto = pontos.get(j);
            if ((ponto.x<paredes.getMinX())||(ponto.x>paredes.getMaxX())||
                    (ponto.y<paredes.getMinY())||(ponto.y>paredes.getMaxY())) {
                pontos.remove(j);
                j--;
            }
        }

        //Funde os pontos demasiado próximos, gerando um único ponto com coordenadas intermédias.
        for (int i=0; i<pontos.size();i++)
            for(int j=i+1;j< pontos.size(); j++ ){
                if (pontos.get(i).distance(pontos.get(j))<corridorwidth/2){
                    pontos.get(i).x=(pontos.get(i).x+pontos.get(j).x)/2;
                    pontos.get(i).y=(pontos.get(i).y+pontos.get(j).y)/2;
                    pontos.remove(j);
                    j--;
                }


            }


        return pontos;
    }

    class LineComparator implements Comparator<Line2D>
    {
        @Override
        public int compare(Line2D o1, Line2D o2) {

             Float d1=new Float(o1.getP1().distance(o1.getP2()));
            Float d2=new Float(o2.getP1().distance(o2.getP2()));

            return d1.compareTo(d2);
        }
    }

    public boolean cruzam(Line2D.Float l1, Line2D.Float l2){

        if (l1.intersectsLine(l2)){

            return !(((Math.abs(l1.x1-l2.x1)<0.01)&&(Math.abs(l2.y1-l1.y1)<0.01))||
                    ((Math.abs(l1.x2-l2.x1)<0.01)&&(Math.abs(l1.y2-l2.y1)<0.01))||
                    ((Math.abs(l1.x1-l2.x2)<0.01)&&(Math.abs(l1.y1-l2.y2)<0.01))||
                    ((Math.abs(l1.x2-l2.x2)<0.01)&&(Math.abs(l1.y2-l2.y2)<0.01)));
        }
        else
            return false;

    }

    public boolean quaseparalelas(Line2D.Float l1, Line2D.Float l2){


        double cosx=(((l2.y2-l2.y1)*(l1.y2-l1.y1))+((l2.x2-l2.x1)*(l1.x2-l1.x1)))/
                (l2.getP1().distance(l2.getP2())*l1.getP1().distance(l1.getP2()));

        if (cosx>0.985)
        {
            Point2D comum, ext1, ext2;

            if (l1.getP1().equals(l2.getP1())){
                comum=l1.getP1();
                ext1=l1.getP2();
                ext2=l2.getP2();
            }else if (l1.getP1().equals(l2.getP2())){
                comum=l1.getP1();
                ext1=l1.getP2();
                ext2=l2.getP1();
            }else if (l1.getP2().equals(l2.getP1())) {
                comum = l1.getP2();
                ext1 = l1.getP1();
                ext2 = l2.getP2();
            } else if (l1.getP2().equals(l2.getP2())){
                comum = l1.getP2();
                ext1 = l1.getP1();
                ext2 = l2.getP1();
            } else
                return false;
            return (ext1.distance(ext2)<Math.max(comum.distance(ext1),comum.distance(ext2)));
        }
        else
            return false;

    }


    public ARWGraph createGraph(){
        ArrayList<Point2D.Float> pontos= generateIntersections();
        ArrayList<Shape> shapes = warehouse.generateShapes(corridorwidth/2);
        ArrayList<Line2D.Float> linhas=new ArrayList();

        ARWGraph arwGraph = new ARWGraph();

        for (GNode node: startnodes) {
            pontos.add(new Point2D.Float((float)node.getX(),(float)node.getY()));
        }

        for (int i=0; i<pontos.size(); i++){
            for (int j=i+1; j<pontos.size();j++){
                Line2D.Float linha = new Line2D.Float(pontos.get(i),pontos.get(j));
                boolean intersetou=false;
                for (Shape shape: shapes) {
                    if (linha.intersects(shape.getBounds2D())) {
                        intersetou = true;
                        break;
                    }
                }
                if (!intersetou)
                    linhas.add(linha);

            }

        }
        linhas.sort(new LineComparator());

        ArrayList<Line2D.Float> selectedlines=new ArrayList<>();
        selectedlines.add(linhas.get(0));

        ARWGraphNode start = new ARWGraphNode(arwGraph.getNumberOfNodes(),linhas.get(0).x1, linhas.get(0).y1,  GraphNodeType.SIMPLE);
        arwGraph.createGraphNode(start);
        ARWGraphNode end = new ARWGraphNode(arwGraph.getNumberOfNodes(),linhas.get(0).x2, linhas.get(0).y2,  GraphNodeType.SIMPLE);
        arwGraph.createGraphNode(end);
        arwGraph.createEdge(new Edge(start,end,1,arwGraph.getNumberOfEdges() ));

        for (int i=1; i< linhas.size();i++){
            boolean intersetou=false;
            Line2D.Float linhatual=linhas.get(i);
            System.out.println("Comp linha="+linhatual.getP1().distance(linhatual.getP2()));
            for (int j=0;j<selectedlines.size();j++){
                if (cruzam(linhatual,selectedlines.get(j))
                       || quaseparalelas(linhatual,selectedlines.get(j))
                )
                {
                    intersetou = true;
                    break;
                }
            }
            if (!intersetou){
                start = arwGraph.findClosestNode(linhatual.x1, linhatual.y1, 0.05f);
                if (start==null) {
                    start = new ARWGraphNode(arwGraph.getNumberOfNodes(), linhatual.x1, linhatual.y1, GraphNodeType.SIMPLE);
                    arwGraph.createGraphNode(start);
                }
                end = arwGraph.findClosestNode(linhatual.x2, linhatual.y2, 0.05f);
                if (end==null) {
                    end = new ARWGraphNode(arwGraph.getNumberOfNodes(), linhatual.x2, linhatual.y2, GraphNodeType.SIMPLE);
                    arwGraph.createGraphNode(end);
                }
                arwGraph.createEdge(new Edge(start,end,1,arwGraph.getNumberOfEdges()));
                selectedlines.add(linhatual);
            }


        }
        return arwGraph;
    }
}




