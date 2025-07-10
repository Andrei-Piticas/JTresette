package model.opponent;

import model.carta.Carta;
import model.Mano;
import model.carta.Valore;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Strategia {

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

        // fallback sicuro: scarta la carta con meno punti,
        // orElse(0) non può più esplodere perché la lista non è vuota
        return carteMano.stream()
                .min(Comparator.comparingDouble(c -> c.valore().getPunti()))
                .orElse(carteMano.get(0));
    }

}
