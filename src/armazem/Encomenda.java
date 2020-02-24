package armazem;

import java.awt.*;

public class Encomenda {
    private Prateleira prateleira;

    public Prateleira getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(Prateleira prateleira) {
        this.prateleira = prateleira;
    }

    public Encomenda(Prateleira prateleira) {
        this.prateleira = prateleira;
        this.prateleira.setEncomenda(this);
    }

    public Color getColor(){
        return Color.CYAN;
    }

    public Cell getCell(){
        return this.prateleira.getCell();
    }
}
