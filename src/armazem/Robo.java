package armazem;

import java.awt.*;
import java.util.List;

public class Robo implements Agent {

    private Cell cell;
    private List<Cell> path;
    private int index;
    private int numPicks;

    public Robo(Cell cell) {
        this.cell = cell;
        this.cell.setAgent(this);
        this.numPicks = 0;

    }

    public Robo(Cell cell, List<Cell> path) {
        this.cell = cell;
        this.cell.setAgent(this);
        this.path = path;
        this.numPicks = 0;
        index = 0;
    }


    public void act(Environment environment) {
        if (path != null && index < path.size()) {
            setCell(environment.getCell(path.get(index).getLine(), path.get(index).getColumn()));
            index++;
        }
        if (getCell().getLine() > 0) {
            if (environment.getCell(getCell().getLine() - 1, getCell().getColumn()).hasEncomenda()) {
                environment.getCell(getCell().getLine() - 1, getCell().getColumn()).setEncomenda(null);
            }
        }
        if (getCell().getLine() + 1 < environment.getNumLines()) {
            if (environment.getCell(getCell().getLine() + 1, getCell().getColumn()).hasEncomenda()) {
                environment.getCell(getCell().getLine() + 1, getCell().getColumn()).setEncomenda(null);
            }
        }
        if (getCell().getColumn() > 0) {
            if (environment.getCell(getCell().getLine(), getCell().getColumn() - 1).hasEncomenda()) {
                environment.getCell(getCell().getLine(), getCell().getColumn() - 1).setEncomenda(null);
            }
        }
        if (getCell().getColumn() + 1 < environment.getNumColumns()) {
            if (environment.getCell(getCell().getLine(), getCell().getColumn() + 1).hasEncomenda()) {
                environment.getCell(getCell().getLine(), getCell().getColumn() + 1).setEncomenda(null);
            }
        }
    }

    @Override
    public void addPath(List<Cell> path) {
        this.path = path;
    }


    public Cell getCell() {
        return cell;
    }

    @Override
    public void clearPath() {
        this.path = null;
    }

    public void setCell(Cell cell) {
        this.cell.setAgent(null);
        this.cell = cell;
        this.cell.setAgent(this);
    }

    public Color getColor() {
        return Color.BLACK;
    }

    public List<Cell> getPath() {
        return path;
    }

    public void setPath(List<Cell> path) {
        this.path = path;
    }

    public void setNumPicks(int numPicks) {
        this.numPicks = numPicks;

    }

    public int getNumPicks() {
        return numPicks;
    }
}
