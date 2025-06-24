package model;

import model.carta.Carta;
import model.carta.Seme;
import model.carta.Valore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Mazzo {
    private final List<Carta> carte;


    public Mazzo(){
            carte = new ArrayList<>(40);
                for(Seme s : Seme.values())
                    for (Valore v : Valore.values())
                        carte.add(new Carta(s,v));
    }


    public void mescola(){
        Collections.shuffle(carte);

    }


    public Carta pesca(){
            if(carte.isEmpty()) {
                throw new IllegalStateException("Il mazzo e esaurito");
            }
            return carte.remove(0);

    }

    public List<Mano>distribuzione(){
            List<Mano> mani = Stream.generate(Mano::new)
                    .limit(4)
                    .toList();

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


    public int carteRimanenti(){
            return carte.size();
    }

}
