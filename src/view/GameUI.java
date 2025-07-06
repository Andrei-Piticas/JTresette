package view;

import model.carta.Carta;

import java.util.List;

public interface GameUI {
     Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo);
     void update(); // <-- AGGIUNGI QUESTO METODO SE NON ESISTE

}
