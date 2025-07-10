package model;

import controller.Giocatore;

import java.util.List;


/**
 * Rappresenta una facciata della logica piu complessa del gioco contenuta in Partita2v2.
 * Il suo scopo è fornire un'interfaccia semplice per avviare e gestire una partita
 */

public class Partita {
    private final Partita2v2 gioco;

    /*
     Costruttore della Partita che accetta una lista di giocatori da cui creare l'istanza del motore di gioco.
     */

    public Partita(List<Giocatore> giocatori) {
        this.gioco = new Partita2v2(giocatori);
    }


    /*
    Utile per la View (GamePanel) per ottenere informazioni dettagliate sullo stato del gioco.
     **/
    public Partita2v2 getGioco() {
        return this.gioco;
    }

    /*Esegue una partita completa di Tresette dall'inizio alla fine.
     * Il metodo gestisce il ciclo principale del gioco, che consiste in 10 round.
     * Per ogni round, vengono giocati 4 turni (una carta per giocatore), e in seguito
     * vengono calcolati i punti e il tavolo viene svuotato.
     **/
    public String eseguiPartita() {
        gioco.inizio();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 4; j++) {
                gioco.giocaTurno();
            }
            gioco.finalizzaRound();
            try { Thread.sleep(1500); } catch (InterruptedException e) {} // Pausa per vedere il tavolo
            gioco.svuotaTavolo();
        }
        return calcolaVincitore();
    }
    public String calcolaVincitore() {
        // Calcola i punteggi di squadra come prima
        float puntiSquadra1 = Partita2v2.getPunteggio(0) + Partita2v2.getPunteggio(2);
        float puntiSquadra2 = Partita2v2.getPunteggio(1) + Partita2v2.getPunteggio(3);


        List<Giocatore> giocatori = gioco.getGiocatori();
        String nomeSquadra1 = giocatori.get(0).getNome() + " & " + giocatori.get(2).getNome();
        String nomeSquadra2 = giocatori.get(1).getNome() + " & " + giocatori.get(3).getNome();

        System.out.println("\nRISULTATO FINALE → " +
                nomeSquadra1 + ": " + String.format("%.2f", puntiSquadra1) + " pt | " +
                nomeSquadra2 + ": " + String.format("%.2f", puntiSquadra2) + " pt");

        if (puntiSquadra1 > puntiSquadra2) {
            return nomeSquadra1;
        } else if (puntiSquadra2 > puntiSquadra1) {
            return nomeSquadra2;
        } else {
            return "Pareggio";
        }
    }
}
