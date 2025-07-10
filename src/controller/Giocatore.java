package controller;
import model.carta.Carta;
import java.util.List;

/**
   L'interfaccia Giocatore definisce il comportamento base per ogni componente del gioco,
   sia umano che bot.
   Stabilisce un contatto che permette di interagire con le carte e il tavolo da gioco.

 */
public interface Giocatore {

    /**
      Aggiunge una carta alla mano del giocatore.
     */
    void riceviCarta(Carta c);

    /**
      Metodo principale per far giocare una carta al giocatore.
      La decisione su quale carta giocare può dipendere dallo stato rispettivo del tavolo.
     */
    Carta giocaCarta(List<Carta> tavolo);

    /**
     Restituisce una copia della mano di carte attuale del giocatore.
     */
    List<Carta> getCarte();

    Carta giocaCarta();

    Carta giocaCarta(List<Carta> carte, List<Carta> tavolo);

    String getNome(String nome);

    /**
     Svuota la mano del giocatore.
     Utile per resettare lo stato tra una partita e l'altra.
     */
    void svuotaMano();

    /**
     Imposta il nome del giocatore.
    */
    void setNome(String nome);

    /**
     Restituisce il nome del giocatore.
     */
    String getNome();
}