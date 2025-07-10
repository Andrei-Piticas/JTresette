package model.opponent;

import model.carta.Carta;
import model.Mano;
import model.carta.Valore;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


/**
 * La classe Strategia contiene la logica per scegliere la carta da giocare
 * in base alla mano del bot e alle carte presenti sul tavolo seguendo le regole.
 */
public class Strategia {


    /*
     * Sceglie la carta migliore da giocare dalla mano del bot in base allo stato attuale del tavolo.
       Trova la carta con il ranking più alto attualmente sul tavolo.
       Cerca nella propria mano la carta più debole (ranking minimo).
       Se trova una carta vincente, la gioca.
       Se non può vincere, gioca la carta di minor valore in punti.
     */
    public Carta scegliCarta(Mano mano, List<Carta> tavolo) {
        List<Carta> carteMano = mano.getCarte();
        if (carteMano.isEmpty()) {
            throw new IllegalStateException("Il bot non ha più carte");
        }

        int miglioreT = tavolo.stream()
                .map(Carta::valore)
                .mapToInt(Valore::getRanking)
                .max()
                .orElse(0);

        Optional<Carta> minVittoria = carteMano.stream()
                .filter(c -> c.valore().getRanking() > miglioreT)
                .min(Comparator.comparingInt(c -> c.valore().getRanking()));
        if (minVittoria.isPresent()) {
            return minVittoria.get();
        }

        // Se non ci sono carte che possono vincere, gioca la carta con il valore minimo attraverso il comparatore
        return carteMano.stream()
                .min(Comparator.comparingDouble(c -> c.valore().getPunti()))
                .orElse(carteMano.get(0));
    }

}
