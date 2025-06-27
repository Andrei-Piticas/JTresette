package view.Game;

import model.carta.Carta;
import jtresette.GiocatoreUmano;
import jtresette.Giocatore;
import opponent.BotPlayer;
import model.Statistiche;
import jtresette.Partita2v2;
import services.StatisticheRep;
import view.GameUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel implements GameUI {
    private final Image backgroundGame;
    private final Partita2v2 partita;
    private final JPanel tablePanel;
    private final HandPanel handPanel;
    private final JLabel labelScoreA;
    private final JLabel labelScoreB;
    private final JLabel statusLabel;

    public GamePanel(CardLayout cards, JPanel cardHolder,
                     Statistiche stat, StatisticheRep repo, Image backgroundGame) {
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
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        topBar.add(backButton, BorderLayout.WEST);
        topBar.add(statusLabel, BorderLayout.CENTER);
        topBar.add(scorePanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Init partita
        List<Giocatore> roster = List.of(
                new GiocatoreUmano(this),
                new BotPlayer(),
                new BotPlayer(),
                new BotPlayer()
        );
        partita = new Partita2v2(roster);
        partita.inizio();

        // Panels for bots: north, west, east
        add(createBotPanel(roster.get(1), "Bot NORD"), BorderLayout.NORTH);
        add(createBotPanel(roster.get(2), "Bot OVEST"), BorderLayout.WEST);
        add(createBotPanel(roster.get(3), "Bot EST"), BorderLayout.EAST);

        // Table panel
        tablePanel = new JPanel(new GridLayout(1, 4, 10, 0));
        tablePanel.setOpaque(false);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(tablePanel, BorderLayout.CENTER);

        // Hand panel
        handPanel = new HandPanel(partita.getGiocatori().get(0).getCarte());
        add(handPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JButton createBackButton(CardLayout cards, JPanel cardHolder) {
        JButton btn = new JButton("<- Indietro");
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.addActionListener(e -> cards.show(cardHolder, "MENU"));
        return btn;
    }

    private JPanel createBotPanel(Giocatore bot, String title) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(120, 80));
        p.add(new JLabel(title));
        // qui si può aggiungere icona o info del bot
        return p;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundGame, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo) {
        // logica minima: gioca prima carta
        return mano.get(0);
    }

    // Semplice pannello mano utente
    private static class HandPanel extends JPanel {
        public HandPanel(List<Carta> mano) {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
            for (Carta c : mano) {
                ImageIcon icon = new ImageIcon(
                        getClass().getResource("/images/carte/" + c.getNomeFile())
                );
                JButton btn = new JButton(icon);
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                btn.addActionListener(e -> {/* TODO: logica gioco */});
                add(btn);
            }
        }
    }
}
