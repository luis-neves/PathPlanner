package armazem;

import java.awt.*;
import java.util.List;

public class Robo implements Agent {

    private Cell cell;
    private List<Cell> caminho;

    public Robo(Cell cell) {
        this.cell = cell;
        this.cell.setAgent(this);
    }
    public Robo(Cell cell,List<Cell> caminho) {
        this.cell = cell;
        this.cell.setAgent(this);
        this.caminho = caminho;
    }

    public void act(Environment environment) {
        Perception perception = buildPerception(environment);
        Action action = decide(perception);
        execute(action, environment);
    }
    public void actNew(Environment environment, int newAgent) {
        if(caminho.indexOf(getCell()) != -1 && caminho.indexOf(getCell()) != caminho.size() - 1){
            setCell(caminho.get(caminho.indexOf(getCell())+1));
        }
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell.setAgent(null);
        this.cell = cell;
        this.cell.setAgent(this);
    }

    public Color getColor() {
        return Color.BLACK;
    }

    private Perception buildPerception(Environment environment) {
        return new Perception(
                environment.getNorthCell(cell),
                environment.getSouthCell(cell),
                environment.getEastCell(cell),
                environment.getWestCell(cell));
    }

    private Action decide(Perception perception) {
        // todo modify to improve the Robo's decision process

        return Action.NORTH; // life is peaceful there :-)
    }

    private void execute(Action action, Environment environment) {

        // todo modify to improve the Robo's decision process
        
        Cell nextCell = null;

        if (action == Action.NORTH && cell.getLine() != 0) {
            nextCell = environment.getNorthCell(cell);
        } else if (action == Action.SOUTH && cell.getLine() != environment.getNumLines() - 1) {
            nextCell = environment.getSouthCell(cell);
        } else if (action == Action.WEST && cell.getColumn() != 0) {
            nextCell = environment.getWestCell(cell);
        } else if (action == Action.EAST && cell.getColumn() != environment.getNumColumns() - 1) {
            nextCell = environment.getEastCell(cell);
        }

        if (nextCell != null && !nextCell.hasPrateleira() && !nextCell.hasAgent()) {
            setCell(nextCell);
        }
    }

}
