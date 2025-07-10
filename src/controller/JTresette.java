package controller;

import model.Statistiche;
import model.services.StatisticheRep;
import view.MainMenu.MainMenu;

import javax.swing.*;

/**
  La classe JTresette è il punto di ingresso principale dell'applicazione.
  Il suo unico scopo è avviare il gioco, creando la finestra del menu principale
 */
public class JTresette {

    /**
     Il metodo main è il punto di partenza dell'esecuzione del programma.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Carica le statistiche esistenti o ne crea di nuove se non esistono
                StatisticheRep repo = new StatisticheRep();
                Statistiche stat = repo.loadStats();

                // Crea e visualizza la finestra del menu principale,
                // passando i dati delle statistiche.
                new MainMenu(stat, repo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}