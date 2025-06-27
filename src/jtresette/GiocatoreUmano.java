package jtresette;

import model.carta.Carta;
import view.GameUI;

import java.util.ArrayList;
import java.util.List;

public class GiocatoreUmano implements Giocatore {
    private final GameUI ui;
    private final List<Carta> mano = new ArrayList<>();

    public GiocatoreUmano(GameUI ui) {
        this.ui = ui;
    }

    @Override
    public void riceviCarta(Carta c) {
        // quando il mazzo distribuisce, memorizzo la carta nella mia mano
        mano.add(c);
    }

    @Override
    public List<Carta> getCarte() {
        // restituisco una copia per sicurezza
        return new ArrayList<>(mano);
    }

    @Override
    public Carta giocaCarta() {
        return null;
    }

    @Override
    public Carta giocaCarta(List<Carta> tavolo) {
        // chiedo alla GUI quale carta vuoi giocare
        Carta scelta = ui.promptGiocaCarta(new ArrayList<>(mano), tavolo);
        // rimuovo la carta dalla mia mano interna
        mano.remove(scelta);
        return scelta;
    }
}
