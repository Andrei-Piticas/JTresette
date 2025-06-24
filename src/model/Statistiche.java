package model;

import org.json.JSONObject;

public class Statistiche {
    private int partiteGiocate;
    private int partiteVinte;
    private int partitePerse;

    public Statistiche() {
        this.partiteGiocate = 0;
        this.partiteVinte   = 0;
        this.partitePerse   = 0;
    }

    public Statistiche(JSONObject obj) {
        this.partiteGiocate = obj.optInt("partiteGiocate", 0);
        this.partiteVinte   = obj.optInt("partiteVinte",   0);
        this.partitePerse   = obj.optInt("partitePerse",   0);
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("partiteGiocate", partiteGiocate);
        obj.put("partiteVinte",   partiteVinte);
        obj.put("partitePerse",   partitePerse);
        return obj;
    }

    public int getPartiteGiocate() {
        return partiteGiocate;
    }

    public void setPartiteGiocate(int partiteGiocate) {
        this.partiteGiocate = partiteGiocate;
    }

    public int getPartiteVinte() {
        return partiteVinte;
    }

    public void setPartiteVinte(int partiteVinte) {
        this.partiteVinte = partiteVinte;
    }

    public int getPartitePerse() {
        return partitePerse;
    }

    public void setPartitePerse(int partitePerse) {
        this.partitePerse = partitePerse;
    }

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
