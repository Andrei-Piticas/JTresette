package model.carta;

/**
  Rappresenta una singola carta da gioco in modo immutabile.
  L'uso di un 'record' Java garantisce che una volta creata, una carta
  (con il suo seme e valore) non possa più essere modificata.
 */
public record Carta(Seme seme,Valore valore) {


/**
    Genera il nome del file immagine associato a questa carta.
    La convenzione usata è VALORENOME.png (es. "ASSOSPADE.png").
 */
    public String getNomeFile() {
        return valore.name() + seme.name() + ".png";
    }

    /**
     * Restituisce il seme della carta.
     */
    public Seme getSeme() {
        return seme;
    }

    /**
     * Restituisce il valore della carta.
     */
    public Valore getValore() {
        return valore;
    }
}
