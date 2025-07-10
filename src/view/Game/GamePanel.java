package view.Game;

import controller.Giocatore;
import controller.GiocatoreUmano;
import model.Partita;
import model.Partita2v2;
import model.opponent.BotPlayer;
import view.GameUI;
import model.Statistiche;
import model.services.StatisticheRep;
import model.carta.Carta;
import model.carta.Seme;
import model.carta.Valore;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Rappresenta il pannello principale dell'interfaccia di gioco,
   questa classe è fondamentale nel package view  nel pattern MVC.
    Si occupa di disegnare tutti gli elementi di gioco (tavolo, carte, punteggi) e di catturare
    l'input dell'utente.
 Implementa l'interfaccia GAME UI, agendo come un Observer che viene notificato dalla logica di gioco (il Model)
 quando lo stato della partita cambia.

 */
public class GamePanel extends JPanel implements GameUI {


    private RoundSummaryPanel summaryPanel;
    private JPanel southPlayerArea, northPlayerArea, westPlayerArea, eastPlayerArea, centerTableArea;
    private Map<Giocatore, JPanel> playerAreaMap;
    private final Map<String, Image> imageCache = new HashMap<>();

    // dichiaro delle dimensioni fisse per le carte
    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 125;
    private static final int CENTER_CARD_WIDTH = 100;
    private static final int CENTER_CARD_HEIGHT = 156;

    // dichiaro attributi per la partita e la carta scelta dall'utente + statistiche
    private Partita partita;
    private Carta cartaSceltaDallUmano;
    private final Object lock = new Object(); //serve a sincronizzare due thread diversi(GameWorker e GUI)
    private GameWorker gameWorker;
    private final Statistiche stats;
    private final StatisticheRep repo;
    private final CardLayout mainCards;
    private final JPanel mainCardHolder;


    /*Costruttore principale che ha come parametri
    * stats e repo per aggiornare e comunicare le statistiche di gioco
    * cards per la navigazione tra piu panel
    * cardHolder il contenitore dei panel
    * */
    public GamePanel(Statistiche stats, StatisticheRep repo, CardLayout cards, JPanel cardHolder) {
        super(new BorderLayout());
        this.stats = stats;
        this.repo = repo;
        this.mainCards = cards;
        this.mainCardHolder = cardHolder;

        loadImages();
        initializePlayerAreas();

        this.add(northPlayerArea, BorderLayout.NORTH);
        this.add(createSouthContainer(), BorderLayout.SOUTH);
        this.add(westPlayerArea, BorderLayout.WEST);
        this.add(eastPlayerArea, BorderLayout.EAST);
        this.add(centerTableArea, BorderLayout.CENTER);

        // Configura il Glass Pane (per l'overlay di fine mano)
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && this.isShowing()) {
                setupGlassPane();
            }
        });
    }

    /**
     * Il GamePanel stesso disegna la sua immagine di sfondo,che viene chiamato automaticamente da Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image bg = imageCache.get("game_background");
        if (bg != null) {
            g.drawImage(bg, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }

    /**
     * Inizializza i pannelli che conterranno le mani dei giocatori e il tavolo da gioco.
     * Tutti i pannelli sono resi trasparenti per far vedere l'immagine di sfondo principale.
     */
    private void initializePlayerAreas() {
        northPlayerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, -35, 5));
        northPlayerArea.setPreferredSize(new Dimension(0, CARD_HEIGHT + 45));

        westPlayerArea = new JPanel(new GridLayout(0, 1, 5, -90));
        westPlayerArea.setPreferredSize(new Dimension(CARD_HEIGHT + 40, 0));

        eastPlayerArea = new JPanel(new GridLayout(0, 1, 5, -90));
        eastPlayerArea.setPreferredSize(new Dimension(CARD_HEIGHT + 40, 0));

        centerTableArea = new JPanel(new GridBagLayout());


        northPlayerArea.setOpaque(false);
        westPlayerArea.setOpaque(false);
        eastPlayerArea.setOpaque(false);
        centerTableArea.setOpaque(false);
    }

    /**
     * Crea un contenitore per la parte inferiore dello schermo,dove gioca l'utente.
     */
    private JPanel createSouthContainer() {
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.setOpaque(false);
        southPlayerArea = new JPanel(new FlowLayout(FlowLayout.CENTER, -25, 5));
        southPlayerArea.setOpaque(false);
        southContainer.add(southPlayerArea, BorderLayout.CENTER);
        return southContainer;
    }

    /**
     * Carica tutte le risorse immagine necessarie per il gioco e le mette in cache
        per un accesso rapido(grazie al fatto che le immagini vengono caricate immediatamente ed una volta sola invece di essere cercate e ricaricate ogni volta).
     */
    private void loadImages() {
        String imagePath = "/images/";
        imageCache.put("game_background", new ImageIcon(getClass().getResource(imagePath + "3858.jpg")).getImage());
        imageCache.put("backpanel", new ImageIcon(getClass().getResource(imagePath + "BackPanel.png")).getImage());

        String cardsPath = imagePath + "carte/";
        for (Valore valore : Valore.values()) {
            for (Seme seme : Seme.values()) {
                String cardName = valore.name() + seme.name();
                java.net.URL imgURL = getClass().getResource(cardsPath + cardName + ".png");
                if (imgURL != null) imageCache.put(cardName, new ImageIcon(imgURL).getImage());
            }
        }
        java.net.URL backURL = getClass().getResource(cardsPath + "back_carta.png");
        if (backURL != null) imageCache.put("back_carta", new ImageIcon(backURL).getImage());
        java.net.URL rotatedBackURL = getClass().getResource(cardsPath + "back_carta_ruotata.png");
        if (rotatedBackURL != null) imageCache.put("back_carta_ruotata", new ImageIcon(rotatedBackURL).getImage());
    }

    /**
     * Metodo di utilità per ottenere un'icona ridimensionata */
    private ImageIcon getScaledIcon(String name, int width, int height) {
        Image img = imageCache.get(name);
        if (img == null) return null;
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    /**
     * Avvia una nuova partita.
     * Crea i giocatori, l'oggetto Partita e fa partire
     * il GameWorker per gestire il flusso di gioco.
     */
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

        gameWorker = new GameWorker();
        gameWorker.execute();
    }

    /**
     * Classe interna che gestisce la logica di gioco
     */
    private class GameWorker extends SwingWorker<String, Void> {
        /**
         * Esegue la partita completa in background.
         */
        @Override
        protected String doInBackground() throws Exception {
            return partita.eseguiPartita();
        }

        /**
         * Metodo eseguito al termine della partita.
         * Aggiorna le statistiche e mostra il risultato finale.
         */

        protected void done() {
            try {
                String risultato = get();

                stats.incrementaGiocate();
                if (risultato.equals("Squadra 1 (Tu e Bot Nord)")) {
                    stats.incrementaVinte();
                } else {
                    stats.incrementaPerse();
                }
                repo.saveStats(stats);

                mostraRiepilogoFinale(risultato);

            } catch (Exception e) {
                if (!(e.getCause() instanceof InterruptedException)) e.printStackTrace();
            }
        }
    }

    /**
     * Mostra il pannello di riepilogo finale e attende un click per tornare al menu.
     */
    public void mostraRiepilogoFinale(String risultato) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;
        JPanel glassPane = (JPanel) topFrame.getGlassPane();

        summaryPanel.updateInfoFinale("Partita Terminata", "Ha vinto: " + risultato);

        MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                glassPane.setVisible(false);
                glassPane.removeMouseListener(this);
                mainCards.show(mainCardHolder, "MENU");
            }
        };
        glassPane.addMouseListener(listener);
        glassPane.setVisible(true);
    }

    /**

     Mostra un pannello temporaneo che indica chi ha vinto la mano.
     */
    @Override
    public void mostraRiepilogoMano(String nomeVincitore) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;
        JPanel glassPane = (JPanel) topFrame.getGlassPane();
        try {
            summaryPanel.updateInfoMano(nomeVincitore);
            glassPane.setVisible(true);
            Timer timer = new Timer(1500, e -> glassPane.setVisible(false));
            timer.setRepeats(false);
            timer.start();
        } catch (Exception e) {}
    }

    /**

     * Metodo chiamato per forzare un aggiornamento della GUI.
     */
    @Override
    public void update() {
        SwingUtilities.invokeLater(this::updateGUI);
    }

    /**

     * Attende che l'utente clicchi su una carta.
     * Utilizza SwingUtilities.invokeAndWait per garantire la sincronizzazione tra i thread(gioco e ui).
     */
    @Override
    public Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                updateGUI();
                enableCardListeners(mano);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        disableCardListeners();
        return cartaSceltaDallUmano;
    }

    /**
     * Aggiorna l'intera interfaccia grafica, ridisegnando le mani dei giocatori e il tavolo.
     */
    public void updateGUI() {
        if (partita == null || partita.getGioco() == null || playerAreaMap == null) return;
        playerAreaMap.values().forEach(JPanel::removeAll);
        for (Component c : centerTableArea.getComponents()) {
            if (c instanceof JLabel && ((JLabel) c).getIcon() != null) {
                centerTableArea.remove(c);
            }
        }
        List<Giocatore> giocatoriInGioco = partita.getGioco().getGiocatori();
        if (giocatoriInGioco != null && giocatoriInGioco.size() == 4) {
            for (int i = 0; i < giocatoriInGioco.size(); i++) {
                Giocatore player = giocatoriInGioco.get(i);
                JPanel area = playerAreaMap.get(player);
                if (area != null) {
                    float score = Partita2v2.getPunteggio(i);
                    String title = String.format("%s: %.2f", player.getNome(), score);
                    area.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createEmptyBorder(), title, TitledBorder.CENTER,
                            TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 14), Color.WHITE
                    ));
                    drawPlayerHand(player, area, player instanceof GiocatoreUmano);
                }
            }
        }
        drawTable();
        revalidate();
        repaint();
    }



    /**
     * Disegna le carte della mano di un giocatore nell'area specificata.
     * Se il giocatore è umano, mostra le carte vere, altrimenti mostra le carte coperte.
     */
    private void drawPlayerHand(Giocatore player, JPanel area, boolean isHuman) {
        if (player.getCarte() == null) return;
        String backImageName = (area == westPlayerArea || area == eastPlayerArea) ? "back_carta_ruotata" : "back_carta";
        int width = (area == westPlayerArea || area == eastPlayerArea) ? CARD_HEIGHT : CARD_WIDTH;
        int height = (area == westPlayerArea || area == eastPlayerArea) ? CARD_WIDTH : CARD_HEIGHT;
        for (Carta card : player.getCarte()) {
            String cardName = isHuman ? getCardImageName(card) : backImageName;
            ImageIcon icon = getScaledIcon(cardName, width, height);
            if (icon != null) area.add(new JLabel(icon));
        }
    }


    /**
     * Disegna le carte attualmente sul tavolo.
     * Le carte vengono posizionate in base al giocatore che le ha giocate.
     */
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
            ImageIcon icon = getScaledIcon(getCardImageName(carta), CENTER_CARD_WIDTH, CENTER_CARD_HEIGHT);
            if (icon == null) continue;

            //posiziona l'icona in base al giocatore che ha giocato
            if (giocatoreCheHaGiocato instanceof GiocatoreUmano) {
                gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.SOUTH;
            } else if (playerAreaMap.get(giocatoreCheHaGiocato) == westPlayerArea) {
                gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
            } else if (playerAreaMap.get(giocatoreCheHaGiocato) == northPlayerArea) {
                gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.NORTH;
            } else {
                gbc.gridx = 2; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
            }
            centerTableArea.add(new JLabel(icon), gbc);
        }
    }


    /**
     * Configura il pannello di vetro (glass pane) per mostrare il riepilogo della mano o della partita.
     * Viene chiamato quando il GamePanel viene mostrato.
     */
    private void setupGlassPane() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame == null) return;
        summaryPanel = new RoundSummaryPanel(imageCache.get("backpanel"));
        JPanel glassPane = (JPanel) topFrame.getGlassPane();
        glassPane.setLayout(new GridBagLayout());
        glassPane.removeAll();
        glassPane.add(summaryPanel);
        glassPane.setOpaque(false);
    }


    /**
     * Abilita i listener per le carte nella mano dell'utente. */
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
                        synchronized (lock) { cartaSceltaDallUmano = carta; lock.notify(); }
                    }
                });
            }
        }
    }

    /**
     * Disabilita i listener delle carte nella mano dell'utente,
     * Viene chiamato quando l'utente ha già scelto una carta o quando la mano è finita.
     */
    private void disableCardListeners() {
        for (Component comp : southPlayerArea.getComponents()) {
            comp.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            for (MouseListener ml : comp.getMouseListeners()) { comp.removeMouseListener(ml); }
        }
    }


    /**
     * Restituisce il nome dell'immagine della carta in base al suo valore e seme.
     * Utilizza la convenzione di denominazione "ValoreSeme" (es. "ASSOCUORI").
     */
    private String getCardImageName(Carta carta) { return carta.getValore().name() + carta.getSeme().name(); }


    /**
     * Pannello che mostra il riepilogo della mano e che
     * viene mostrato sopra il GamePanel quando una mano termina.
     */
    private class RoundSummaryPanel extends JPanel {
        private final JLabel titleLabel;
        private final JLabel subtitleLabel;
        private final Image background;

        public RoundSummaryPanel(Image bg) {
            this.background = bg;
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setPreferredSize(new Dimension(450, 220));
            this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            this.setOpaque(false);
            titleLabel = new JLabel("", SwingConstants.CENTER);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
            titleLabel.setForeground(Color.YELLOW);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            subtitleLabel = new JLabel("", SwingConstants.CENTER);
            subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
            subtitleLabel.setForeground(Color.WHITE);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(Box.createVerticalGlue());
            this.add(titleLabel);
            this.add(Box.createRigidArea(new Dimension(0, 15)));
            this.add(subtitleLabel);
            this.add(Box.createVerticalGlue());
        }


        /**
         * Ridisegna il pannello con l'immagine di sfondo..
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.background != null) g.drawImage(this.background, 0, 0, this.getWidth(), this.getHeight(), this);
        }


        /**
         * Mostra il nome del vincitore della mano.
         */
        public void updateInfoMano(String winnerName) {
            this.titleLabel.setText("Mano vinta da: " + winnerName);
            this.subtitleLabel.setText("");
        }


        /**
         * Mostra il riepilogo finale della partita.
         * Viene chiamato quando la partita termina.
         */
        public void updateInfoFinale(String titolo, String sottotitolo) {
            this.titleLabel.setText(titolo);
            this.subtitleLabel.setText(sottotitolo);
        }
    }
}