package model;

import model.carta.Carta;
import model.carta.Seme;
import model.carta.Valore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
  Rappresenta il mazzo di 40 carte da gioco e gestisce la creazione di un mazzo standard, la sua mescolata
  e la distribuzione delle carte ai giocatori all'inizio di una partita.
 */

public class Mazzo {
    private final List<Carta> carte;

    /*
    Crea un mazzo completo e ordinato di 40 carte,
    iterando attraverso tutti i semi e tutti i valori.
    /*
     */
    public Mazzo(){
            carte = new ArrayList<>(40);
                for(Seme s : Seme.values())
                    for (Valore v : Valore.values())
                        carte.add(new Carta(s,v));
    }


    /*Mescola le carte nel mazzo in modo casuale.*/
    public void mescola(){
        Collections.shuffle(carte);

    }

    /*pesca la prima disponibile e la rimuove dal mazzo*/
    public Carta pesca(){
            if(carte.isEmpty()) {
                throw new IllegalStateException("Il mazzo e esaurito");
            }
            return carte.remove(0);

    }


    /*Distrubuisce le carte ai giocatori,attraverso l'utilizzo di stream,sottoforma di lista*/
    public List<Mano>distribuzione(){
            List<Mano> mani = Stream.generate(Mano::new)
                    .limit(4)
                    .toList();


            /*ciclo che simula la situazione dei giocatori*/
            for(int count : new int[]{4,3,3}){
                    for(Mano m:mani){
                        List<Carta> pescate = new ArrayList<>(count);
                        for(int i=0;i<count;i++){
                                pescate.add(pesca());
                        }
                        m.addCarte(pescate);
                    }
            }
            return mani;

    }


}
