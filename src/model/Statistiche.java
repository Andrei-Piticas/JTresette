package model;

import org.json.JSONObject;


//* Statistiche è un modello che contiene tutte le informazioni relative ai progressi e alla carriera di un giocatore.
// È responsabile di mantenere il conteggio delle partite giocate, vinte e perse.
//*
public class Statistiche {
    private int partiteGiocate;
    private int partiteVinte;
    private int partitePerse;



    // Costruttore di default che inizializza le statistiche a zero.
    public Statistiche() {
        this.partiteGiocate = 0;
        this.partiteVinte   = 0;
        this.partitePerse   = 0;
    }


    // Costruttore che accetta un JSONObject per inizializzare le statistiche.
    public Statistiche(JSONObject obj) {
        this.partiteGiocate = obj.optInt("partiteGiocate", 0);
        this.partiteVinte   = obj.optInt("partiteVinte",   0);
        this.partitePerse   = obj.optInt("partitePerse",   0);
    }


    // Metodo che converte le statistiche in un JSONObject per la serializzazione.
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("partiteGiocate", partiteGiocate);
        obj.put("partiteVinte",   partiteVinte);
        obj.put("partitePerse",   partitePerse);
        return obj;
    }


    //*Getter dei rispettivi valori*//
    public int getPartiteGiocate() {
        return partiteGiocate;
    }

    public int getPartiteVinte() {
        return partiteVinte;
    }

    public int getPartitePerse() {
        return partitePerse;
    }


    //*Metodi per incrementare i valori rispettivi*//
    public void incrementaGiocate() {
        this.partiteGiocate++;
    }

    public void incrementaVinte() {
        this.partiteVinte++;
    }

    public void incrementaPerse() {
        this.partitePerse++;
    }
}
