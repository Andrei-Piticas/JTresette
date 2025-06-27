package model.carta;

public record Carta(Seme seme,Valore valore) {
    public String getNomeFile() {
        return valore.name() + seme.name() + ".png";
    }


}
