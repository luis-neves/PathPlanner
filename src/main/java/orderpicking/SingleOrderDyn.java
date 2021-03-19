package orderpicking;


import pathfinder.*;
import tsp.TspDynamicProgrammingIterative;

import java.util.*;

public class SingleOrderDyn {
    Graph<GNode> grafo;
    String[] produtos;
    List rotafinal;
    String startnode;
    String endnode;

    public SingleOrderDyn(Graph<GNode> grafo, String[] produtos, String startnode, String endnode){
        this.grafo=grafo;
        this.produtos=produtos;
        this.startnode=startnode;
        this.endnode=endnode;
    }

    public List getRotafinal(){
        return rotafinal;
    }

    public double solve(){

        RouteFinder routeFinder = new RouteFinder<>(grafo, new DistanceScorer(), new DistanceScorer());
        //Constroi matriz de distâncias com m x m, m=nnosproduto+entrada+saída
        //a 1ª linha serve para a distância entre o nó de partida e cada um dos nós de produto
        //a última linha tem a distância entre cada um dos nós de produto e a saída
        //as distâncias têm de ser introduzidas nos 2 sentidos.

        int nnos=produtos.length+2;
        double[][] distanceMatrix=new double[nnos][nnos];
        List[][] rotas=new List[nnos][nnos];
        for (double[] row : distanceMatrix) Arrays.fill(row, 100000);
        for (int i=1; i<nnos-1;i++){
            //Determina rota e custos entre o nó inicial e todos os nós de produto
            Route r=routeFinder.findRoute(grafo.getNode(startnode), grafo.getNode(produtos[i-1]));
            distanceMatrix[0][i]=r.getCost();
            rotas[0][i]=r.getRoute();
            //Determina rota e custos entre cada nó de produto e o nó de saída
            r=routeFinder.findRoute(grafo.getNode(produtos[i-1]),grafo.getNode(endnode));
            distanceMatrix[i][nnos-1]=r.getCost();
            rotas[i][nnos-1]=r.getRoute();
            for(int j=i+1;j<nnos-1;j++){
                if (i!=j) {
                    //Determina rota e custos entre nós de produto
                    r = routeFinder.findRoute(grafo.getNode(produtos[i - 1]), grafo.getNode(produtos[j - 1]));
                    distanceMatrix[i][j] = r.getCost();
                    rotas[i][j]=r.getRoute();
                    distanceMatrix[j][i] = r.getCost();
                    rotas[j][i]=routeFinder.reverseRoute(r);
                }
            }
        }
        //Como o algoritmo TSP assume o regresso ao ponto de partida, define-se a distância entre a saída e a
        //entrada =0 (só nesse sentido) para não interferir com o cálculo do custo.
        //Rever possivelmente numa versão futura.
        distanceMatrix[nnos-1][0]=0;

        //Calcula a melhor rota entre a entrada e a saída, passando por todos os produtos
        TspDynamicProgrammingIterative solver = new TspDynamicProgrammingIterative(0, distanceMatrix);

        //Imprime o resultado do TSP
    //        System.out.println("Tour: " + solver.getTour());

        //Junta as rotas numa lista única

        rotafinal=new ArrayList();
        List indices=solver.getTour();
            for (int i = 0; i<indices.size()-1;i++){
            int o = (int)indices.get(i);
            int d = (int)indices.get(i+1);

            if(rotas[o][d]!=null) {
                if (!rotafinal.isEmpty())
                    rotafinal.remove(rotafinal.size() - 1);
                rotafinal.addAll(rotas[o][d]);
            }
        }
         //   System.out.println("Tour: " + rotafinal);
        return solver.getTourCost();
        // Print: 42.0
        //    System.out.println("Tour cost: " + solver.getTourCost());

    }
}
