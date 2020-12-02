package communication;

import ga.GASingleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class CommunicationVariables extends Observable {

    private List<Operator> operators;
    private int num_operators = GASingleton.NUM_OPERATORS;

    public int getNum_operators() {
        return num_operators;
    }

    public void setNum_operators(int num_operators) {
        this.num_operators = num_operators;
    }

    private boolean operators_ready = false;
    private boolean erp_ready = false;
    private boolean modelador_ready = false;
    private boolean loc_fina_ready = false;
    private boolean loc_aprox_ready = false;

    public boolean isOperators_ready() {
        return operators_ready;
    }

    public void setOperators_ready(boolean operators_ready) {
        this.operators_ready = operators_ready;
    }

    public boolean isErp_ready() {
        return erp_ready;
    }

    public void setErp_ready(boolean erp_ready) {
        this.erp_ready = erp_ready;
    }

    public boolean isModelador_ready() {
        return modelador_ready;
    }

    public void setModelador_ready(boolean modelador_ready) {
        this.modelador_ready = modelador_ready;
    }

    public boolean isLoc_fina_ready() {
        return loc_fina_ready;
    }

    public void setLoc_fina_ready(boolean loc_fina_ready) {
        this.loc_fina_ready = loc_fina_ready;
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
    }

    public CommunicationVariables() {
        operators = new ArrayList<>();
    }
    public CommunicationVariables(GASingleton instance) {
        operators = new ArrayList<>();
        addObserver(instance);
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
}
