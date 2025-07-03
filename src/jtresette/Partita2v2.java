package jtresette;

import model.Mano;
import model.Mazzo;
import model.carta.Carta;
import model.carta.Seme;
import model.carta.Valore;

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


    public Partita2v2(List<Giocatore> players){
        this.giocatori = players;
        this.mazzo = new Mazzo();
        this.punteggi = new float[4];
        this.lastTavoloRound = new ArrayList<>();
    }



    public void inizio(){
        mazzo.mescola();
        List<Mano> mani = mazzo.distribuzione();
        for (int i = 0; i < 4; i++){
            Giocatore g = giocatori.get(i);
            for (Carta c : mani.get(i).getCarte()){
                g.riceviCarta(c);
            }
        }
        Arrays.fill(punteggi, 0f);
        contaRound = 0;
        turnoIndex = 0;
    }

    public Carta giocaTurno(){
        Giocatore g = giocatori.get(turnoIndex);
        Carta c = g.giocaCarta(List.copyOf(tavolo));
        tavolo.add(c);
        turnoIndex = (turnoIndex + 1) % 4;
        return c;
    }

    public int eseguiRound() {
        System.out.println("⏺ eseguiRound #" + contaRound + " startIndex=" + turnoIndex);
        lastStartIndex = turnoIndex;

        List<Integer> order = new ArrayList<>(4);
        tavolo.clear();

        for (int i = 0; i < 4; i++) {
            int idx = turnoIndex;
            giocaTurno();
            order.add(idx);
        }

        return completaRound(order);
    }

    /** Calcola vincitore e aggiorna punteggi usando le carte in 'tavolo'. */
    public int completaRound(List<Integer> order) {
        if (tavolo.size() != 4) {
            throw new IllegalStateException("Round incompleto: carte sul tavolo = " + tavolo.size());
        }

        List<Carta> tavoloRound = new ArrayList<>(tavolo);
        int migliore = 0;
        for (int j = 1; j < 4; j++) {
            if (tavoloRound.get(j).valore().getRanking() > tavoloRound.get(migliore).valore().getRanking()) {
                migliore = j;
            }
        }
        int vincitore = order.get(migliore);

        float puntiRound = (float) tavoloRound.stream().mapToDouble(c -> c.valore().getPunti()).sum();
        this.ultimoVincitore  = vincitore;
        this.puntiUltimoRound = puntiRound;

        punteggi[vincitore] += puntiRound;
        contaRound++;
        if (contaRound == 10) punteggi[vincitore] += 1f;

        turnoIndex = vincitore;
        lastTavoloRound = tavoloRound;
        lastStartIndex = order.get(0);
        tavolo.clear();
        System.out.println("   → tavoloRound = " + tavoloRound);
        return vincitore;
    }


    // ─── GETTER ───
    public int getUltimoVincitore() {
        return ultimoVincitore;
    }

    public float getPuntiUltimoRound() {
        return puntiUltimoRound;
    }

    public int getNumeroRound() {
        return contaRound;
    }

    public int getTurnoIndex(){
        return turnoIndex;
    }

    public static float getPunteggio(int i) {
        return punteggi[i];
    }

    public List<Giocatore> getGiocatori() {
        return List.copyOf(giocatori);
    }
    public List<Carta> getTavolo() {
        return List.copyOf(tavolo);
    }


    public List<Carta> getLastTavoloRound() {
        return List.copyOf(lastTavoloRound);
    }

    /** Restituisce l’indice (0..3) del giocatore che ha aperto l’ultimo round */
    public int getLastStartIndex() {
        return lastStartIndex;
    }

}
