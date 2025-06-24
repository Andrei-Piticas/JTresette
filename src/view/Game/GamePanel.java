package view.Game;
import jtresette.GiocatoreUmano;
import jtresette.Giocatore;
import model.Statistiche;
import model.carta.Carta;
import opponent.BotPlayer;
import services.StatisticheRep;
import jtresette.Partita2v2;
import view.GameUI;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class GamePanel extends JPanel implements GameUI  {
    private final Image backgroundGame;
    private final Partita2v2 partita;
    private final JPanel tablePanel;
    private final JPanel handPanel;
    private final java.util.List<Giocatore> players;
    private int roundNumero = 0 ;



    @Override
    public Carta promptGiocaCarta(List<Carta> mano, List<Carta> tavolo) {
        // placeholder: restituisco sempre la prima carta
        return mano.get(0);
    }



    public GamePanel(CardLayout cards , JPanel cardHolder , Statistiche stat , StatisticheRep rep, Image backgroundGame, List<Giocatore> players){
        this.backgroundGame = backgroundGame;
        this.players = players;
        setLayout(new BorderLayout());


        List<Giocatore> players = List.of(new GiocatoreUmano(this) , new BotPlayer() , new GiocatoreUmano(this), new BotPlayer());

        partita  = new Partita2v2(players);
        partita.inizio();

        // ─── TABLE PANEL ───
        tablePanel = new JPanel(new GridLayout(1, 4, 10, 0));
        tablePanel.setOpaque(false);
        add(tablePanel, BorderLayout.CENTER);

// ─── HAND PANEL ───
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        handPanel.setOpaque(false);
        add(handPanel, BorderLayout.SOUTH);


        popolaMano


        // disegno le carte del Giocatore 1
        List<Carta> mano = partita.getGiocatori().get(0).getCarte();
        for (Carta c : mano) {
            // carica l’icona della carta (adatta il path/file)
            ImageIcon cardIcon = new ImageIcon(
                    getClass().getResource("/images/cards/" + c.getNomeFile())
            );
            JButton btn = new JButton(cardIcon);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);

            btn.addActionListener(e -> {
                // 1) gioca la carta nel modello
                Carta giocata = partita.giocaTurno();
                // 2) rimuovi il bottone dalla mano
                handPanel.remove(btn);
                handPanel.revalidate();
                handPanel.repaint();
                // 3) mostra subito la carta sul tavolo
                tablePanel.add(new JLabel(new ImageIcon(
                        getClass().getResource("/images/cards/" + giocata.getNomeFile())
                )));
                tablePanel.revalidate();
                tablePanel.repaint();
                // 4) TODO: fai giocare automaticamente gli altri 3 e poi eseguiRound()
            });

            handPanel.add(btn);
        }



        // ─── TOP BAR con back + indicatore turno ───
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

// back button
        URL backUrl = getClass().getResource("/images/backButton.png");
        ImageIcon rawBack = new ImageIcon(backUrl);
        ImageIcon backIcon = new ImageIcon(
                rawBack.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)
        );
        JButton backButton = new JButton(backIcon);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> cards.show(cardHolder, "MENU"));
        topBar.add(backButton, BorderLayout.WEST);

// indicatore di turno (placeholder, cambierai in seguito)
        JLabel turnLabel = new JLabel("Turno di: Giocatore 1", JLabel.CENTER);
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(turnLabel, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);




    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundGame, 0, 0, getWidth(), getHeight(), this);
    }
}
