package view;

import model.carta.Carta;

import java.util.List;


/*L'interfaccia GameUI definisce il contratto di comunicazione tra la logica di gioco (Model)
 * e l'interfaccia grafica*/
public interface GameUI {

     //*Metodo chiamato dalla logica di gioco per richiedere l'input al giocatore umano.*/
     Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo);

    /* La logica di gioco lo invoca per segnalare
     che è avvenuto un cambiamento di stato e che l'interfaccia
       grafica deve essere ridisegnatab*/
     void update();



     void startNewGame();


     /*Mostra un messaggio di riepilogo temporaneo alla fine di ogni mano.*/
     void mostraRiepilogoMano(String nomeVincitore);

}
