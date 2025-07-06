package opponent;

import jtresette.Giocatore;
import model.Mano;
import model.carta.Carta;
import model.carta.Seme;

import java.util.List;

public class BotPlayer implements Giocatore {

    private final Mano mano = new Mano();
    private final Strategia strategia = new Strategia();



    @Override
    public void riceviCarta(Carta c) {
            mano.addCarta(c);
    }

    @Override
    public Carta giocaCarta(List<Carta> tavolo) {
        Seme semeGuida = tavolo.isEmpty() ? null : tavolo.get(0).seme();
        List<Carta> cartePossibili = mano.getCarte().stream()
                .filter(c -> semeGuida == null || c.seme() == semeGuida)
                .toList();
        if (cartePossibili.isEmpty()) {
            cartePossibili = mano.getCarte();
        }

        Mano manoTemp = new Mano(cartePossibili);
        Carta scelta = strategia.scegliCarta(manoTemp, tavolo);

        // *** NUOVO: fallback se la strategia restituisce null ***
        if (scelta == null) {
            // prendi semplicemente la prima carta disponibile
            scelta = cartePossibili.get(0);
        }

        mano.gioca(scelta);
        return scelta;
    }


    @Override
    public List<Carta> getCarte() {
        return mano.getCarte();
    }

    @Override
    public Carta giocaCarta() {
        return null;
    }

    @Override
    public Carta giocaCarta(List<Carta> carte, List<Carta> tavolo) {
        return giocaCarta(tavolo);
    }

    @Override
    public String getNome(String nome) {
        return "Bot";
    }
}
