package model.carta;


/**
 * L'enum Valore rappresenta i valori delle carte nel gioco del Tresette,
 * dal Due all'Asso.
 * <p>
 * Ogni valore ha associati due attributi fondamentali(ranking e punti) per la logica di gioco:
 */
public enum Valore {
    DUE(1,0.0f),QUATTRO(1,0.0f),CINQUE(2,0.0f),SEI(3,0.0f),SETTE(4,0.0f),FANTE(5,1.0f/3),CAVALLO(6,1.0f/3),RE(7,1.0f/3),TRE(10,1.0f/3),ASSO(8,1.0f);

    /**
     Il ranking è un valore che indica l'ordine di importanza del valore della carta.
     */
    private final int ranking;
    private final float punti;


    /**
     Costruttore privato per l'enum Valore.
     */
    Valore(int ranking, float punti) {
        this.ranking = ranking;
        this.punti = punti;
    }

    /**
     Restituisce il ranking della carta.
     */

    public int getRanking(){
            return ranking;
    }


    /*
     Restituisce i punti associati al valore della carta.
     */
    public float getPunti() {
        return punti;
    }
}
