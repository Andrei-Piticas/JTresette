package model;

import model.carta.Carta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;



/**
 * Rappresenta la mano di carte di un singolo giocatore.
 * Questa classe agisce come un contenitore specializzato per gli oggetti Carta,
 * incorporando la logica per aggiungere, rimuovere e accedere alle carte.
 */
public class Mano {
    /* La lista di carte nella mano,che non può essere modifica esternamente(non verra riassegnata),ma cambierà il contenuto.
     */
    private final List<Carta> carte = new ArrayList<>();

    public Mano(){
        // Costruttore vuoto
    }

    /* Costruttore per creare una mano partendo da una collezione di carte esistente.
        Utile per creare la mano temporanea nella scelta della carta da giocare del bot.
     */
    public Mano(Collection<Carta> iniziali) {
        carte.addAll(iniziali);

    }


    /**
     * Aggiunge una lista di nuove carte alla mano
     */
    public void addCarte(List<Carta> nuove) {
        carte.addAll(nuove);
    }

    /**
     * Aggiunge una singola carta alla mano.
     */
    public void addCarta(Carta c){
            carte.add(c);
    }

    /*  Simula la giocata di una carta, rimuovendola dalla mano.
        Lancia un'eccezione se si tenta di giocare una carta non presente,
        prevenendo errori di stato nel gioco nelle giocate dei bot o dei giocatori.
            */

    public void gioca(Carta c){
            if(!carte.remove(c)){
                throw new IllegalArgumentException("Nessuna carta nella mano" + c);
            }
    }


    /*
    Restituisce il numero di carte attualmente presenti nella mano.
        */
    public int size(){
            return carte.size();
    }
    /*
    Fornisce una copia della lista di carte nella mano.
     */
    public List<Carta> getCarte(){
            return List.copyOf(carte);
    }

/**
 * Svuota completamente la mano del giocatore.
 * Utile per resettare lo stato all'inizio di una nuova partita.
 */
    public void svuota() {
        // Assuming your internal list is named 'carte'
        this.carte.clear();
    }




}
