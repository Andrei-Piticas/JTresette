package model.carta;

public enum Valore {
    DUE(9,1.0f/3),QUATTRO(1,0.0f),CINQUE(2,0.0f),SEI(3,0.0f),SETTE(4,0.0f),FANTE(5,1.0f/3),CAVALLO(6,1.0f/3),RE(7,1.0f/3),TRE(10,1.0f/3),ASSO(8,1.0f);


    private final int ranking;
    private final float punti;

    Valore(int ranking, float punti) {
        this.ranking = ranking;
        this.punti = punti;
    }

    public int getRanking(){
            return ranking;
    }

    public float getPunti() {
        return punti;
    }
}
