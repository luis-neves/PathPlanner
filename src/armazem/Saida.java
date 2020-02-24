package armazem;

import java.awt.*;

public class Saida {
    private Cell cell;

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Saida(Cell cell) {
        this.cell = cell;
        this.cell.setSaida(this);
    }

    public Color getColor(){
        return Color.green;
    }
}
