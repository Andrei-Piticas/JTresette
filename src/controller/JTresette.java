
package controller;

import model.Statistiche;
import model.services.StatisticheRep;
import view.MainMenu.MainMenu;

import javax.swing.*;

public class JTresette {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // se hai un costruttore di Statistiche/Repo senza parametri:
                Statistiche stat = new Statistiche();
                StatisticheRep repo = new StatisticheRep();

                new MainMenu(stat, repo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
