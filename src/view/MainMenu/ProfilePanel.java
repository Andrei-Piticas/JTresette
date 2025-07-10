package view.MainMenu;

import model.Statistiche;
import model.services.StatisticheRep;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;



/* * La classe ProfilePanel rappresenta la schermata del profilo utente.
 * Questa permette all'utente di visualizzare le proprie statistiche di gioco,
 * modificare il proprio nickname e cambiare l'immagine del proprio avatar. */
public class ProfilePanel extends JPanel {
    // --- Attributi spostati qui ---
    private final CardLayout cards;
    private final JPanel cardHolder;
    private final Statistiche stats;
    private final StatisticheRep repo;
    private final Image frameImg;
    private Image avatarImg;
    private JTextField nickField;
    private Image backgAvatar;
    private final MainMenu mainMenu;


    private JButton btnGiocate;
    private JButton btnVinte;
    private JButton btnPerse;


    /*Costruttore del pannello del profilo*/
    public ProfilePanel(CardLayout cards, JPanel cardHolder, Image initialAvatar, String initialNick, Statistiche stats, StatisticheRep repo, MainMenu mainMenu) {
        super();
        this.cards = cards;
        this.cardHolder = cardHolder;
        this.stats = stats;
        this.repo = repo;
        this.avatarImg = initialAvatar;
        this.mainMenu = mainMenu;

        ImageIcon fI = new ImageIcon(getClass().getResource("/images/avatar.png"));
        frameImg = fI.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
        URL bgUrl = getClass().getResource("/images/avatar_background.jpg");
        backgAvatar = new ImageIcon(bgUrl).getImage();

        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        iniziaComponentis(this, initialNick);
    }


    /* Assembla e configura tutti i componenti grafici di questo pannello. */
    private void iniziaComponentis(JPanel panel, String initialNick) {
        //Header
        URL backUrl = getClass().getResource("/images/backButton.png");
        ImageIcon rawBack = new ImageIcon(backUrl);
        ImageIcon backIcon = new ImageIcon(rawBack.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
        JButton back = new JButton(backIcon);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setFocusPainted(false);
        back.addActionListener(e -> cards.show(cardHolder, "MENU"));
        JLabel title = new JLabel("PROFILO", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        header.setOpaque(false);
        header.add(back);
        header.add(Box.createHorizontalGlue());
        header.add(title);
        header.add(Box.createHorizontalGlue());
        header.add(Box.createRigidArea(new Dimension(60, 0)));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(header);

        //Avatar Panel
        JPanel avatarPanel = new JPanel() {
            {
                setPreferredSize(new Dimension(130, 130));
                setMaximumSize(getPreferredSize());
                setOpaque(false);
                setAlignmentX(Component.CENTER_ALIGNMENT);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileFilter(new FileNameExtensionFilter("Immagini", "png", "jpg", "jpeg", "gif"));
                        if (chooser.showOpenDialog(ProfilePanel.this) == JFileChooser.APPROVE_OPTION) {
                            File f = chooser.getSelectedFile();
                            ImageIcon raw = new ImageIcon(f.getAbsolutePath());
                            avatarImg = raw.getImage().getScaledInstance(82, 82, Image.SCALE_SMOOTH);
                            mainMenu.updateAvatar(avatarImg);
                            repaint();
                        }
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(frameImg, 0, 0, this);
                int x = (getWidth() - avatarImg.getWidth(null)) / 2;
                int y = (getHeight() - avatarImg.getHeight(null)) / 2;
                g.drawImage(avatarImg, x, y, this);
            }
        };
        panel.add(avatarPanel);

        // Nickname
        URL nickBgUrl = getClass().getResource("/images/nickBack.png");
        Image nickBgImg = new ImageIcon(nickBgUrl).getImage().getScaledInstance(250, 80, Image.SCALE_SMOOTH);
        BackgroundPanel nickBgPanel = new BackgroundPanel(nickBgImg);
        nickBgPanel.setLayout(new BoxLayout(nickBgPanel, BoxLayout.X_AXIS));
        nickBgPanel.setOpaque(false);
        nickBgPanel.setPreferredSize(new Dimension(250, 80));
        nickBgPanel.setMaximumSize(nickBgPanel.getPreferredSize());
        nickField = new JTextField(initialNick, 15);
        nickField.setOpaque(false);
        nickField.setBorder(null);
        nickField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nickField.setForeground(Color.WHITE);
        nickField.setHorizontalAlignment(SwingConstants.CENTER);
        nickBgPanel.add(Box.createHorizontalGlue());
        nickBgPanel.add(nickField);
        nickBgPanel.add(Box.createHorizontalGlue());
        JPanel avatarNickContainer = new JPanel();
        avatarNickContainer.setLayout(new BoxLayout(avatarNickContainer, BoxLayout.X_AXIS));
        avatarNickContainer.setOpaque(false);
        avatarNickContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatarNickContainer.add(avatarPanel);
        avatarNickContainer.add(Box.createHorizontalStrut(20));
        avatarNickContainer.add(nickBgPanel);
        panel.add(avatarNickContainer);

        // Statistiche
        URL woodUrl = getClass().getResource("/images/BackPanel.png");
        Image woodImg = new ImageIcon(woodUrl).getImage().getScaledInstance(600, 450, Image.SCALE_SMOOTH);
        BackgroundPanel statsPanel = new BackgroundPanel(woodImg);
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setPreferredSize(new Dimension(600, 450));
        statsPanel.setMaximumSize(statsPanel.getPreferredSize());
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        URL genUrl = getClass().getResource("/images/generalButtons1.png");
        ImageIcon genIcon = new ImageIcon(new ImageIcon(genUrl).getImage().getScaledInstance(350, 100, Image.SCALE_SMOOTH));
        Dimension btnSize = new Dimension(350, 100);


        btnGiocate = new JButton("Partite Giocate: " + stats.getPartiteGiocate(), genIcon);
        btnVinte = new JButton("Partite Vinte: " + stats.getPartiteVinte(), genIcon);
        btnPerse = new JButton("Partite Perse: " + stats.getPartitePerse(), genIcon);

        for (JButton b : new JButton[]{btnGiocate, btnVinte, btnPerse}) {
            b.setPreferredSize(btnSize);
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setVerticalTextPosition(SwingConstants.CENTER);
            b.setAlignmentX(CENTER_ALIGNMENT);
            b.setIconTextGap(0);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setFont(new Font("SansSerif", Font.BOLD, 16));
            b.setForeground(Color.WHITE);
        }

        statsPanel.add(Box.createVerticalGlue());
        statsPanel.add(btnGiocate);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(btnVinte);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(btnPerse);
        statsPanel.add(Box.createVerticalGlue());
        panel.add(statsPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(Box.createVerticalGlue());

        //Pulsante salva e annulla
        URL statButtonUrl = getClass().getResource("/images/generalButtons.png");
        ImageIcon rawButtonStat = new ImageIcon(statButtonUrl);
        Image ButtonStatImg = rawButtonStat.getImage().getScaledInstance(190, 80, Image.SCALE_SMOOTH);
        ImageIcon ButtonStat = new ImageIcon(ButtonStatImg);
        Dimension butSize = new Dimension(190, 80);
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton save = new JButton("Salva", ButtonStat);
        save.setPreferredSize(butSize);
        save.setHorizontalTextPosition(SwingConstants.CENTER);
        save.setVerticalTextPosition(SwingConstants.CENTER);
        save.setIconTextGap(0);
        save.setBorderPainted(false);
        save.setContentAreaFilled(false);
        save.setFocusPainted(false);
        save.setFont(new Font("SansSerif", Font.BOLD, 20));
        save.setForeground(Color.WHITE);
        save.addActionListener(e -> {
            String nick = nickField.getText().trim();
            mainMenu.updateNickname(nick);
            cards.show(cardHolder, "MENU");
        });
        JButton cancel = new JButton("Annulla", ButtonStat);
        cancel.setPreferredSize(butSize);
        cancel.setHorizontalTextPosition(SwingConstants.CENTER);
        cancel.setVerticalTextPosition(SwingConstants.CENTER);
        cancel.setIconTextGap(0);
        cancel.setBorderPainted(false);
        cancel.setContentAreaFilled(false);
        cancel.setFocusPainted(false);
        cancel.setFont(new Font("SansSerif", Font.BOLD, 20));
        cancel.setForeground(Color.WHITE);
        cancel.addActionListener(e -> cards.show(cardHolder, "MENU"));
        buttonsPanel.add(save);
        buttonsPanel.add(cancel);
        panel.add(buttonsPanel);
    }

    /** Metodo per aggiornare le statistiche visualizzate nel pannello del profilo */
    public void aggiornaDisplayStatistiche() {
        if (btnGiocate != null && btnVinte != null && btnPerse != null) {
            btnGiocate.setText("Partite Giocate: " + stats.getPartiteGiocate());
            btnVinte.setText("Partite Vinte: " + stats.getPartiteVinte());
            btnPerse.setText("Partite Perse: " + stats.getPartitePerse());
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgAvatar, 0, 0, getWidth(), getHeight(), this);
    }
}