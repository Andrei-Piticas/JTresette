package jtresette;

import model.Mano;
import model.carta.Carta;
import view.GameUI;

import java.util.List;

public class GiocatoreUmano implements Giocatore{
    private final Mano mano = new Mano();
    private final GameUI ui;

    public GiocatoreUmano(GameUI ui) {
        this.ui = ui;
    }


    @Override
    public void riceviCarta(Carta c) {
            mano.addCarta(c);
    }



    @Override
    public List<Carta> getCarte() {
        return mano.getCarte();
    }

    public Carta giocaCarta() {
        return null;
    }


    @Override
    public Carta giocaCarta(List<Carta> tavolo) {
        Carta scelta = ui.promptGiocaCarta(mano.getCarte(),tavolo);
        mano.gioca(scelta);
        return scelta;

    }
}
