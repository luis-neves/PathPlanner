package communication;

import org.codehaus.jackson.annotate.JsonProperty;

public class Tarefa {
    @JsonProperty("Ordem")
    String ordem;
    @JsonProperty("LinhaOrdem")
    String linhaOrdem;
    @JsonProperty("Produto")
    String produto;
    @JsonProperty("Quantidade")
    Integer quantidade;
    @JsonProperty("Origem")
    String origem;
    @JsonProperty("Destino")
    String destino;


    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getOrdem() {
        return ordem;
    }

    public void setOrdem(String ordem) {
        this.ordem = ordem;
    }

    public String getLinhaOrdem() {
        return linhaOrdem;
    }

    public void setLinhaOrdem(String linhaOrdem) {
        this.linhaOrdem = linhaOrdem;
    }



    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Tarefa() {
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "ordem='" + ordem + '\'' +
                ", linhaOrdem='" + linhaOrdem + '\'' +
                ", produto=" + produto +
                ", quantidade=" + quantidade +
                ", origem='" + origem + '\'' +
                ", destino='" + destino + '\'' +
                '}';
    }
}
