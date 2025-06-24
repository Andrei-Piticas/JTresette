package view;

import model.carta.Carta;

import java.util.List;

public interface GameUI {
     Carta promptGiocaCarta(List<Carta> mano , List<Carta> tavolo);


}
