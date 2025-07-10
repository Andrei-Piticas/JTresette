package model;

public class Squadra {

    private final String nome;
    private final int giocatore1;
    private  final int giocatore2;

    public Squadra(String nome, int giocatore1, int giocatore2) {
        this.nome = nome;
        this.giocatore1 = giocatore1;
        this.giocatore2 = giocatore2;
    }

    public String getNome(){
        return nome;
    }

    public int getGiocatore1(){
        return giocatore1;
    }

    public int getGiocatore2() {
        return giocatore2;
    }



    public float calcolaPunteggio(Partita2v2 partita){
        return partita.getPunteggio(giocatore1) + partita.getPunteggio(giocatore2);

    }
}
