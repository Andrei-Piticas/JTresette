package view.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import audio.AudioManager;

public class SettingPanel extends JPanel {
    private final Image backgImage;
    private final CardLayout cards;
    private final JPanel cardHolder;

    public SettingPanel(CardLayout cards, JPanel cardHolder) {
        this.cards      = cards;
        this.cardHolder = cardHolder;


        URL bgUrl = getClass().getResource("/images/avatar_background.jpg");
        backgImage = new ImageIcon(bgUrl).getImage();
        setLayout(new BorderLayout());
        setOpaque(false);


        URL backUrl = getClass().getResource("/images/backButton.png");
        ImageIcon rawBack = new ImageIcon(backUrl);
        ImageIcon backIcon = new ImageIcon(
                rawBack.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)
        );
        JButton back = new JButton(backIcon);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setFocusPainted(false);
        back.addActionListener(e -> cards.show(cardHolder, "MENU"));




        URL tUrl = getClass().getResource("/images/TitleBack.png");
        ImageIcon rawTitle = new ImageIcon(tUrl);
        ImageIcon titleIcon = new ImageIcon(
                rawTitle.getImage().getScaledInstance(340, 100, Image.SCALE_SMOOTH)
        );

        JLabel title = new JLabel("IMPOSTAZIONI", titleIcon, JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setHorizontalTextPosition(JLabel.CENTER);
        title.setVerticalTextPosition(JLabel.CENTER);
        title.setHorizontalAlignment(SwingConstants.CENTER);



        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBar.add(back, BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);
        add(topBar, BorderLayout.NORTH);


        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(Box.createVerticalGlue());


        URL offUrl = getClass().getResource("/images/checkbox_off.png");
        URL onUrl  = getClass().getResource("/images/checkbox_on.png");
        ImageIcon rawOff = new ImageIcon(offUrl);
        ImageIcon rawOn  = new ImageIcon(onUrl);


        ImageIcon offScaled = new ImageIcon(
                rawOff.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)
        );
        ImageIcon onScaled = new ImageIcon(
                rawOn.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)
        );


        JCheckBox cbMusica = new JCheckBox("Musica");
        cbMusica.setIcon(offScaled);
        cbMusica.setSelectedIcon(onScaled);
        cbMusica.setPressedIcon(onScaled);
        cbMusica.setIconTextGap(8);
        cbMusica.setBorderPainted(false);
        cbMusica.setContentAreaFilled(false);
        cbMusica.setFocusPainted(false);
        cbMusica.setOpaque(false);
        cbMusica.setSelected(false);
        cbMusica.addItemListener(e -> {
            if (cbMusica.isSelected()) AudioManager.getInstance().toggleMusic();
            else                       AudioManager.getInstance().toggleMusic();
        });

        cbMusica.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension cbSize = new Dimension(200, cbMusica.getPreferredSize().height);
        cbMusica.setPreferredSize(cbSize);
        cbMusica.setMaximumSize(cbSize);
        center.add(cbMusica);
        center.add(Box.createVerticalStrut(10));


        JCheckBox cbEffetti = new JCheckBox("Effetti Sonori");
        cbEffetti.setIcon(offScaled);
        cbEffetti.setSelectedIcon(onScaled);
        cbEffetti.setPressedIcon(onScaled);
        cbEffetti.setIconTextGap(8);
        cbEffetti.setBorderPainted(false);
        cbEffetti.setContentAreaFilled(false);
        cbEffetti.setFocusPainted(false);
        cbEffetti.setOpaque(false);
        cbEffetti.setSelected(false);
        cbEffetti.addItemListener(e -> {
            if (cbEffetti.isSelected()) AudioManager.getInstance().toggleMusic();
            else                         AudioManager.getInstance().toggleMusic();
        });

        cbEffetti.setAlignmentX(Component.CENTER_ALIGNMENT);
        cbEffetti.setPreferredSize(cbSize);
        cbEffetti.setMaximumSize(cbSize);
        center.add(cbEffetti);
        center.add(Box.createVerticalStrut(20));



        JPanel selectMazzo = new JPanel();
        selectMazzo.setOpaque(false);
        selectMazzo.setAlignmentX(Component.CENTER_ALIGNMENT);



        URL leftUrl = getClass().getResource("/images/freccia_sinistra.png");
        ImageIcon rawLeft = new ImageIcon(leftUrl);
        ImageIcon leftIcon = new ImageIcon(
                rawLeft.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)
        );
        JButton btnLeft = new JButton(leftIcon);
        btnLeft.setBorderPainted(false);
        btnLeft.setContentAreaFilled(false);
        btnLeft.setFocusPainted(false);
        btnLeft.setAlignmentY(Component.CENTER_ALIGNMENT);




        ImageIcon deckIcon = new ImageIcon(
                getClass().getResource("/images/default_deck.png")
        );
        JLabel lblDeckIcon = new JLabel(deckIcon);
        lblDeckIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
        JLabel lblDeckName = new JLabel("Default");
        lblDeckName.setForeground(Color.WHITE);
        lblDeckName.setAlignmentY(Component.CENTER_ALIGNMENT);



        URL rightUrl = getClass().getResource("/images/freccia_destra.png");
        ImageIcon rawRight = new ImageIcon(rightUrl);
        ImageIcon rightIcon = new ImageIcon(
                rawRight.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH)
        );
        JButton btnRight = new JButton(rightIcon);
        btnRight.setBorderPainted(false);
        btnRight.setContentAreaFilled(false);
        btnRight.setFocusPainted(false);
        btnRight.setAlignmentY(Component.CENTER_ALIGNMENT);


        btnLeft.addActionListener(e -> { });
        btnRight.addActionListener(e -> { });

        selectMazzo.add(btnLeft);
        selectMazzo.add(Box.createHorizontalStrut(10));
        selectMazzo.add(lblDeckIcon);
        //selectMazzo.add(Box.createHorizontalStrut(5));
        //selectMazzo.add(lblDeckName);
        selectMazzo.add(Box.createHorizontalStrut(10));
        selectMazzo.add(btnRight);

        center.add(selectMazzo);
        center.add(Box.createVerticalGlue());
        add(center,BorderLayout.CENTER);


    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgImage, 0, 0, getWidth(), getHeight(), this);
    }
}
