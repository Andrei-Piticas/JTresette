package view.Game;

import opponent.BotPlayer;
import view.GameUI;
import jtresette.*;
import model.carta.Carta;
import model.carta.Seme;
import model.carta.Valore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GamePanel extends JPanel implements GameUI {

    // Attributi Grafici
    private final Map<String, ImageIcon> cardImages = new HashMap<>();
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 125;

    // Aree di Gioco e Mappa per la Corretta Disposizione
    private JPanel southPlayerArea, northPlayerArea, westPlayerArea, eastPlayerArea, centerTableArea;
    private Map<Giocatore, JPanel> playerAreaMap;

    // Attributi di Gioco
    private Partita partita;
    private Carta cartaSceltaDallUmano;
    private final Object lock = new Object();

    public GamePanel() {
        super(new BorderLayout(10, 10));
        loadCardImages();
        initializePlayerAreas();
        this.add(northPlayerArea, BorderLayout.NORTH);
        this.add(southPlayerArea, BorderLayout.SOUTH);
        this.add(westPlayerArea, BorderLayout.WEST);
        this.add(eastPlayerArea, BorderLayout.EAST);
        this.add(centerTableArea, BorderLayout.CENTER);
        this.setBackground(new Color(34, 139, 34));
    }

    // NUOVA LOGICA DI AVVIO PARTITA
    public void startNewGame() {
        // 1. Crea i singoli giocatori
        Giocatore umano = new GiocatoreUmano(this);
        Giocatore botOvest = new BotPlayer();
        Giocatore botNord = new BotPlayer();
        Giocatore botEst = new BotPlayer();

        // 2. Mappa ogni giocatore alla sua area grafica corretta
        playerAreaMap = new HashMap<>();
        playerAreaMap.put(umano, southPlayerArea);
        playerAreaMap.put(botOvest, westPlayerArea);
        playerAreaMap.put(botNord, northPlayerArea);
        playerAreaMap.put(botEst, eastPlayerArea);

        // 3. Crea la lista dei giocatori nell'ORDINE DI GIOCO CORRETTO (ANTI-ORARIO)
        // Sud -> Est -> Nord -> Ovest
        List<Giocatore> giocatoriInOrdineDiGioco = List.of(umano, botEst, botNord, botOvest);

        // 4. Avvia la partita con la lista ordinata per la logica
        partita = new Partita(giocatoriInOrdineDiGioco);
        partita.getGioco().setObserver(this);

        GameWorker gameWorker = new GameWorker();
        gameWorker.execute();
    }

    private class GameWorker extends SwingWorker<String, Void> {
        @Override
        protected String doInBackground() throws Exception {
            return partita.eseguiPartita();
        }

        @Override
        protected void done() {
            try {
                String messaggioFinale = partita.calcolaVincitore();
                JOptionPane.showMessageDialog(GamePanel.this,
                        "Partita terminata!\n" + messaggioFinale,
                        "Fine Partita", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update() {
        SwingUtilities.invokeLater(this::updateGUI);
    }

    @Override
    public Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                updateGUI();
                enableCardListeners(mano);
            });
        } catch (Exception e) { e.printStackTrace(); }

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        disableCardListeners();
        return cartaSceltaDallUmano;
    }

    // NUOVA LOGICA DI AGGIORNAMENTO GRAFICO
    public void updateGUI() {
        if (partita == null || partita.getGioco() == null || playerAreaMap == null) return;

        // Pulisci tutte le aree
        playerAreaMap.values().forEach(JPanel::removeAll);
        centerTableArea.removeAll();

        // Disegna le mani dei giocatori nelle loro aree corrette
        List<Giocatore> giocatoriInGioco = partita.getGioco().getGiocatori();
        for (Giocatore player : giocatoriInGioco) {
            JPanel area = playerAreaMap.get(player);
            if (area != null) {
                drawPlayerHand(player, area, player instanceof GiocatoreUmano);
            }
        }

        drawTable();

        revalidate();
        repaint();
    }

    // NUOVA LOGICA PER DISEGNARE IL TAVOLO
    private void drawTable() {
        if (partita.getGioco().getTavolo().isEmpty()) return;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        Partita2v2 gioco = partita.getGioco();
        List<Carta> carteSulTavolo = gioco.getTavolo();
        List<Giocatore> giocatoriInLogica = gioco.getGiocatori();
        int primoGiocatoreIndex = gioco.getLastStartIndex();

        for (int i = 0; i < carteSulTavolo.size(); i++) {
            Carta carta = carteSulTavolo.get(i);
            Giocatore giocatoreCheHaGiocato = giocatoriInLogica.get((primoGiocatoreIndex + i) % 4);

            // Determina la posizione grafica basandosi sull'oggetto Giocatore
            if (giocatoreCheHaGiocato instanceof GiocatoreUmano) {
                gbc.gridx = 1; gbc.gridy = 2; // Sud
            } else if (playerAreaMap.get(giocatoreCheHaGiocato) == westPlayerArea) {
                gbc.gridx = 0; gbc.gridy = 1; // Ovest
            } else if (playerAreaMap.get(giocatoreCheHaGiocato) == northPlayerArea) {
                gbc.gridx = 1; gbc.gridy = 0; // Nord
            } else { // East
                gbc.gridx = 2; gbc.gridy = 1; // Est
            }
            centerTableArea.add(new JLabel(cardImages.get(getCardImageName(carta))), gbc);
        }
    }

    // --- METODI ESISTENTI (nessuna modifica necessaria) ---

    private void initializePlayerAreas() {
        southPlayerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, -35, 10));
        southPlayerArea.setPreferredSize(new Dimension(0, CARD_HEIGHT + 20));
        northPlayerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, -35, 10));
        northPlayerArea.setPreferredSize(new Dimension(0, CARD_HEIGHT + 20));
        westPlayerArea = new JPanel(new GridLayout(0, 1, 5, -90));
        westPlayerArea.setPreferredSize(new Dimension(CARD_WIDTH + 40, 0));
        eastPlayerArea = new JPanel(new GridLayout(0, 1, 5, -90));
        eastPlayerArea.setPreferredSize(new Dimension(CARD_WIDTH + 40, 0));
        centerTableArea = new JPanel(new GridBagLayout());
        centerTableArea.setBackground(new Color(0, 80, 0));
        southPlayerArea.setOpaque(false);
        northPlayerArea.setOpaque(false);
        westPlayerArea.setOpaque(false);
        eastPlayerArea.setOpaque(false);
    }

    private void drawPlayerHand(Giocatore player, JPanel area, boolean isHuman) {
        if (player.getCarte() == null) return;
        for (Carta card : player.getCarte()) {
            String cardName = isHuman ? getCardImageName(card) : "back_carta";
            ImageIcon icon = cardImages.get(cardName);
            if (icon != null) {
                area.add(new JLabel(icon));
            }
        }
    }

    private void enableCardListeners(List<Carta> mano) {
        Component[] components = southPlayerArea.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (i < mano.size() && components[i] instanceof JLabel) {
                JLabel cardLabel = (JLabel) components[i];
                Carta carta = mano.get(i);
                cardLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                cardLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        synchronized (lock) {
                            cartaSceltaDallUmano = carta;
                            lock.notify();
                        }
                    }
                });
            }
        }
    }

    private void disableCardListeners() {
        for (Component comp : southPlayerArea.getComponents()) {
            comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            for (MouseListener ml : comp.getMouseListeners()) {
                comp.removeMouseListener(ml);
            }
        }
    }

    private void loadCardImages() {
        String imagePath = "/images/carte/";
        for (Valore valore : Valore.values()) {
            for (Seme seme : Seme.values()) {
                String cardName = valore.name() + seme.name();
                String fileName = cardName + ".png";
                java.net.URL imgURL = getClass().getResource(imagePath + fileName);
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(imgURL);
                    Image scaled = icon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
                    cardImages.put(cardName, new ImageIcon(scaled));
                }
            }
        }
        String backName = "back_carta";
        java.net.URL backURL = getClass().getResource(imagePath + backName + ".png");
        if (backURL != null) {
            ImageIcon icon = new ImageIcon(backURL);
            Image scaled = icon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
            cardImages.put(backName, new ImageIcon(scaled));
        }
    }

    private String getCardImageName(Carta carta) {
        return carta.getValore().name() + carta.getSeme().name();
    }
}