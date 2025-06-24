package jtresette;

import model.Squadra;

import java.util.List;

public class Partita {
    private final Partita2v2 gioco;


    public Partita(List<Giocatore> giocatori) {
        this.gioco = new Partita2v2(giocatori);
    }

    public String eseguiPartita() {
        int[] numeroPrese = {0, 0};
        Squadra squadraA = new Squadra("ANDREI", 0, 2);
        Squadra squadraB = new Squadra("BORL", 1, 3);
        gioco.inizio();

        int ultimo = -1;

        for (int round = 1; round <= 10; round++) {
            int vincitoreRound = gioco.eseguiRound();
            float puntiVincitore = gioco.getPunteggio(vincitoreRound);
            ultimo = vincitoreRound;

            if (vincitoreRound % 2 == 0) {
                numeroPrese[0]++;

            } else numeroPrese[1]++;

            float puntiA = squadraA.calcolaPunteggio(gioco);
            float puntiB = squadraB.calcolaPunteggio(gioco);
            System.out.printf("Round %d: giocatore %d vha vinto (%.2f pt). Squadre -> %s: %.2f /%s : %.2f%n",
                    round, vincitoreRound, puntiVincitore, squadraA.getNome(), puntiA, squadraB.getNome(), puntiB);


            if (puntiA >= 31f || puntiB >= 31) {
                System.out.println("Una delle due squadra ha raggiunto 31 punti,partita finita");
                break;
            }


        }


        float finaleA = squadraA.calcolaPunteggio(gioco);
        float finaleB = squadraB.calcolaPunteggio(gioco);
        String nomeVincitori;
        if (finaleA != finaleB) {
            nomeVincitori = finaleA > finaleB
                    ? squadraA.getNome() : squadraB.getNome();
        } else {
            if (numeroPrese[0] != numeroPrese[1]) {
                nomeVincitori = numeroPrese[0] > numeroPrese[1]
                        ? squadraA.getNome() : squadraB.getNome();
            } else {
                nomeVincitori = (ultimo % 2 == 0)
                        ? squadraA.getNome() : squadraB.getNome();
            }


        }

        System.out.printf("%nRISULTATO FINALE → %s: %.2f pt (%d prese) | %s: %.2f pt (%d prese)%nVince: %s%n",
                squadraA.getNome(), finaleA, numeroPrese[0],
                squadraB.getNome(), finaleB, numeroPrese[1],
                nomeVincitori);

        return nomeVincitori;
    }


}



