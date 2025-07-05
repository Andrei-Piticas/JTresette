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
        // ① debug e salva chi apre
        System.out.println("⏺ eseguiRound #" + contaRound + " startIndex=" + turnoIndex);
        lastStartIndex = turnoIndex;

        List<Carta> tavoloRound = new ArrayList<>(4);
        List<Integer> pos         = new ArrayList<>(4);

        // ② ciascun giocatore in ordine
        for (int i = 0; i < 4; i++) {
            int idx = turnoIndex;
            Giocatore g = giocatori.get(idx);

            // prendi la mano corrente e prova a giocare
            List<Carta> mano = g.getCarte();
            Carta c = null;
            if (!mano.isEmpty()) {
                c = g.giocaCarta(new ArrayList<>(tavoloRound));
                if (c == null) {
                    // fallback immediato
                    System.err.println("⚠️ Giocatore " + idx + " ha restituito null, uso fallback");
                    c = mano.get(0);
                }


                Seme semeGuida = tavoloRound.isEmpty() ? null : tavoloRound.get(0).seme();
                if (!(g instanceof jtresette.GiocatoreUmano) && semeGuida != null) {
                    boolean haSeme = mano.stream()
                            .anyMatch(x -> x.seme() == semeGuida);
                    if (haSeme && c.seme() != semeGuida) {
                        // enforcement solo per bot
                        g.riceviCarta(c);
                        c = mano.stream()
                                .filter(x -> x.seme() == semeGuida)
                                .findFirst()
                                .orElse(mano.get(0));
                    }
                }


            }

            if (c == null) {
                throw new IllegalStateException("Giocatore " + idx + " non aveva carte!");
            }

            tavoloRound.add(c);
            pos.add(idx);
            turnoIndex = (turnoIndex + 1) % 4;
        }

        // ③ determina vincitore del round
        int migliore = 0;
        for (int j = 1; j < 4; j++) {
            if (tavoloRound.get(j).valore().getRanking() >
                    tavoloRound.get(migliore).valore().getRanking()) {
                migliore = j;
            }
        }
        int vincitore = pos.get(migliore);

        // ④ calcola e salva punti
        float puntiRound = (float) tavoloRound.stream()
                .mapToDouble(carta -> carta.valore().getPunti())
                .sum();
        this.ultimoVincitore  = vincitore;
        this.puntiUltimoRound = puntiRound;

        punteggi[vincitore] += puntiRound;
        contaRound++;
        if (contaRound == 10) punteggi[vincitore] += 1f;

        // ⑤ chi apre il prossimo round
        turnoIndex = vincitore;

        // log e salva le carte giocate
        System.out.println("   → tavoloRound = " + tavoloRound);
        this.lastTavoloRound = new ArrayList<>(tavoloRound);

        return vincitore;
    }

    public void determinaPrimoGiocatore() {
        // Trova il giocatore con la carta più alta
        int migliore = 0;
        Carta cartaMigliore = null;

        for (int i = 0; i < 4; i++) {
            List<Carta> mano = giocatori.get(i).getCarte();
            for (Carta c : mano) {
                if (cartaMigliore == null ||
                        c.valore().getRanking() > cartaMigliore.valore().getRanking()) {
                    cartaMigliore = c;
                    migliore = i;
                }
            }
        }
        // Imposta il turno iniziale
        turnoIndex = migliore;
        System.out.println("Primo giocatore: " + migliore);
    }


    // ─── GETTER ───

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

    public int getVincitoreTurno() {
        return ultimoVincitore;
    }

    public int getLastStartIndex() {
        return lastStartIndex;
    }

    public List<Carta> getLastTavoloRound() {
        return List.copyOf(lastTavoloRound);
    }
}
