package armazem;

import java.awt.*;

public class Prateleira {
    private Cell cell;
    private Encomenda encomenda;


    public Prateleira(Cell cell) {
        this.cell = cell;
        this.cell.setPrateleira(this);
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Encomenda getEncomenda() {
        return encomenda;
    }

    public void setEncomenda(Encomenda encomenda) {
        this.encomenda = encomenda;
        this.cell.setEncomenda(encomenda);
    }

    public Color getColor(){
        if(this.cell.hasEncomenda()){
            return this.encomenda.getColor();
        }
        return Color.gray;
    }

    @Override
    public String toString() {
        return "Prateleira";
    }
}