package jtresette;

import model.Statistiche;
import services.StatisticheRep;
import view.MainMenu.MainMenu;

public class JTresette {
    public static void main(String[] args) {
        StatisticheRep rep = new StatisticheRep();
        Statistiche stat = rep.loadStats();
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    new MainMenu(stat,rep).setVisible(true);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
