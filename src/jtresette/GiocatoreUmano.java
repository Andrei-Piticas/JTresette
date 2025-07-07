package jtresette;

import model.carta.Carta;
import view.GameUI;

import java.util.ArrayList;
import java.util.List;

public class GiocatoreUmano implements Giocatore {
    private final GameUI ui;
    protected final List<Carta> mano = new ArrayList<>();

    public GiocatoreUmano(GameUI ui) {
        this.ui = ui;
    }

    @Override
    public void riceviCarta(Carta c) {
        mano.add(c);
    }

    @Override
    public Carta giocaCarta(List<Carta> tavolo) {
        // chiedo alla GUI quale carta vuole giocare
        Carta scelta = ui.promptGiocaCarta(new ArrayList<>(mano), tavolo);
        // la rimuovo definitivamente dalla mia mano
        mano.remove(scelta);
        return scelta;
    }

    @Override
    public List<Carta> getCarte() {
        return new ArrayList<>(mano);
    }

    @Override
    public Carta giocaCarta() {
        // delega, se mai fosse invocato
        return giocaCarta(List.of());
    }

    @Override
    public Carta giocaCarta(List<Carta> carte, List<Carta> tavolo) {
        return null;
    }

    @Override
    public String getNome(String nome) {
        return "Tu";
    }

    // Add this method inside your GiocatoreUmano class

    @Override
    public void svuotaMano() {
        this.mano.clear();
    }


}
