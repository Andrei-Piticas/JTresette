package jtresette;

import model.Mano;
import model.Mazzo;
import model.carta.Carta;
import model.carta.Seme;
import model.carta.Valore;
import view.GameUI; // You may need to adjust this import based on your package structure

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Partita2v2 {
    private final List<Giocatore> giocatori;
    private final Mazzo mazzo;
    private final List<Carta> tavolo = new ArrayList<>();
    private int turnoIndex = 0;
    private static float[] punteggi;
    private int contaRound = 0;
    private int ultimoVincitore;
    private float puntiUltimoRound;
    private List<Carta> lastTavoloRound = new ArrayList<>();
    private int lastStartIndex;
    private GameUI observer;

    public Partita2v2(List<Giocatore> players) {
        this.giocatori = players;
        this.mazzo = new Mazzo();
        this.punteggi = new float[4];
        this.lastTavoloRound = new ArrayList<>();
    }

    public void inizio() {
        mazzo.mescola();
        List<Mano> mani = mazzo.distribuzione();
        for (int i = 0; i < 4; i++) {
            Giocatore g = giocatori.get(i);
            for (Carta c : mani.get(i).getCarte()) {
                g.riceviCarta(c);
            }
        }
        Arrays.fill(punteggi, 0f);
        contaRound = 0;
        turnoIndex = 0; // Or determine first player logically
    }

    public Carta giocaTurno() {
        Giocatore g = giocatori.get(turnoIndex);
        Carta c = g.giocaCarta(List.copyOf(tavolo));
        tavolo.add(c);
        if (observer != null) {
            observer.update();
            try {
                // Piccola pausa per rendere le mosse visibili
                if (!(g instanceof GiocatoreUmano)) {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        turnoIndex = (turnoIndex + 1) % 4;
        return c;
    }

    // THIS IS A NEW METHOD
    public void calcolaPuntiEManoSuccessiva() {
        // Determine winner of the hand
        int migliore = 0;
        Seme semeGuida = tavolo.get(0).getSeme();
        for (int j = 1; j < tavolo.size(); j++) {
            Carta cartaCorrente = tavolo.get(j);
            Carta cartaMigliore = tavolo.get(migliore);

            // If the current card has the leading suit and the best card doesn't, current wins
            if (cartaCorrente.getSeme() == semeGuida && cartaMigliore.getSeme() != semeGuida) {
                migliore = j;
            }
            // If both cards have the leading suit, compare ranking
            else if (cartaCorrente.getSeme() == semeGuida && cartaMigliore.getSeme() == semeGuida) {
                if (cartaCorrente.getValore().getRanking() > cartaMigliore.getValore().getRanking()) {
                    migliore = j;
                }
            }
            // If neither has the leading suit, the first card remains the best
        }

        int vincitore = (this.lastStartIndex + migliore) % 4;

        // Calculate and save points
        float puntiRound = (float) tavolo.stream()
                .mapToDouble(carta -> carta.getValore().getPunti())
                .sum();
        this.ultimoVincitore = vincitore;
        this.puntiUltimoRound = puntiRound;

        punteggi[vincitore] += puntiRound;
        contaRound++;
        if (contaRound == 10) punteggi[vincitore] += 1f; // Last hand point

        turnoIndex = vincitore; // The winner starts the next round
        this.lastTavoloRound = new ArrayList<>(tavolo);
    }

    // THIS IS A NEW METHOD
    public void svuotaTavolo() {
        this.tavolo.clear();
    }

    public void setObserver(GameUI observer) {
        this.observer = observer;
    }

    // THIS IS A NEW METHOD
    public void setLastStartIndex(int index) {
        this.lastStartIndex = index;
    }

    // --- GETTERS (existing) ---
    public float getPuntiUltimoRound() { return puntiUltimoRound; }
    public int getNumeroRound() { return contaRound; }
    public int getTurnoIndex() { return turnoIndex; }
    public static float getPunteggio(int i) { return punteggi[i]; }
    public List<Giocatore> getGiocatori() { return List.copyOf(giocatori); }
    public List<Carta> getTavolo() { return List.copyOf(tavolo); }
    public int getVincitoreTurno() { return ultimoVincitore; }
    public int getLastStartIndex() { return lastStartIndex; }
    public List<Carta> getLastTavoloRound() { return List.copyOf(lastTavoloRound); }
}