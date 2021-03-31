package whgraph;

import newwarehouse.Warehouse;

import orderpicking.GNode;

import java.awt.geom.*;
import java.util.*;

public class GraphGenerator {
    private final Warehouse warehouse;
    private final ArrayList<GNode> startnodes;
    private final ArrayList<Area> areas;
    private final Rectangle2D.Float paredes;
    private final float corridorwidth;

    public GraphGenerator(Warehouse warehouse, ArrayList<GNode> startnodes, float corridorwidth) {
        this.warehouse = warehouse;
        this.startnodes=startnodes;
        this.corridorwidth = corridorwidth;
        areas=new ArrayList();
        fillAreas();
        paredes=new Rectangle2D.Float(0,0,warehouse.getWidth(),warehouse.getDepth());
    }

    public void fillAreas(){
      /*  LinkedList<Prefab> allprefabs = warehouse.getAllPrefabs();
        Area area;
        for (NPrefab prefab: allprefabs){
            Rectangle2D.Float rec = new Rectangle.Float(prefab.getPosition().x,prefab.getPosition().y,
                    prefab.getSize().getX(),prefab.getSize().getX() );
            area=new Area(rec);
            if (prefab.rotation!=0) {
                AffineTransform tx = new AffineTransform();

                tx.rotate(-Math.toRadians(prefab.getRotation().getZ()), prefab.getPosition().getX(),
                        prefab.getPosition().getY());

                 area.transform(tx);

            }
            areas.add(area);
        }*/
    }


    public ArrayList<Point2D.Float> intersect(Area area1, Area area2 ){
        ArrayList<Point2D.Float> corners = new ArrayList<Point2D.Float>();
        Rectangle2D aux=area1.getBounds2D();
        Rectangle2D aux2=aux.createIntersection(area2.getBounds2D());

        return corners(new Area(aux2));
    }

    public ArrayList<Point2D.Float> corners(Area area){
        ArrayList<Point2D.Float> corners = new ArrayList<Point2D.Float>();

        float[] coords = new float[6];
        PathIterator pi=area.getBounds2D().getPathIterator(null, 1.0);
        while (pi.isDone()==false){
            int k=pi.currentSegment(coords);
            corners.add(new Point2D.Float(coords[0],coords[1]));
            pi.next();
        }

        return corners;
    }

    public Area grow(Area area){
        Area aux=new Area(area);
        AffineTransform tx = new AffineTransform();
        float escala= (float) ((float)1.0+corridorwidth/area.getBounds2D().getWidth());
        tx.scale(escala,escala);
        tx.translate(-corridorwidth/2,-corridorwidth/2);
        aux.transform(tx);
        return aux;
    }

    public ARWGraph CreateNodes(){

        ARWGraph grafo = new ARWGraph();
        ArrayList<Point2D.Float> pontos=new ArrayList<>();
        int nnos=0;
        for (GNode snode: startnodes){
            pontos.add(new Point2D.Float((float)snode.getX(),(float)snode.getY()));

        }

        //Insere pontos junto às paredes e junto às esquinas de cada obstáculo
        for (Area area: areas){
            pontos.addAll(intersect(grow(area),new Area(paredes)));
            for (Area outra: areas){
                if (!area.equals(outra)){
                    pontos.addAll(intersect(grow(area),grow(outra)));
                }
            }
            pontos.addAll(corners(grow(area)));
        }
        for (Area area: areas){
            for (Point2D.Float ponto: pontos){
                if (area.contains(ponto)||(!paredes.contains(ponto)))
                    pontos.remove(ponto);
            }
        }

        for (Point2D.Float ponto: pontos){
            ARWGraphNode node=grafo.findClosestNode(ponto.x,ponto.y,(float)0.25);
            if (node==null)
                grafo.createGraphNode(ponto.x,ponto.y, GraphNodeType.SIMPLE);
            else
                pontos.remove(ponto);
        }

        for (int i=0; i<pontos.size();i++){
            for (int j=i+1; j<pontos.size();j++){
                boolean intercetou=false;
                for (Area area: areas){
                    Line2D.Float line = new Line2D.Float(pontos.get(i).x,pontos.get(i).y,pontos.get(j).x,pontos.get(j).y);
                    if (line.intersects(area.getBounds2D())){
                        intercetou=true;
                        break;
                    }
                }
                if (!intercetou){
                    ARWGraphNode start=grafo.findClosestNode(pontos.get(i).x,pontos.get(i).y);
                    ARWGraphNode end=grafo.findClosestNode(pontos.get(j).x,pontos.get(j).y);

                    grafo.makeNeighbors(start,end,false);
                }
            }
        }
        return grafo;

    }

    public void testa(){


        Area area1= new Area(new Rectangle2D.Float((float)1.0,(float)1.0,(float)5.0,(float)5.0));
        Area area2= new Area(new Rectangle2D.Float((float)3.0,(float)0.0,(float)1.0,(float)8.0));

        ArrayList<Point2D.Float> pontos=intersect(area1,area2);

        System.out.println(pontos.toString());
    }

}
