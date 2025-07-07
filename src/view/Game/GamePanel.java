package view.Game;

import opponent.BotPlayer;
import view.GameUI;
import jtresette.*;
import model.carta.Carta;
import model.carta.Seme;
import model.carta.Valore;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GamePanel extends JPanel implements GameUI {

    // --- ATTRIBUTI PER LA UI ---
    private RoundSummaryPanel summaryPanel;

    private JPanel southPlayerArea, northPlayerArea, westPlayerArea, eastPlayerArea, centerTableArea;
    private Map<Giocatore, JPanel> playerAreaMap;
    private final Map<String, ImageIcon> cardImages = new HashMap<>();
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 125;
    private Partita partita;
    private Carta cartaSceltaDallUmano;
    private final Object lock = new Object();

    public GamePanel() {
        super(new BorderLayout(5, 5));
        loadCardImages();
        initializePlayerAreas();

        // Layout di base semplice e funzionante
        this.add(northPlayerArea, BorderLayout.NORTH);
        this.add(southPlayerArea, BorderLayout.SOUTH);
        this.add(westPlayerArea, BorderLayout.WEST);
        this.add(eastPlayerArea, BorderLayout.EAST);
        this.add(centerTableArea, BorderLayout.CENTER);
        this.setBackground(new Color(34, 139, 34));

        // Aggiungi un listener per configurare il glass pane appena il pannello è visibile
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && this.isShowing()) {
                setupGlassPane();
            }
        });
    }

    // --- NUOVO METODO PER CONFIGURARE L'OVERLAY ---
    private void setupGlassPane() {
        // Trova la finestra principale (JFrame) che contiene questo pannello
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;

        // Crea il pannello di riepilogo
        summaryPanel = new RoundSummaryPanel(cardImages.get("BackPanel"));

        // Prendi il "pannello di vetro" e aggiungici il nostro pannello di riepilogo
        JPanel glassPane = (JPanel) topFrame.getGlassPane();
        glassPane.setLayout(new GridBagLayout());
        glassPane.add(summaryPanel);
        glassPane.setOpaque(false); // Rendi il glass pane trasparente
    }


    // --- NUOVO METODO IMPLEMENTATO ---
    @Override
    public void mostraRiepilogoMano(String nomeVincitore, float punteggioSquadra1, float punteggioSquadra2) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;

        // Aggiorna le informazioni e rendi visibile il pannello di vetro
        summaryPanel.updateInfo(nomeVincitore, punteggioSquadra1, punteggioSquadra2);
        topFrame.getGlassPane().setVisible(true);

        // Usa un Timer per nascondere il glass pane dopo 2 secondi
        Timer timer = new Timer(2000, e -> topFrame.getGlassPane().setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }


    // --- INNER CLASS PER IL PANNELLO DI RIEPILOGO (invariata) ---
    private class RoundSummaryPanel extends JPanel {
        private JLabel winnerLabel;
        private JLabel scoreLabel;
        private ImageIcon background;

        public RoundSummaryPanel(ImageIcon bg) {
            this.background = bg;
            this.setLayout(new GridBagLayout());
            this.setOpaque(false);


            JPanel contentPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (background != null) {
                        g.drawImage(background.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
                    }
                }
            };
            contentPanel.setOpaque(false);
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setPreferredSize(new Dimension(480, 320));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


            winnerLabel = new JLabel("Mano vinta da: ", SwingConstants.CENTER);
            winnerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            winnerLabel.setForeground(Color.WHITE);
            winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            scoreLabel = new JLabel("Punteggi:", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            contentPanel.add(Box.createVerticalGlue());
            contentPanel.add(winnerLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            contentPanel.add(scoreLabel);
            contentPanel.add(Box.createVerticalGlue());

            this.add(contentPanel);
        }

        public void updateInfo(String winnerName, float score1, float score2) {
            winnerLabel.setText("Mano vinta da: " + winnerName);
            scoreLabel.setText(String.format("Andrei & Borl: %.2f  -  DiegoBot & ThomasBot: %.2f", score1, score2));
        }
    }


    // --- TUTTO IL RESTO DEL CODICE RIMANE IDENTICO ---

    private void initializePlayerAreas() {
        northPlayerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, -35, 5));
        northPlayerArea.setPreferredSize(new Dimension(0, CARD_HEIGHT + 45));
        southPlayerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, -35, 5));
        southPlayerArea.setPreferredSize(new Dimension(0, CARD_HEIGHT + 45));
        westPlayerArea = new JPanel(new GridLayout(0, 1, 5, -90));
        westPlayerArea.setPreferredSize(new Dimension(CARD_HEIGHT + 20, 0));
        eastPlayerArea = new JPanel(new GridLayout(0, 1, 5, -90));
        eastPlayerArea.setPreferredSize(new Dimension(CARD_HEIGHT + 20, 0));
        centerTableArea = new JPanel(new GridBagLayout());
        centerTableArea.setBackground(new Color(0, 80, 0));
        northPlayerArea.setOpaque(false);
        southPlayerArea.setOpaque(false);
        westPlayerArea.setOpaque(false);
        eastPlayerArea.setOpaque(false);
    }

    public void startNewGame() {
        Giocatore umano = new GiocatoreUmano(this);
        umano.setNome("Andrei");
        Giocatore botEst = new BotPlayer();
        botEst.setNome("ThomasBot");
        Giocatore botNord = new BotPlayer();
        botNord.setNome("Borl");
        Giocatore botOvest = new BotPlayer();
        botOvest.setNome("DiegoBot");

        playerAreaMap = new HashMap<>();
        playerAreaMap.put(umano, southPlayerArea);
        playerAreaMap.put(botOvest, westPlayerArea);
        playerAreaMap.put(botNord, northPlayerArea);
        playerAreaMap.put(botEst, eastPlayerArea);

        List<Giocatore> giocatoriInOrdineDiGioco = List.of(umano, botEst, botNord, botOvest);

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

    public void updateGUI() {
        if (partita == null || partita.getGioco() == null || playerAreaMap == null) return;

        playerAreaMap.values().forEach(JPanel::removeAll);

        for(Component c : centerTableArea.getComponents()) {
            if (c instanceof JLabel && ((JLabel)c).getIcon() != null) {
                centerTableArea.remove(c);
            }
        }

        for (Giocatore player : partita.getGioco().getGiocatori()) {
            JPanel area = playerAreaMap.get(player);
            if (area != null) {
                area.setBorder(BorderFactory.createTitledBorder(
                        null,
                        player.getNome(),
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 14),
                        Color.WHITE
                ));
                drawPlayerHand(player, area, player instanceof GiocatoreUmano);
            }
        }

        drawTable();
        revalidate();
        repaint();
    }

    private void drawPlayerHand(Giocatore player, JPanel area, boolean isHuman) {
        if (player.getCarte() == null) return;
        String backImageName = (area == westPlayerArea || area == eastPlayerArea) ? "back_carta_ruotata" : "back_carta";
        for (Carta card : player.getCarte()) {
            String cardName = isHuman ? getCardImageName(card) : backImageName;
            ImageIcon icon = cardImages.get(cardName);
            if (icon != null) {
                area.add(new JLabel(icon));
            }
        }
    }

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

            if (giocatoreCheHaGiocato instanceof GiocatoreUmano) {
                gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.SOUTH;
            } else if (playerAreaMap.get(giocatoreCheHaGiocato) == westPlayerArea) {
                gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            } else if (playerAreaMap.get(giocatoreCheHaGiocato) == northPlayerArea) {
                gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.NORTH;
            } else {
                gbc.gridx = 2; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
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
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/BackPanel.png"));
        cardImages.put("BackPanel", bgIcon);
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
        String rotatedBackName = "back_carta_ruotata";
        java.net.URL rotatedBackURL = getClass().getResource(imagePath + rotatedBackName + ".png");
        if (rotatedBackURL != null) {
            ImageIcon icon = new ImageIcon(rotatedBackURL);
            Image scaled = icon.getImage().getScaledInstance(CARD_HEIGHT, CARD_WIDTH, Image.SCALE_SMOOTH);
            cardImages.put(rotatedBackName, new ImageIcon(scaled));
        } else {
            System.err.println("Immagine non trovata: " + rotatedBackName + ".png. Usando il dorso standard.");
            cardImages.put(rotatedBackName, cardImages.get(backName));
        }
    }

    private String getCardImageName(Carta carta) {
        return carta.getValore().name() + carta.getSeme().name();
    }
}