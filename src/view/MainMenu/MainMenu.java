package view.MainMenu;

import audio.AudioManager;
import model.Statistiche;
import services.StatisticheRep;
import view.Game.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class MainMenu extends JFrame {
    private JLabel nickLabel;
    private String currentNick;
    private CardLayout cards;
    private JPanel cardHolder;
    private final ImageIcon[] levelIcons = new ImageIcon[5];
    private boolean isMute = false;
    private final Statistiche stat;
    private final StatisticheRep repo;

    private int getPlayerLevel() {
        return 2;
    }

    private class FullAvatarPanel extends JPanel {
        private final Image frameImage;
        private final Image avatarImage;

        public FullAvatarPanel(Image initialAvatar) {
            ImageIcon fr = new ImageIcon(getClass().getResource("/images/avatar.png"));
            frameImage = fr.getImage().getScaledInstance(130, 127, Image.SCALE_SMOOTH);
            avatarImage = initialAvatar;
            Dimension d = new Dimension(130, 130);
            setPreferredSize(d);
            setMaximumSize(d);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(frameImage, 0, 0, this);
            int x = (getWidth() - avatarImage.getWidth(null)) / 2;
            int y = (getHeight() - avatarImage.getHeight(null)) / 2;
            g.drawImage(avatarImage, x, y, this);
        }
    }


    public MainMenu(Statistiche stat, StatisticheRep repo) throws Exception {
        super("JTresette");
        this.stat = stat;
        this.repo = repo;
        currentNick = "ANDREI";


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        AudioManager.getInstance().play("src/audio/mainMenu.wav");

        ImageIcon rawAv = new ImageIcon(getClass().getResource("/images/avatarTest.png"));
        Image initialAv = rawAv.getImage().getScaledInstance(82, 82, Image.SCALE_SMOOTH);
        String initialNick = "ANDREI";

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/3858.jpg"));
        BackgroundPanel menuPanel = new BackgroundPanel(icon.getImage());
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));


        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 30, 40));

        for (int i = 0; i < levelIcons.length; i++) {
            String path = String.format("/images/level%d.png", i);
            URL url = getClass().getResource(path);
            ImageIcon raw = new ImageIcon(url);
            Image scaled = raw.getImage().getScaledInstance(200, 30, Image.SCALE_SMOOTH);
            levelIcons[i] = new ImageIcon(scaled);
        }

        initComponentsOn(menuPanel,currentNick);

        cards = new CardLayout();
        cardHolder = new JPanel(cards);
        cardHolder.add(menuPanel, "MENU");
        URL gameBgUrl = getClass().getResource("/images/backTest.png");
        Image gameBg   = new ImageIcon(gameBgUrl)
                .getImage()
                .getScaledInstance(500,500, Image.SCALE_SMOOTH );
        cardHolder.add(new GamePanel(), "GAME");
        cardHolder.add(new SettingPanel(cards, cardHolder), "IMPOSTAZIONI");


        ProfilePanel profilePanel = new ProfilePanel(
                cards,
                cardHolder,
                initialAv,
                initialNick,
                stat,
                repo,
                this
        );
        cardHolder.add(profilePanel, "PROFILE");

        setContentPane(cardHolder);
        setVisible(true);
    }

    private void initComponentsOn(JPanel bg,String nick) {
        ImageIcon nickBg = new ImageIcon(getClass().getResource("/images/nickBack.png"));
        Image nickImg = nickBg.getImage().getScaledInstance(220, 70, Image.SCALE_SMOOTH);

        nickLabel = new JLabel(nick, new ImageIcon(nickImg),JLabel.CENTER);
        nickLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nickLabel.setForeground(Color.WHITE);
        nickLabel.setHorizontalTextPosition(JLabel.CENTER);
        nickLabel.setVerticalTextPosition(JLabel.CENTER);
        nickLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        nickLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int livello = getPlayerLevel();
        JLabel levelLabel = new JLabel(levelIcons[livello]);
        levelLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel nickContainer = new JPanel();
        nickContainer.setLayout(new BoxLayout(nickContainer, BoxLayout.Y_AXIS));
        nickContainer.setOpaque(false);
        nickContainer.add(nickLabel);
        nickContainer.add(levelLabel);
        nickContainer.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        FullAvatarPanel avatarPanel = new FullAvatarPanel(
                new ImageIcon(getClass().getResource("/images/avatarTest.png"))
                        .getImage().getScaledInstance(82, 82, Image.SCALE_SMOOTH)
        );
        avatarPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        avatarPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        avatarPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cards.show(cardHolder, "PROFILE");
            }
        });

        JPanel profileContainer = new JPanel();
        profileContainer.setLayout(new BoxLayout(profileContainer, BoxLayout.X_AXIS));
        profileContainer.setOpaque(false);
        profileContainer.add(avatarPanel);
        profileContainer.add(Box.createRigidArea(new Dimension(-27, 0)));
        profileContainer.add(nickContainer);

        ImageIcon rawTitle = new ImageIcon(getClass().getResource("/images/TitleBack.png"));
        Image scaledTitle = rawTitle.getImage().getScaledInstance(340, 100, Image.SCALE_SMOOTH);
        JLabel titleM = new JLabel("TRESSETTE", new ImageIcon(scaledTitle), JLabel.CENTER);
        titleM.setFont(new Font("Serif", Font.BOLD, 36));
        titleM.setForeground(Color.white);
        titleM.setHorizontalTextPosition(JLabel.CENTER);
        titleM.setVerticalTextPosition(JLabel.CENTER);
        titleM.setHorizontalAlignment(JLabel.CENTER);
        titleM.setVerticalAlignment(JLabel.CENTER);

        ImageIcon rawAch = new ImageIcon(getClass().getResource("/images/achivments.png"));
        Image scaledAch = rawAch.getImage().getScaledInstance(330, 130, Image.SCALE_SMOOTH);
        JLabel achivments = new JLabel("OBIETTIVI                     ", new ImageIcon(scaledAch), JLabel.CENTER);
        achivments.setForeground(Color.white);
        achivments.setFont(new Font("Serif", Font.BOLD, 20));
        achivments.setHorizontalTextPosition(JLabel.CENTER);
        achivments.setVerticalTextPosition(JLabel.CENTER);
        achivments.setHorizontalAlignment(JLabel.CENTER);
        achivments.setVerticalAlignment(JLabel.CENTER);

        JPanel header = new JPanel(new GridLayout(1, 3));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, -70));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(profileContainer);
        header.add(titleM);
        header.add(achivments);
        bg.add(header);
        bg.add(Box.createVerticalStrut(20));

        URL url = getClass().getResource("/images/generalButtons.png");
        ImageIcon rawIcon = new ImageIcon(url);
        Image btnImg = rawIcon.getImage().getScaledInstance(260, 85, Image.SCALE_SMOOTH);
        ImageIcon buttonIcon = new ImageIcon(btnImg);

        JButton bottGioca = new JButton("GIOCA", buttonIcon);
        JButton bottImpostazioni = new JButton("IMPOSTAZIONI", buttonIcon);
        JButton bottEsci = new JButton("ESCI", buttonIcon);
        Dimension size = new Dimension(290, 85);
        bottGioca.setPreferredSize(size);
        bottImpostazioni.setPreferredSize(size);
        bottEsci.setPreferredSize(size);
        bottGioca.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottImpostazioni.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottEsci.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottGioca.addActionListener(e -> cards.show(cardHolder, "GAME"));
        bottImpostazioni.addActionListener(e -> cards.show(cardHolder, "IMPOSTAZIONI"));
        bottEsci.addActionListener(e -> System.exit(0));
        for (JButton b : new JButton[]{bottGioca, bottImpostazioni, bottEsci}) {
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setVerticalTextPosition(SwingConstants.CENTER);
            b.setIconTextGap(0);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setFont(new Font("SansSerif", Font.BOLD, 20));
            b.setForeground(Color.white);
        }

        ImageIcon origWood = new ImageIcon(getClass().getResource("/images/BackPanel.png"));
        Image woodImg = origWood.getImage().getScaledInstance(420, 380, Image.SCALE_SMOOTH);
        JLabel woodLabel = new JLabel(new ImageIcon(woodImg));
        woodLabel.setLayout(new BoxLayout(woodLabel, BoxLayout.Y_AXIS));
        woodLabel.setBorder(BorderFactory.createEmptyBorder(100, 20, 20, 20));
        woodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        woodLabel.add(Box.createVerticalStrut(35));
        woodLabel.add(bottGioca);
        woodLabel.add(Box.createVerticalStrut(4));
        woodLabel.add(bottImpostazioni);
        woodLabel.add(Box.createVerticalStrut(4));
        woodLabel.add(bottEsci);
        bg.add(woodLabel);

        ImageIcon onAudio = new ImageIcon(new ImageIcon(
                getClass().getResource("/images/audioButton.png"))
                .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        );
        ImageIcon offAudio = new ImageIcon(new ImageIcon(
                getClass().getResource("/images/muteButton.png"))
                .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        );
        JButton muteButton = new JButton(onAudio);
        muteButton.setBorderPainted(false);
        muteButton.setContentAreaFilled(false);
        muteButton.setFocusPainted(false);

        muteButton.addActionListener(e -> {
            isMute = !isMute;
            if (isMute) {
                AudioManager.getInstance().toggleMusic();
                muteButton.setIcon(offAudio);
            } else {
                AudioManager.getInstance().toggleMusic();
                muteButton.setIcon(onAudio);
            }
        });

        bg.add(Box.createVerticalGlue());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 50));
        footer.setPreferredSize(new Dimension(2000, 250));
        footer.setMaximumSize(new Dimension(2000, 250));
        footer.setOpaque(false);
        footer.add(muteButton);
        bg.add(footer);

    }

    public void updateNickname(String newNick) {
        currentNick = newNick;
        nickLabel.setText(newNick);
    }

}