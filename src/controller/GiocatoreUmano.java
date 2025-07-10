package controller;

import model.carta.Carta;
import view.GameUI;

import java.util.ArrayList;
import java.util.List;

/**
  Rappresenta un giocatore umano all'interno del gioco.
  Questa classe implementa l'interfaccia Giocatore e si differenzia dai bot
  perché non usa una strategia automatica, ma si affida all'interfaccia grafica
  per ricevere l'input dall'utente.
 */

public class GiocatoreUmano implements Giocatore {
    private final GameUI ui;  // Riferimento all'interfaccia grafica
    protected final List<Carta> mano = new ArrayList<>(); // La lista di carte possedute dal giocatore
    private String nome; // Il nome del giocatore



    /*
    L'implementazione dell'interfaccia grafica
    da cui ricevere le scelte dell'utente.
    */
    public GiocatoreUmano(GameUI ui) {
        this.ui = ui;
    }

    /*
    Aggiunge una carta alla mano del giocatore.
    */
    @Override
    public void riceviCarta(Carta c) {
        mano.add(c);
    }

    @Override
    public Carta giocaCarta(List<Carta> tavolo) {
        // chiedo alla GUI quale carta vuole giocare
        Carta scelta = ui.promptGiocaCarta(new ArrayList<>(mano), tavolo);
        // la rimuovo definitivamente dalla mia mano
        mano.remove(scelta);
        return scelta;
    }

    /*Restituisce una copia della mano di carte attuale del giocatore.
     È importante che restituisca una copia per proteggere lo stato iniziale
     */
    @Override
    public List<Carta> getCarte() {
        return new ArrayList<>(mano);
    }

    /*Metodi implementati "vuoti" per conformita' del codice*/

    @Override
    public Carta giocaCarta() {
        // delega, se mai fosse invocato
        return giocaCarta(List.of());
    }

    @Override
    public Carta giocaCarta(List<Carta> carte, List<Carta> tavolo) {
        return null;
    }


    /*Metodo per ottenere un nome*/
    @Override
    public String getNome(String nome) {
        return "Tu";
    }

    /*Metodo per svuotare la mano del giocatore*/

    @Override
    public void svuotaMano() {
        this.mano.clear();
    }

    /**
     * Imposta il nome del giocatore.
     */
    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il nome del giocatore.
     */
    public String getNome() {
        return this.nome;
    }


}
