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

    private JPanel southPlayerArea, northPlayerArea, westPlayerArea, eastPlayerArea, centerTableArea;
    private final Map<String, ImageIcon> cardImages = new HashMap<>();
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 125;

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

    public void startNewGame() {
        List<Giocatore> giocatori = new ArrayList<>();
        giocatori.add(new GiocatoreUmano(this));
        giocatori.add(new BotPlayer());
        giocatori.add(new BotPlayer());
        giocatori.add(new BotPlayer());

        partita = new Partita(giocatori);
        partita.getGioco().setObserver(this);

        GameWorker gameWorker = new GameWorker();
        gameWorker.execute();
    }

    private class GameWorker extends SwingWorker<String, Void> {
        @Override
        protected String doInBackground() throws Exception {
            // La logica di gioco originale guida la partita
            return partita.eseguiPartita();
        }

        @Override
        protected void done() {
            try {
                String risultato = get();
                // A fine partita, usa la logica di calcolo del vincitore
                String messaggioFinale = partita.calcolaVincitore();
                JOptionPane.showMessageDialog(GamePanel.this,
                        "Partita terminata!\n" + messaggioFinale,
                        "Fine Partita", JOptionPane.INFORMATION_MESSAGE);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo dall'interfaccia GameUI per ricevere notifiche dalla logica
    @Override
    public void update() {
        SwingUtilities.invokeLater(this::updateGUI);
    }

    @Override
    public Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo) {
        try {
            // Forza il thread di gioco ad attendere che la grafica sia pronta
            SwingUtilities.invokeAndWait(() -> {
                updateGUI();
                enableCardListeners(mano);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }

        // Ora attende in sicurezza il click dell'utente
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        disableCardListeners();
        return cartaSceltaDallUmano;
    }

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

    public void updateGUI() {
        if (partita == null || partita.getGioco() == null) return;

        southPlayerArea.removeAll();
        northPlayerArea.removeAll();
        westPlayerArea.removeAll();
        eastPlayerArea.removeAll();
        centerTableArea.removeAll();

        List<Giocatore> giocatoriInGioco = partita.getGioco().getGiocatori();

        if (giocatoriInGioco != null && giocatoriInGioco.size() == 4) {
            drawPlayerHand(giocatoriInGioco.get(0), southPlayerArea, true);
            drawPlayerHand(giocatoriInGioco.get(1), westPlayerArea, false);
            drawPlayerHand(giocatoriInGioco.get(2), northPlayerArea, false);
            drawPlayerHand(giocatoriInGioco.get(3), eastPlayerArea, false);
        }

        drawTable();

        revalidate();
        repaint();
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

    private void drawTable() {
        if (partita == null || partita.getGioco() == null || partita.getGioco().getTavolo() == null) return;

        Partita2v2 gioco = partita.getGioco();
        List<Carta> carteSulTavolo = gioco.getTavolo();
        if (carteSulTavolo.isEmpty()) return;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        int primoGiocatoreDelTurno = gioco.getLastStartIndex();

        for (int i = 0; i < carteSulTavolo.size(); i++) {
            Carta carta = carteSulTavolo.get(i);
            int playerIndex = (primoGiocatoreDelTurno + i) % 4;

            switch (playerIndex) {
                case 0: gbc.gridx = 1; gbc.gridy = 2; break; // SUD
                case 1: gbc.gridx = 0; gbc.gridy = 1; break; // OVEST
                case 2: gbc.gridx = 1; gbc.gridy = 0; break; // NORD
                case 3: gbc.gridx = 2; gbc.gridy = 1; break; // EST
            }
            centerTableArea.add(new JLabel(cardImages.get(getCardImageName(carta))), gbc);
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