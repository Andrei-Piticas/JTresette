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
    private int turnoIndex=0;
    private static float[] punteggi;
    private int contaRound = 0 ;


    public Partita2v2(List<Giocatore> players){
            this.giocatori = players;
            this.mazzo = new Mazzo();
            this.punteggi = new float[4];

    }


    public void inizio(){
            mazzo.mescola();
            List<Mano> mani = mazzo.distribuzione();
            for (int i=0;i<4;i++){
                    Giocatore g = giocatori.get(i);
                    for (Carta c : mani.get(i).getCarte()){
                        g.riceviCarta(c);
                    }

            }
            Arrays.fill(punteggi,0f);
    }

    public Carta giocaTurno(){

            Giocatore g = giocatori.get(turnoIndex);
            Carta c = g.giocaCarta(List.copyOf(tavolo));
            tavolo.add(c);

            turnoIndex = (turnoIndex + 1)%4;
            return c;
    }

    public int eseguiRound(){
        List<Carta> tavolo = new ArrayList<>(4);
        List<Integer> pos = new ArrayList<>(4);

        for (int i=0;i<4;i++){
            int idx = turnoIndex;
            Giocatore g = giocatori.get(idx);
            Carta c;

            while(true){

                    c = g.giocaCarta(tavolo);
                    Seme semeGuida = tavolo.isEmpty() ? null : tavolo.get(0).seme();
                    if(semeGuida != null){
                        boolean semeSi = g.getCarte().stream()
                                .anyMatch(x -> x.seme() == semeGuida);

                        if(semeSi && c.seme()!=semeGuida){
                            g.riceviCarta(c);
                            continue;
                        }

                    }

                    break;

            }
            tavolo.add(c);
            pos.add(idx);
            turnoIndex = (turnoIndex +1)%4;

        }

        int migliore = 0;
        for(int j = 1 ; j < 4 ; j++){
            if(tavolo.get(j).valore().getRanking() > tavolo.get(migliore).valore().getRanking()){
                migliore = j;
            }
        }

        int vincitore = pos.get(migliore);

        float puntiRound = (float) tavolo.stream()
                .map(Carta::valore)
                .mapToDouble(Valore::getPunti)
                .sum();

        punteggi[vincitore] += puntiRound;
        contaRound++;

        if(contaRound == 10){
            punteggi[vincitore]+= 1f;
        }

        turnoIndex = vincitore;
        return vincitore;
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
