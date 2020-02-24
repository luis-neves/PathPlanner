package utils;

/**
 * @author Actual code:
 * Carlos Urbano<carlos.urbano@ipleiria.pt>
 * Catarina Reis<catarina.reis@ipleiria.pt>
 * Marco Ferreira<marco.ferreira@ipleiria.pt>
 * João Ramos<joao.f.ramos@ipleiria.pt>
 * Original code: José Magno<jose.magno@ipleiria.pt>
 */
public enum ComparacaoDoubleDesc implements Comparacao<Double> {
    CRITERIO;

     ComparacaoDoubleDesc() {
    }

    @Override
    public int comparar(double o1, double o2) {
        return o2 < o1 ? -2 : o2 == o1 ? 0 : 5;
    }}

