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

    // --- ATTRIBUTES ---
    private JLabel scoreLabel;
    private JLabel messageLabel;

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

        this.add(northPlayerArea, BorderLayout.NORTH);
        this.add(createSouthContainer(), BorderLayout.SOUTH);
        this.add(westPlayerArea, BorderLayout.WEST);
        this.add(eastPlayerArea, BorderLayout.EAST);
        this.add(centerTableArea, BorderLayout.CENTER);
        this.setBackground(new Color(34, 139, 34));

        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && this.isShowing()) {
                setupGlassPane();
            }
        });
    }

    private void initializePlayerAreas() {
        northPlayerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, -35, 5));
        northPlayerArea.setPreferredSize(new Dimension(0, CARD_HEIGHT + 45));

        westPlayerArea = new JPanel(new GridLayout(0, 1, 5, -90));
        westPlayerArea.setPreferredSize(new Dimension(CARD_HEIGHT + 20, 0));

        eastPlayerArea = new JPanel(new GridLayout(0, 1, 5, -90));
        eastPlayerArea.setPreferredSize(new Dimension(CARD_HEIGHT + 20, 0));

        centerTableArea = new JPanel(new GridBagLayout());
        centerTableArea.setBackground(new Color(0, 80, 0));

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        messageLabel.setForeground(Color.YELLOW);
        messageLabel.setVisible(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        centerTableArea.add(messageLabel, gbc);

        northPlayerArea.setOpaque(false);
        westPlayerArea.setOpaque(false);
        eastPlayerArea.setOpaque(false);
    }

    private JPanel createSouthContainer() {
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.setOpaque(false);

        southPlayerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, -25, 5)); // Aumentata un po' la distanza
        southPlayerArea.setOpaque(false);

        JPanel scoreContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        scoreContainer.setOpaque(false);
        scoreContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        scoreLabel = new JLabel("Andrei & Borl: 0.00  -  DiegoBot & ThomasBot: 0.00");
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        scoreLabel.setForeground(Color.WHITE);
        scoreContainer.add(scoreLabel);

        southContainer.add(southPlayerArea, BorderLayout.CENTER);
        southContainer.add(scoreContainer, BorderLayout.SOUTH);

        return southContainer;
    }

    // --- Metodo updateGUI() Modificato ---

    public void updateGUI() {
        if (partita == null || partita.getGioco() == null || playerAreaMap == null) return;

        playerAreaMap.values().forEach(JPanel::removeAll);

        for(Component c : centerTableArea.getComponents()) {
            if (c instanceof JLabel && ((JLabel)c).getIcon() != null) {
                centerTableArea.remove(c);
            }
        }

        List<Giocatore> giocatoriInGioco = partita.getGioco().getGiocatori();

        if (giocatoriInGioco != null && giocatoriInGioco.size() == 4) {
            for (int i = 0; i < giocatoriInGioco.size(); i++) {
                Giocatore player = giocatoriInGioco.get(i);
                JPanel area = playerAreaMap.get(player);

                if (area != null) {
                    // 1. Recupera il punteggio individuale del giocatore
                    float score = Partita2v2.getPunteggio(i);
                    // 2. Crea la stringa per il titolo (Nome: Punti)
                    String title = String.format("%s: %.2f", player.getNome(), score);

                    // 3. Crea un TitledBorder con una base trasparente (EmptyBorder)
                    TitledBorder titledBorder = BorderFactory.createTitledBorder(
                            BorderFactory.createEmptyBorder(), // Bordo di base invisibile
                            title,
                            TitledBorder.CENTER,
                            TitledBorder.TOP,
                            new Font("SansSerif", Font.BOLD, 14),
                            Color.WHITE
                    );

                    area.setBorder(titledBorder);
                    drawPlayerHand(player, area, player instanceof GiocatoreUmano);
                }
            }
        }

        drawTable();

        // Aggiorna il punteggio totale di squadra in basso
        float puntiSquadra1 = Partita2v2.getPunteggio(0) + Partita2v2.getPunteggio(2);
        float puntiSquadra2 = Partita2v2.getPunteggio(1) + Partita2v2.getPunteggio(3);
        scoreLabel.setText(String.format("Andrei & Borl: %.2f  -  DiegoBot & ThomasBot: %.2f", puntiSquadra1, puntiSquadra2));

        revalidate();
        repaint();
    }

    // --- TUTTI GLI ALTRI METODI RIMANGONO INVARIATI ---
    // (Il codice seguente è identico a prima, ma lo includo per completezza)

    private void setupGlassPane() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;
        RoundSummaryPanel summaryPanel = new RoundSummaryPanel(cardImages.get("backpanel"));
        JPanel glassPane = (JPanel) topFrame.getGlassPane();
        glassPane.setLayout(new GridBagLayout());
        glassPane.add(summaryPanel);
        glassPane.setOpaque(false);
    }

    @Override
    public void mostraRiepilogoMano(String nomeVincitore, float punteggioSquadra1, float punteggioSquadra2) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;
        JPanel glassPane = (JPanel) topFrame.getGlassPane();
        // Assumiamo che il summaryPanel sia il primo componente del primo componente del glassPane
        try {
            Component C = glassPane.getComponent(0);
            if(C instanceof RoundSummaryPanel){
                RoundSummaryPanel summaryPanel = (RoundSummaryPanel) C;
                summaryPanel.updateInfo(nomeVincitore, punteggioSquadra1, punteggioSquadra2);
                glassPane.setVisible(true);
                Timer timer = new Timer(2000, e -> glassPane.setVisible(false));
                timer.setRepeats(false);
                timer.start();
            }

        } catch (Exception e) {
            System.err.println("Errore nel trovare il summary panel.");
        }
    }

    private class RoundSummaryPanel extends JPanel {
        private JLabel winnerLabel;
        private JLabel scoreLabel;
        private Image background;

        public RoundSummaryPanel(ImageIcon bg) {
            if (bg != null) this.background = bg.getImage();
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setPreferredSize(new Dimension(450, 220));
            this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            this.setOpaque(false);

            winnerLabel = new JLabel("Mano vinta da: ", SwingConstants.CENTER);
            winnerLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
            winnerLabel.setForeground(Color.YELLOW);
            winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoreLabel = new JLabel("Punteggi:", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
            scoreLabel.setForeground(Color.WHITE);
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(Box.createVerticalGlue());
            this.add(winnerLabel);
            this.add(Box.createRigidArea(new Dimension(0, 15)));
            this.add(scoreLabel);
            this.add(Box.createVerticalGlue());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (background != null) g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
        }

        public void updateInfo(String winnerName, float score1, float score2) {
            winnerLabel.setText("Mano vinta da: " + winnerName);
            scoreLabel.setText(String.format("Andrei & Borl: %.2f  -  DiegoBot & ThomasBot: %.2f", score1, score2));
        }
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
        String imagePath = "/images/";
        ImageIcon bgIcon = new ImageIcon(getClass().getResource(imagePath + "BackPanel.png"));
        cardImages.put("backpanel", bgIcon);

        String cardsPath = imagePath + "carte/";
        for (Valore valore : Valore.values()) {
            for (Seme seme : Seme.values()) {
                String cardName = valore.name() + seme.name();
                String fileName = cardName + ".png";
                java.net.URL imgURL = getClass().getResource(cardsPath + fileName);
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(imgURL);
                    Image scaled = icon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
                    cardImages.put(cardName, new ImageIcon(scaled));
                }
            }
        }
        String backName = "back_carta";
        java.net.URL backURL = getClass().getResource(cardsPath + backName + ".png");
        if (backURL != null) {
            ImageIcon icon = new ImageIcon(backURL);
            Image scaled = icon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
            cardImages.put(backName, new ImageIcon(scaled));
        }

        String rotatedBackName = "back_carta_ruotata";
        java.net.URL rotatedBackURL = getClass().getResource(cardsPath + rotatedBackName + ".png");
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