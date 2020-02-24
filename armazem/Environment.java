package armazem;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Environment {

    private final Cell[][] grid;
    private final LinkedList<Robo> agents;
    private final LinkedList<Prateleira> prateleiras;
    private final LinkedList<Encomenda> encomendas;
    private LinkedList<Cell> caminho;
    private Saida saida;
    private int numLines;
    private int numColumns;
    private AStar aStar;
    private int countNumeroIterecoes;


    private final int numIterations;

    public Environment(int numLines, int numColumns, int numIterations) {
        this.numIterations = numIterations;
        this.numColumns = numColumns;
        this.numLines = numLines;
        grid = new Cell[numLines][numColumns];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }

        saida=new Saida(getCell(0,39));

       // this.grid = aStar.getSearchArea();

        this.prateleiras = new LinkedList<>();
        int aux = 0;
        for (int i = 1; i < numLines-1; i++) {
            for (int j = 1; j < numColumns-1; j++) {
                while(j < numColumns-1){
                    for (int a = 0; a < 5 && j + a < numColumns-1; a++) {

                        prateleiras.add(new Prateleira(getCell(i,j+a)));

                    }
                    j=j+7;
                }
            }
            aux++;
            if(aux > 1){
                i=i+2;
                aux=0;
            }
        }

        encomendas = new LinkedList<Encomenda>();
        agents = new LinkedList<Robo>();
        agents.add(new Robo(getCell(39, 0)));

        for(int ii= 0; ii< 3; ii++ ){
           int random = new Random(ii).nextInt(prateleiras.size());
            encomendas.add(new Encomenda(prateleiras.get(random)));
        }

        aStar = new AStar(numLines, numColumns, agents.get(0).getCell(), saida.getCell());
        aStar.setBlocks(prateleiras);
        aStar.setEncomendas(encomendas);

        //getDistancias();
        getDistanciasOtimizada();
        //getManhattandistance();
        System.out.println(countNumeroIterecoes);
    }

    public void getManhattandistance(){
        for(Encomenda a : encomendas){
            Cell inicial = getCellCloses(a.getCell());
            for(Encomenda ab: encomendas){
                Cell destino = getCellCloses(ab.getCell());
                int distance = Math.abs(inicial.getLine()-destino.getLine()) + Math.abs(inicial.getColumn()-destino.getColumn());
                System.out.println(a.getCell()+ "|"+ab.getCell()+"distance ="+distance);
            }
        }
    }


    public void getDistanciasOtimizada(){
        for(int ii = 0; ii< encomendas.size()-1;ii++){
            Cell inicial = getCellCloses(encomendas.get(ii).getCell());
            for(int i=ii+1; i<encomendas.size(); i++){
                Cell destino = getCellCloses(encomendas.get(i).getCell());
                aStar.setInitialNode(inicial);
                aStar.setFinalNode(destino);
                List<Cell> caminho = aStar.findPath();
                for(Cell c: caminho){
                    grid[c.getLine()][c.getColumn()].setCaminho();
                }
                agents.add(new Robo(caminho.get(0), caminho));
               // int aux = aStar.findPath2()-1; // -1 pq ele conta a celula (inicial) onde se encontra
                System.out.println(encomendas.get(ii).getCell().toString()+"|"+encomendas.get(i).getCell().toString()+"distancia="+ (caminho.size()-1));
                //System.out.println(encomendas.get(ii).getCell().toString()+"|"+encomendas.get(i).getCell().toString()+"distancia="+ aux);
                countNumeroIterecoes++;
            }
        }
    }
    public void getDistancias(){

        for(Encomenda a : encomendas){
            Cell inicial = getCellCloses(a.getCell());
            for(Encomenda ab : encomendas){
                Cell destino = getCellCloses(ab.getCell());
               // aStar = new AStar(numLines, numColumns, inicial, destino);
                //aStar.setBlocks(prateleiras);
                aStar.setInitialNode(inicial);
                aStar.setFinalNode(destino);
                int aux = aStar.findPath2()-1; // -1 pq ele conta a celula inicial onde se encontra
                System.out.println(a.getCell().toString()+"|"+ab.getCell().toString()+"distancia="+ aux);
                countNumeroIterecoes++;

            }
        }
    }

    // estamos a considerar que que as prateleiras têm 2 quadriculas de largura,
    public Cell getCellCloses(Cell prateleira){
        if(!grid[prateleira.getLine()-1][prateleira.getColumn()].hasPrateleira()) return grid[prateleira.getLine()-1][prateleira.getColumn()];
        return grid[prateleira.getLine()+1][prateleira.getColumn()];
    }


    public void run() {
        for (int i = 0; i < numIterations; i++) {
            for (Agent agent : agents) {
                if(agents.get(0) == agent){
                    agent.act(this);
                }
                else{
                    agent.actNew(this, 1);
                }
                fireUpdatedEnvironment();
            }
        }
    }

    public Cell getNorthCell(Cell cell) {
        if (cell.getLine() > 0) {
            return grid[cell.getLine() - 1][cell.getColumn()];
        }
        return null;
    }

    public Cell getSouthCell(Cell cell) {
        if (cell.getLine() < grid.length - 1) {
            return grid[cell.getLine() + 1][cell.getColumn()];
        }
        return null;
    }

    public Cell getEastCell(Cell cell) {
        if (cell.getColumn() < grid[0].length - 1) {
            return grid[cell.getLine()][cell.getColumn() + 1];
        }
        return null;
    }

    public Cell getWestCell(Cell cell) {
        if (cell.getColumn() > 0) {
            return grid[cell.getLine()][cell.getColumn() - 1];
        }
        return null;
    }

    public int getNumLines() {
        return grid.length;
    }

    public int getNumColumns() {
        return grid[0].length;
    }

    public final Cell getCell(int linha, int coluna) {
        return grid[linha][coluna];
    }

    public Color getCellColor(int linha, int coluna) {
        return grid[linha][coluna].getColor();
    }

    //listeners
    private final ArrayList<EnvironmentListener> listeners = new ArrayList<EnvironmentListener>();

    public synchronized void addEnvironmentListener(EnvironmentListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void fireUpdatedEnvironment() {
        for (EnvironmentListener listener : listeners) {
            listener.environmentUpdated();
        }
    }
}
