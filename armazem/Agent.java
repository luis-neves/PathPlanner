package armazem;


import java.util.List;

public interface Agent {

    void act(Environment environment);
    void actNew(Environment environment, int newAgent);
}