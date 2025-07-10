package model.opponent;

import controller.Giocatore;
import model.Mano;
import model.carta.Carta;
import model.carta.Seme;

import java.util.List;

/**
 * La classe BotPlayer rappresenta un giocatore bot nel gioco del Tresette.
 * Implementa l'interfaccia Giocatore e gestisce la logica per giocare le carte
 * in base a una strategia definita.
 */

public class BotPlayer implements Giocatore {


    private final Mano mano = new Mano(); // Mano del bot
    private final Strategia strategia = new Strategia(); // Strategia del bot per scegliere le carte da giocare
    private String nome; // Nome dei due  bot


    /**
     * aggiunge una carta alla mano del bot.

     */
    @Override
    public void riceviCarta(Carta c) {
            mano.addCarta(c);
    }

    /**
     Determina e gioca una carta seguendo la logica definita dalla Strategia.
     Prima delega la scelta alla classe Strategia, ma se la strategia dovesse fallire e restituire null, il bot giocherà
     semplicemente la prima carta valida disponibile per evitare di bloccare il gioco.
     */
    @Override
    public Carta giocaCarta(List<Carta> tavolo) {

        /*
        Se il tavolo è vuoto, non c'è seme guida, quindi il bot può giocare qualsiasi carta.
        */
        Seme semeGuida = tavolo.isEmpty() ? null : tavolo.get(0).seme();

        /* Filtra le carte della mano del bot in base al seme guida. */
        List<Carta> cartePossibili = mano.getCarte().stream()
                .filter(c -> semeGuida == null || c.seme() == semeGuida)
                .toList();
        if (cartePossibili.isEmpty()) {
            cartePossibili = mano.getCarte();
        }


        Mano manoTemp = new Mano(cartePossibili);
        Carta scelta = strategia.scegliCarta(manoTemp, tavolo);

         /*
        Se non ci sono carte valide da giocare, il bot giocherà la prima carta disponibile.
        */


        if (scelta == null) {
            scelta = cartePossibili.get(0);
        }

        mano.gioca(scelta);
        return scelta;
    }


    /**
     * Restituisce la mano del bot in una copia.
     */
    @Override
    public List<Carta> getCarte() {
        return mano.getCarte();
    }


    /**
      Gioca una carta dalla mano del bot, utilizzando la strategia definita.
    */

    @Override
    public Carta giocaCarta(List<Carta> carte, List<Carta> tavolo) {
        return giocaCarta(tavolo);
    }


    /*
     * Svuota la mano del bot, rimuovendo tutte le carte.
     */
    @Override
    public void svuotaMano() {
        this.mano.svuota();
    }



    /**
     * Restituisce il nome del bot settato.
     */
    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }



    @Override
    public String getNome() {
        return this.nome;
    }





    @Override
    public Carta giocaCarta() {
        return null;
    }

    @Override
    public String getNome(String nome) {
        return "Bot";
    }
}
