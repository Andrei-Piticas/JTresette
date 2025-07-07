package view;

import model.carta.Carta;

import java.util.List;

public interface GameUI {
     Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo);
     void update();

     void startNewGame();

     void mostraRiepilogoMano(String nomeVincitore, float punteggioSquadra1, float punteggioSquadra2);

}
