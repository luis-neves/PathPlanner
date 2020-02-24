package armazem;


import java.util.List;

public interface Agent {

    void act(Environment environment);

    void addPath(List<Cell> path);

    Cell getCell();

    void clearPath();

    List<Cell> getPath();
}