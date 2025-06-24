package model.carta;

public record Carta(Seme seme,Valore valore) {
    public String getNomeFile() {
        String v = valore.name().toLowerCase(); // es. "asso", "due", ...
        String s = seme   .name().toLowerCase(); // es. "cuori", "fiori", ...
        return v + "_" + s + ".png";
    }

}
