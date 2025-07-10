package model;

import controller.Giocatore;

import java.util.List;

public class Partita {
    private final Partita2v2 gioco;

    public Partita(List<Giocatore> giocatori) {
        this.gioco = new Partita2v2(giocatori);
    }

    public Partita2v2 getGioco() {
        return this.gioco;
    }

    // Il nuovo ciclo di gioco, orchestrato dalla grafica
    public String eseguiPartita() {
        gioco.inizio();

        for (int i = 0; i < 10; i++) { // 10 round
            for (int j = 0; j < 4; j++) { // 4 turni per round
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

        // --- NUOVA LOGICA PER I NOMI ---
        // Recupera i nomi dei giocatori dalla logica di gioco
        List<Giocatore> giocatori = gioco.getGiocatori();
        String nomeSquadra1 = giocatori.get(0).getNome() + " & " + giocatori.get(2).getNome(); // Andrei & Borl
        String nomeSquadra2 = giocatori.get(1).getNome() + " & " + giocatori.get(3).getNome(); // ThomasBot & DiegoBot

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
