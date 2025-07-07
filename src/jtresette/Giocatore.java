package jtresette;

import model.carta.Carta;

import java.util.List;

public interface Giocatore {
    void riceviCarta(Carta c);
    Carta giocaCarta(List<Carta> tavolo);
    List<Carta> getCarte();
    void svuotaMano();
    void setNome(String nome);



    Carta giocaCarta();

    Carta giocaCarta(List<Carta> carte, List<Carta> tavolo);

    String getNome(String nome);
    String getNome();
}
