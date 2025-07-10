package model;

import view.audio.AudioManager;
import controller.Giocatore;
import controller.GiocatoreUmano;
import model.carta.Carta;
import model.carta.Seme;
import view.GameUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Rappresenta il motore di gioco principale di una partita 2 contro 2
 * Questa classe gestisce lo stato interno della partita, come il mazzo,
 * il tavolo, l'ordine dei turni e i punteggi. Contiene la logica fondamentale
 * per eseguire un turno, finalizzare una mano e calcolare i vincitori.
 */
public class Partita2v2 {
    private final List<Giocatore> giocatori;
    private final Mazzo mazzo;
    private final List<Carta> tavolo = new ArrayList<>();
    private GameUI observer; // Riferimento alla View per le notifiche
    private int turnoIndex = 0;
    private static float[] punteggi;
    private int contaRound = 0;
    private int ultimoVincitore;
    private int lastStartIndex;



    /**
     * Costruttore della partita 2v2 che accetta una lista di giocatori.
     * Inizializza il mazzo e i punteggi.
     */
    public Partita2v2(List<Giocatore> players) {
        this.giocatori = players;
        this.mazzo = new Mazzo();
        this.punteggi = new float[4];
    }


    /**
     * Imposta l'osservatore (View) per notifiche e aggiornamenti.
     */
    public void setObserver(GameUI observer) {
        this.observer = observer;
    }


    /**
     * Inizializza la partita, mescola il mazzo e distribuisce le carte ai giocatori e
     * resetta i punteggi e determina il primo giocatore.
     */
    public void inizio() {
        mazzo.mescola();
        List<Mano> mani = mazzo.distribuzione();
        for (int i = 0; i < 4; i++) {
            Giocatore g = giocatori.get(i);


            g.svuotaMano();

            for (Carta c : mani.get(i).getCarte()) {
                g.riceviCarta(c);
            }
        }
        Arrays.fill(punteggi, 0f);
        contaRound = 0;
        determinaPrimoGiocatore();
    }



    /**
     * Esegue un singolo turno di gioco.
     * Fa giocare il giocatore corrente, aggiunge la carta al tavolo,
     * notifica la GUI e gestisce le pause
     */
    public void giocaTurno() {
        if (tavolo.size() == 0) {
            this.lastStartIndex = this.turnoIndex; // Salva chi inizia il round
        }

        Giocatore g = giocatori.get(turnoIndex);
        Carta c = g.giocaCarta(List.copyOf(tavolo));
        tavolo.add(c);

        AudioManager.getInstance().playSoundEffect("src/view/audio/flipcard-91468.wav");

        turnoIndex = (turnoIndex + 1) % 4;


        if (observer != null) {
            observer.update();
            try {
                // Se non è l'umano, aspetta la pausa del bot
                if (!(g instanceof GiocatoreUmano)) {
                    Thread.sleep(800);
                } else {
                    // altrimenti aggiunge una pausa maggiuntiva per l'umano
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }



    /*
      * Calcola il risultato di una mano una volta che tutte e 4 le carte sono sul tavolo.
     * Determina la carta vincente, assegna i punti al giocatore corretto e imposta il turno per la mano successiva.
     *
     * */
    public void finalizzaRound() {
        Seme semeGuida = tavolo.get(0).getSeme();
        int migliore = 0;

        for (int j = 1; j < tavolo.size(); j++) {
            Carta cartaCorrente = tavolo.get(j);
            Carta cartaMigliore = tavolo.get(migliore);

            if (cartaCorrente.getSeme() == semeGuida && cartaMigliore.getSeme() != semeGuida) {
                migliore = j;
            } else if (cartaCorrente.getSeme() == semeGuida) {
                if (cartaCorrente.getValore().getRanking() > cartaMigliore.getValore().getRanking()) {
                    migliore = j;
                }
            }
        }

        int vincitore = (this.lastStartIndex + migliore) % 4;
        this.ultimoVincitore = vincitore;
        if (observer != null) {
            String nomeVincitore = giocatori.get(vincitore).getNome();
            observer.mostraRiepilogoMano(nomeVincitore);
        }

        float puntiRound = (float) tavolo.stream().mapToDouble(c -> c.getValore().getPunti()).sum();
        punteggi[vincitore] += puntiRound;
        contaRound++;
        if (contaRound == 10) punteggi[vincitore] += 1f;

        turnoIndex = vincitore; // Il vincitore inizia il prossimo round
    }

    /*svuota il tavolo per il round successivo
    * */
    public void svuotaTavolo() {
        tavolo.clear();
        if (this.contaRound < 10) {
            observer.update();
        }
    }


    /**
     * Determina il primo giocatore che inizia la partita.
     * Inizializza l'indice del turno al primo giocatore (0).
     */
    public void determinaPrimoGiocatore() {
        turnoIndex = 0;
    }


    /*i vari getter dei punteggi,giocatori,tavolo e dell'index per permettere tutta la logica funzionante*/

    public static float getPunteggio(int i) { return punteggi[i]; }

    public List<Giocatore> getGiocatori() { return List.copyOf(giocatori); }


    public List<Carta> getTavolo() { return List.copyOf(tavolo); }
    public int getLastStartIndex() { return lastStartIndex; }
}