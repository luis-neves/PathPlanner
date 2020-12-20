package communication;

import ga.GASingleton;
import utils.Graphs.GraphNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class CommunicationVariables extends Observable {

    private List<Operator> operators;
    private List<Tarefa> tarefas;
    private int num_operators = GASingleton.NUM_OPERATORS;

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        if (tarefas != null) {
            tasks_ready = true;
        } else {
            tasks_ready = false;
        }
        this.tarefas = tarefas;
    }

    public int getNum_operators() {
        return num_operators;
    }

    public void setNum_operators(int num_operators) {
        this.num_operators = num_operators;
    }

    private boolean operators_ready = false;
    private boolean erp_ready = false;
    private boolean modelador_ready = false;
    private boolean loc_aprox_ready = false;
    private boolean tasks_ready = false;
    private boolean[] ga_ready;

    public boolean isTasks_ready() {
        return tasks_ready;
    }

    public void setTasks_ready(boolean tasks_ready) {
        this.tasks_ready = tasks_ready;
        setChanged();
        notifyObservers();
    }

    public boolean isOperators_ready() {
        return operators_ready;
    }

    public void setOperators_ready(boolean operators_ready) {
        this.operators_ready = operators_ready;

        setChanged();
        notifyObservers();
    }

    public boolean isErp_ready() {
        return erp_ready;
    }

    public void setErp_ready(boolean erp_ready) {
        this.erp_ready = erp_ready;

        setChanged();
        notifyObservers();
    }

    public boolean isModelador_ready() {
        return modelador_ready;
    }

    public void setModelador_ready(boolean modelador_ready) {
        this.modelador_ready = modelador_ready;

        setChanged();
        notifyObservers();
    }

    public boolean isLoc_aprox_ready() {
        return loc_aprox_ready;
    }

    public void setLoc_aprox_ready(boolean loc_aprox_ready) {
        this.loc_aprox_ready = loc_aprox_ready;
    }

    public List<Operator> getOperators() {
        return operators;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;

        setChanged();
        notifyObservers();
    }

    public CommunicationVariables() {
        operators = new ArrayList<>();
    }

    public CommunicationVariables(GASingleton instance) {
        operators = new ArrayList<>();
        addObserver(instance);
        ga_ready = new boolean[2];
    }

    public boolean ga_fully_ready() {
        for (int i = 0; i < ga_ready.length; i++) {
            if (!ga_ready[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean[] getGa_ready() {
        return ga_ready;
    }

    public void setGa_ready(boolean[] ga_ready) {
        this.ga_ready = ga_ready;
    }

    public void addOperator(String id, boolean available) {
        this.operators.add(new Operator(id, available));
        if (operators.size() == num_operators) {
            operators_ready = true;
        }
        setChanged();
        notifyObservers();
    }

    public Operator findOperator(String id) {
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i).getId().equals(id)) {
                return operators.get(i);
            }
        }
        return null;
    }

    public Operator getOperatorByGraphNode(GraphNode agent) {
        for(Operator operator : operators){
            if(operator.getAgent().equals(agent)){
                return operator;
            }
        }
        return null;
    }
}
