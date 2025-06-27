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

    public Partita2v2(List<Giocatore> players){
        this.giocatori = players;
        this.mazzo = new Mazzo();
        this.punteggi = new float[4];
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

    public int eseguiRound(){
        // nuova tavolo/pos per questo singolo round
        List<Carta> tavoloRound = new ArrayList<>(4);
        List<Integer> pos = new ArrayList<>(4);

        // ciascun giocatore
        for (int i = 0; i < 4; i++){
            int idx = turnoIndex;
            Giocatore g = giocatori.get(idx);
            Carta c;
            // rispetto del seme di apertura
            while (true){
                c = g.giocaCarta(tavoloRound);
                Seme semeGuida = tavoloRound.isEmpty() ? null : tavoloRound.get(0).seme();
                if (semeGuida != null){
                    boolean haSeme = g.getCarte().stream()
                            .anyMatch(x -> x.seme() == semeGuida);
                    if (haSeme && c.seme() != semeGuida){
                        g.riceviCarta(c);
                        continue;
                    }
                }
                break;
            }
            tavoloRound.add(c);
            pos.add(idx);
            turnoIndex = (turnoIndex + 1) % 4;
        }

        // determinazione vincitore del round
        int migliore = 0;
        for (int j = 1; j < 4; j++){
            if (tavoloRound.get(j).valore().getRanking() >
                    tavoloRound.get(migliore).valore().getRanking()){
                migliore = j;
            }
        }
        int vincitore = pos.get(migliore);

        // calcolo punti raccolti
        float puntiRound = (float) tavoloRound.stream()
                .map(Carta::valore)
                .mapToDouble(Valore::getPunti)
                .sum();

        // ↓↓↓ memorizzo nei campi d’istanza ↓↓↓
        this.ultimoVincitore   = vincitore;
        this.puntiUltimoRound  = puntiRound;

        // aggiorno il punteggio e il conteggio dei round
        punteggi[vincitore] += puntiRound;
        contaRound++;

        // bonus di fine match
        if (contaRound == 10){
            punteggi[vincitore] += 1f;
        }

        // il vincitore apre il round successivo
        turnoIndex = vincitore;
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
}
