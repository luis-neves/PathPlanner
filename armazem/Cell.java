package armazem;

import java.awt.*;

public class Cell {
    private final int line, column;
    private Robo agent;
    private Prateleira prateleira;
    private Encomenda encomenda;
    private Saida saida;
    private Cell caminho;

    private int heuristica;
    private int f;



    private int g;
    private Cell parent;



    public Cell(int line, int column) {
        super();
        this.line = line;
        this.column = column;
    }

    public void calcularHeuristica(Cell saida) {
        this.heuristica = Math.abs(saida.getLine() - getLine()) + Math.abs(saida.getColumn() - getColumn());
    }

    public void setNodeData(Cell currentCell, int cost) {
        int gCost = currentCell.getG() + cost;
        setParent(currentCell);
        setG(gCost);
        calculateFinalCost();
    }

    @Override
    public String toString() {
        return "Cell(" + line +
                "|" + column +
                ')';
    }

    public boolean checkBetterPath(Cell currentNode, int cost) {
        int gCost = currentNode.getG() + cost;
        if (gCost < getG()) {
            setNodeData(currentNode, cost);
            return true;
        }
        return false;
    }

    private void calculateFinalCost() {
        int finalCost = getG() + getHeuristica();
        setF(finalCost);
    }

    public Color getColor() {
        if (agent != null) {
            return agent.getColor();
        }
        if (prateleira != null) {
            return prateleira.getColor();
        }
        if(saida != null){
            return saida.getColor();
        }
        if(caminho!= null)
            return Color.orange;
        return Color.WHITE;
    }

    public Robo getAgent() {
        return agent;
    }

    public void setAgent(Robo agent) {
        this.agent = agent;
    }

    public boolean hasAgent() {
        return agent != null;
    }

    public Encomenda getEncomenda() {

        return encomenda;
    }

    public void setEncomenda(Encomenda encomenda) {
        this.encomenda = encomenda;
    }

    public boolean hasEncomenda(){
        return encomenda != null;
    }

    public Prateleira getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(Prateleira prateleira) {
        this.prateleira = prateleira;
    }

    public boolean hasPrateleira(){
        return prateleira != null;
    }


    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object arg0) {
        Cell other = (Cell) arg0;
        return this.getLine() == other.getLine() && this.getColumn() == other.getColumn();
    }

    public Saida getSaida() {
        return saida;
    }

    public void setSaida(Saida saida) {
        this.saida = saida;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public Cell getParent() {
        return parent;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public int getHeuristica() {
        return heuristica;
    }

    public void setHeuristica(int heuristica) {
        this.heuristica = heuristica;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public void setCaminho() {
        this.caminho = this;
    }
}