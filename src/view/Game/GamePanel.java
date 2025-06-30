package view.Game;

import model.carta.Carta;
import jtresette.GiocatoreUmano;
import jtresette.Giocatore;
import opponent.BotPlayer;
import model.Statistiche;
import services.StatisticheRep;
import jtresette.Partita2v2;
import view.GameUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class GamePanel extends JPanel implements GameUI {
    private final Image backgroundGame;
    private final Partita2v2 partita;
    private final JPanel tablePanel;
    private final HandPanel handPanel;
    private final JLabel labelScoreA;
    private final JLabel labelScoreB;
    private final JLabel statusLabel;
    private Carta selectedCard;
    private ImageIcon selectedIcon;

    public GamePanel(CardLayout cards, JPanel cardHolder,
                     Statistiche stat, StatisticheRep rep, Image backgroundGame) throws Exception {
        this.backgroundGame = backgroundGame;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Top bar: back button, status and score
        JButton backButton = createBackButton(cards, cardHolder);
        labelScoreA = createLabel("Squadra A: 0");
        labelScoreB = createLabel("Squadra B: 0");
        statusLabel = createLabel("Benvenuto!");

        JPanel scorePanel = new JPanel(new GridLayout(2, 1));
        scorePanel.setOpaque(false);
        scorePanel.add(labelScoreA);
        scorePanel.add(labelScoreB);

        JPanel topBar = new JPanel(new BorderLayout());
        // Aumenta l'altezza del topBar per far spazio alle carte (160px)
        topBar.setPreferredSize(new Dimension(0, 150));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topBar.add(statusLabel, BorderLayout.CENTER);
        topBar.add(scorePanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);



        // Init partita e roster
        List<Giocatore> roster = List.of(
                new GiocatoreUmano(this), new BotPlayer(), new BotPlayer(), new BotPlayer()
        );
        partita = new Partita2v2(roster);
        partita.inizio();
//
        // Bot panels
        JPanel topBot = createTopBotPanel(roster.get(1));
        topBot.setPreferredSize(new Dimension(0, 200));
        add(topBot, BorderLayout.NORTH);
        topBot.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));


        JPanel leftBot = createSideBotPanel(roster.get(2));
        // Aumentata la larghezza del pannello laterale sinistro
        leftBot.setPreferredSize(new Dimension(300, 900));
        add(leftBot, BorderLayout.WEST);

        JPanel rightBot = createSideBotPanel(roster.get(3));
        // Aumentata la larghezza del pannello laterale destro
        rightBot.setPreferredSize(new Dimension(300, 900));
        add(rightBot, BorderLayout.EAST);

        // Table panel
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(tablePanel, BorderLayout.CENTER);

        // Hand panel wrapper to limit width and add bottom padding
        // nel costruttore di GamePanel
        handPanel = new HandPanel(this, partita.getGiocatori().get(0).getCarte());
        JPanel handWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        handWrapper.setOpaque(false);
        handWrapper.setBorder(BorderFactory.createEmptyBorder(0, 100, 20, 100));
        handWrapper.add(handPanel);
        // Bordo per evidenziare l'handPanel
        //handWrapper.setBorder(new LineBorder(Color.YELLOW, 2));
        add(handWrapper, BorderLayout.SOUTH);

    }
    /**
     * Il giocatore 'parent' ha scelto di giocare 'c' (con icona 'icon'):
     * 1) lo rimuoviamo dalla mano
     * 2) notifichiamo l'engine di gioco
     * 3) disegniamo la carta nell'area 'tablePanel'
     * 4) chiamiamo a turno i tre bot
     */


    private void playCard(Carta c, ImageIcon icon) throws IOException {
        // 1) Rimuovi la carta dalla mano (visivamente e dal model)
        handPanel.removeCard(c);           // dovrai esporre un metodo in HandPanel
        partita.giocaTurno();             // notifica l'engine

        // 2) Pulisci e disegna la carta umana al CENTRO o in posizione "WEST"
        tablePanel.removeAll();
        JLabel lblHuman = new JLabel(icon);
        lblHuman.setHorizontalAlignment(SwingConstants.CENTER);
        tablePanel.add(lblHuman, BorderLayout.WEST);

        // 3) Ora i bot rispondono uno alla volta
        List<Giocatore> giocatori = partita.getGiocatori();
        List<Carta> tavolo = partita.getTavolo();  // o come li chiama il tuo engine
        for (int i = 1; i < giocatori.size(); i++) {
            Giocatore bot = giocatori.get(i);
            // chiedo al bot la sua carta (il bot la toglierà dalla propria mano)
            Carta botCard = bot.giocaCarta(bot.getCarte(), tavolo);
            partita.giocaTurno();  // aggiorna lo stato nel model

            // carico e disegno l'icona corrispondente
            URL url = getClass().getResource("/images/carte/" + botCard.getNomeFile());
            BufferedImage img = ImageIO.read(url);
            ImageIcon botIcon = new ImageIcon(img.getScaledInstance(110, 160, Image.SCALE_SMOOTH));
            JLabel lblBot = new JLabel(botIcon);
            lblBot.setHorizontalAlignment(SwingConstants.CENTER);

            // mettilo in una posizione diversa: BOT1 a NORTH, BOT2 a EAST, BOT3 a SOUTH
            switch(i) {
                case 1: tablePanel.add(lblBot, BorderLayout.NORTH); break;
                case 2: tablePanel.add(lblBot, BorderLayout.EAST);  break;
                case 3: tablePanel.add(lblBot, BorderLayout.SOUTH); break;
            }
        }

        // 4) Rinfresca la GUI
        tablePanel.revalidate();
        tablePanel.repaint();

        // Qui potresti anche chiamare partita.calcolaPunteggi() e aggiornare labelScoreA/B
    }


    /**
     * Posiziona la carta giocata al centro del tablePanel.
     */


    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JButton createBackButton(CardLayout cards, JPanel cardHolder) throws Exception {
        URL backUrl = getClass().getResource("/images/backButton.png");
        ImageIcon backIcon = new ImageIcon(backUrl);
        JButton btn = new JButton(backIcon);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> cards.show(cardHolder, "MENU"));
        return btn;
    }


    /**
     * Side bots panel: uses rotated back image, resized to 90x130 px
     */
    private JPanel createSideBotPanel(Giocatore bot) throws Exception {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        // Aggiunge padding superiore di 20px per centrare verticalmente le carte laterali
        // Rimosso padding superiore per far aderire le carte all'inizio

        JPanel cardsPanel = new JPanel();
        cardsPanel.setOpaque(false);
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBorder(new LineBorder(Color.BLUE, 2));
        cardsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(35, 0, 0, 0),
                new LineBorder(Color.BLUE, 0)
        ));
        URL sideUrl = getClass().getResource("/images/carte/back_carta_ruotata.png");
        BufferedImage sideImg = ImageIO.read(sideUrl);
        ImageIcon sideIcon = new ImageIcon(sideImg.getScaledInstance(130, 90, Image.SCALE_SMOOTH));

        for (Carta c : bot.getCarte()) {
            cardsPanel.add(Box.createVerticalStrut(-50));
            JButton btn = new JButton(sideIcon);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardsPanel.add(btn);
        }
        cardsPanel.add(Box.createVerticalGlue());

        container.add(cardsPanel, BorderLayout.CENTER);
        return container;
    }

    /**
     * Top bot panel: uses non-rotated back image, resized to 140x110 px
     */
    private JPanel createTopBotPanel(Giocatore bot) throws Exception {
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        cardsPanel.setOpaque(false);
       // cardsPanel.setBorder(new LineBorder(Color.MAGENTA, 2));

        URL topUrl = getClass().getResource("/images/carte/back_carta.png");
        BufferedImage topImg = ImageIO.read(topUrl);
        ImageIcon topIcon = new ImageIcon(topImg.getScaledInstance(110, 140, Image.SCALE_SMOOTH));

        for (Carta c : bot.getCarte()) {
            cardsPanel.add(new JLabel(topIcon));
        }
        return cardsPanel;
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundGame, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo) {
        Carta c = selectedCard;
        selectedCard = null;
        return c;
    }


    private static class HandPanel extends JPanel {
        public HandPanel(GamePanel parent, List<Carta> mano) {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 20));

            for (Carta c : mano) {
                try {
                    URL url = getClass().getResource("/images/carte/" + c.getNomeFile());
                    BufferedImage img = ImageIO.read(url);
                    ImageIcon icon = new ImageIcon(img.getScaledInstance(110, 160, Image.SCALE_SMOOTH));
                    JButton btn = new JButton(icon);
                    btn.setBorderPainted(false);
                    btn.setContentAreaFilled(false);
                    // *** salva nel button il riferimento alla carta ***
                    btn.setActionCommand(c.getNomeFile());
                    btn.addActionListener(e -> {
                        parent.selectedCard = c;
                        try {
                            parent.executeTurn();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        removeCard(c);
                    });



                    add(btn);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        /** Rimuove dalla UI il JButton corrispondente a quella carta */
        public void removeCard(Carta c) {
            for (Component comp : getComponents()) {
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    if (c.getNomeFile().equals(btn.getActionCommand())) {
                        remove(btn);
                        break;
                    }
                }
            }
            revalidate();
            repaint();
        }
    }

    /**
     * Esegue 1 turno completo:
     * - gioca la carta humana (usando giocaTurno())
     * - poi 3 volte giocaTurno() per i bot
     * - disegna ogni carta in tablePanel ai lati: WEST, NORTH, EAST, SOUTH
     */
    // 1) Rimuovi entirely il vecchio executeTurn() e al suo posto inserisci:

    private void executeTurn() throws Exception {
        tablePanel.removeAll();

        // 1) Esegui un round completo e prendi chi ha aperto e le carte giocate
        partita.eseguiRound();
        int startIndex     = partita.getLastStartIndex();
        List<Carta> giocate= partita.getLastTavoloRound();

        // 2) Mappatura fissa indice-giocatore → posizione sul BorderLayout
        Map<Integer,String> posMap = Map.of(
                0, BorderLayout.SOUTH,   // umano
                1, BorderLayout.NORTH,   // bot 1
                2, BorderLayout.WEST,    // partner
                3, BorderLayout.EAST     // bot 2
        );

        // 3) Prepara due array paralleli: ordine di visualizzazione e relative icone
        String[] order     = new String[4];
        ImageIcon[] icons  = new ImageIcon[4];
        for (int k = 0; k < 4; k++) {
            int playerIdx = (startIndex + k) % 4;
            order[k]     = posMap.get(playerIdx);
            icons[k]     = loadIconFor(giocate.get(k));
        }

        // 4) Timer per “sparare” una carta ogni 500 ms
        Timer timer = new Timer(500, null);
        timer.addActionListener(new ActionListener() {
            int idx = 0;

            public void actionPerformed(ActionEvent e) {
                if (idx < icons.length) {
                    tablePanel.add(new JLabel(icons[idx], SwingConstants.CENTER), order[idx]);
                    tablePanel.revalidate();
                    tablePanel.repaint();
                    idx++;
                } else {
                    ((Timer)e.getSource()).stop();
                    // qui puoi anche aggiornare i punteggi a schermo
                    float pa = Partita2v2.getPunteggio(0) + Partita2v2.getPunteggio(2);
                    float pb = Partita2v2.getPunteggio(1) + Partita2v2.getPunteggio(3);
                    labelScoreA.setText("Squadra A: " + pa);
                    labelScoreB.setText("Squadra B: " + pb);
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }







    /** Carica e scala l'icona di una carta carbased sul modello */
    private ImageIcon loadIconFor(Carta c) throws Exception {
        String path = "/images/carte/" + c.getNomeFile();
        URL url = getClass().getResource(path);
        System.out.println("⤷ loadIconFor: card=" + c
                + " → resourcePath=" + path
                + " → url=" + url);
        if (url == null) {
            throw new IOException("Immagine non trovata per " + c + ", cercata in: " + path);
        }
        BufferedImage img = ImageIO.read(url);
        return new ImageIcon(img.getScaledInstance(110, 160, Image.SCALE_SMOOTH));
    }




}
