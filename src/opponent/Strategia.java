package opponent;

import model.Mano;
import model.carta.Carta;
import model.carta.Valore;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Strategia {


    public Carta scegliCarta(Mano mano, List<Carta> tavolo){
        List<Carta> carteMano = mano.getCarte();

        int miglioreT = tavolo.stream()
                .map(Carta::valore)
                .mapToInt(Valore::getRanking)
                .max()
                .orElse(0);

        Optional<Carta> minVittoria = carteMano.stream()
                .filter(carta -> carta.valore().getRanking() > miglioreT)
                .min(Comparator.comparing(carta -> carta.valore().getRanking()));

        if (minVittoria.isPresent()){
            Carta scelta = minVittoria.get();

            return scelta;

        }

        Carta scarto = carteMano.stream()
                .min(Comparator.comparingDouble(carta -> carta.valore().getPunti()))
                .orElseThrow();


        return scarto;

    }
            //

}
