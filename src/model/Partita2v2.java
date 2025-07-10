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

public class Partita2v2 {
    private final List<Giocatore> giocatori;
    private final Mazzo mazzo;
    private final List<Carta> tavolo = new ArrayList<>(); // Usiamo sempre questo
    private GameUI observer;
    private int turnoIndex = 0;
    private static float[] punteggi;
    private int contaRound = 0;
    private int ultimoVincitore;
    private int lastStartIndex; // Indice di chi ha iniziato il round

    public Partita2v2(List<Giocatore> players) {
        this.giocatori = players;
        this.mazzo = new Mazzo();
        this.punteggi = new float[4];
    }

    public void setObserver(GameUI observer) {
        this.observer = observer;
    }

// Replace the old inizio() method in Partita2v2.java with this one

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

    // Fa giocare un singolo giocatore e aggiorna lo stato
    public void giocaTurno() {
        if (tavolo.size() == 0) {
            this.lastStartIndex = this.turnoIndex; // Salva chi inizia il round
        }

        Giocatore g = giocatori.get(turnoIndex);
        Carta c = g.giocaCarta(List.copyOf(tavolo));
        tavolo.add(c);

        AudioManager.getInstance().playSoundEffect("src/view/audio/flipcard-91468.wav");

        turnoIndex = (turnoIndex + 1) % 4;

        // Notifica la grafica per aggiornarsi
        if (observer != null) {
            observer.update();
            try {
                // Se non è l'umano, aspetta la pausa del bot
                if (!(g instanceof GiocatoreUmano)) {
                    Thread.sleep(800);
                } else {
                    // ALTRIMENTI, aggiungi una piccola pausa anche dopo la tua giocata
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    // Calcola il vincitore e i punti alla fine di un round (quando ci sono 4 carte sul tavolo)
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
            // Ora passiamo solo il nome, senza i punteggi
            observer.mostraRiepilogoMano(nomeVincitore);
        }

        float puntiRound = (float) tavolo.stream().mapToDouble(c -> c.getValore().getPunti()).sum();
        punteggi[vincitore] += puntiRound;
        contaRound++;
        if (contaRound == 10) punteggi[vincitore] += 1f;

        turnoIndex = vincitore; // Il vincitore inizia il prossimo round
    }

    // Svuota il tavolo per il round successivo
    public void svuotaTavolo() {
        tavolo.clear();
        if (this.contaRound < 10) { // Si usa < 9 perché il round viene contato da 0 a 9 (10 round totali)
            observer.update();
        }
    }

    public void determinaPrimoGiocatore() {
        // Logica per determinare il primo giocatore (es. 4 di denari o casuale)
        // Per ora, iniziamo dal giocatore 0
        turnoIndex = 0;
    }

    // --- GETTER ---
    public int getTurnoIndex() { return turnoIndex; }
    public static float getPunteggio(int i) { return punteggi[i]; }
    public List<Giocatore> getGiocatori() { return List.copyOf(giocatori); }
    public List<Carta> getTavolo() { return List.copyOf(tavolo); }
    public int getLastStartIndex() { return lastStartIndex; }
}