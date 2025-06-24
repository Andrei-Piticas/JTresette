package model;

import model.carta.Carta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class Mano {
    private final List<Carta> carte = new ArrayList<>();

    public Mano(){

    }
    public Mano(Collection<Carta> iniziali) {
        carte.addAll(iniziali);

    }


    public void addCarte(List<Carta> nuove) {
        carte.addAll(nuove);
    }

    public void addCarta(Carta c){
            carte.add(c);
    }

    public void gioca(Carta c){
            if(!carte.remove(c)){
                throw new IllegalArgumentException("Nessuna carta nella mano" + c);
            }
    }


    public int size(){
            return carte.size();
    }


    public Stream<Carta> stream(){
            return carte.stream();
    }


    public List<Carta> getCarte(){
            return List.copyOf(carte);
    }
}
