package model.services;

import model.Statistiche;
import org.json.JSONObject;

import java.io.*;


/** Classe per la gestione delle statistiche del gioco,che ha come unico scopo caricare e salvare le statistiche in un file JSON.
 */
public class StatisticheRep {
    private static final String FILE_PATH = "stats.json";


    /**
     * Carica le statistiche dal file JSON. Se il file non esiste, crea un nuovo oggetto Statistiche vuoto e lo salva.
     */
    public Statistiche loadStats() {
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            Statistiche vuoto = new Statistiche();
            saveStats(vuoto);
            return vuoto;
        }

        /* Carica le statistiche dal file JSON
         */
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject obj = new JSONObject(sb.toString());
            return new Statistiche(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return new Statistiche();
        }
    }


    /**
     * Salva le statistiche nel file JSON.
     */
    public void saveStats(Statistiche stats) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            JSONObject obj = stats.toJSON();
            writer.write(obj.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
