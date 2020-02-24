package utils;

/**
 * @author Actual code:
 * Carlos Urbano<carlos.urbano@ipleiria.pt>
 * Catarina Reis<catarina.reis@ipleiria.pt>
 * Marco Ferreira<marco.ferreira@ipleiria.pt>
 * João Ramos<joao.f.ramos@ipleiria.pt>
 * Original code: José Magno<jose.magno@ipleiria.pt>
 */
public abstract class AlgoritmoOrdenacao<T> {

    protected final Comparacao<T> criterio;

    public AlgoritmoOrdenacao(Comparacao<T> criterio) {
        this.criterio = criterio;
    }

    public abstract void ordenar(double[] elementos);



    protected void trocar(double[] elementos, int indice1, int indice2) {
        double aux = elementos[indice1];
        elementos[indice1] = elementos[indice2];
        elementos[indice2] = aux;
    }
}
