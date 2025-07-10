package view.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import view.audio.AudioManager;



/*La classe SettingPanel rappresenta la schermata delle impostazioni del gioco. */
public class SettingPanel extends JPanel {
    private final Image backgImage;

    /**
     * Costruttore della classe SettingPanel.
     * Inizializza il pannello delle impostazioni con un'immagine di sfondo,
     * un pulsante per tornare al menu principale e due checkbox per le opzioni audio.
     */
    public SettingPanel(CardLayout cards, JPanel cardHolder) {
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

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBar.add(back, BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);


        JPanel placeholder = new JPanel();
        placeholder.setOpaque(false);
        placeholder.setPreferredSize(back.getPreferredSize());
        topBar.add(placeholder, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);


        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));


        // Caricamento icone CheckBox

        URL offUrl = getClass().getResource("/images/checkbox_off.png");
        URL onUrl  = getClass().getResource("/images/checkbox_on.png");
        ImageIcon offScaled = new ImageIcon(new ImageIcon(offUrl).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
        ImageIcon onScaled = new ImageIcon(new ImageIcon(onUrl).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));



        Dimension checkBoxSize = new Dimension(280, 80); // Definiamo una dimensione fissa


        center.add(Box.createRigidArea(new Dimension(0, 60)));

        // CheckBox Musica
        JCheckBox cbMusica = new JCheckBox("Musica");
        cbMusica.setFont(new Font("SansSerif", Font.BOLD, 22)); // Font modificato
        cbMusica.setForeground(Color.WHITE);                     // Colore modificato
        cbMusica.setIcon(offScaled);
        cbMusica.setSelectedIcon(onScaled);
        cbMusica.setPressedIcon(onScaled);
        cbMusica.setIconTextGap(8);
        cbMusica.setOpaque(false);
        cbMusica.setSelected(true); // Se vuoi che la musica parta attiva
        cbMusica.addItemListener(e -> AudioManager.getInstance().toggleMusic());
        cbMusica.setAlignmentX(Component.CENTER_ALIGNMENT);
        cbMusica.setPreferredSize(checkBoxSize);
        cbMusica.setMaximumSize(checkBoxSize);
        center.add(cbMusica);
        center.add(Box.createVerticalStrut(10));

        //CheckBox degli Effetti Sonori
        JCheckBox cbEffetti = new JCheckBox("Effetti Sonori");
        cbEffetti.setFont(new Font("SansSerif", Font.BOLD, 22)); // Font modificato
        cbEffetti.setForeground(Color.WHITE);                      // Colore modificato
        cbEffetti.setIcon(offScaled);
        cbEffetti.setSelectedIcon(onScaled);
        cbEffetti.setPressedIcon(onScaled);
        cbEffetti.setIconTextGap(8);
        cbEffetti.setOpaque(false);
        cbEffetti.setSelected(true);
        cbEffetti.addItemListener(e -> AudioManager.getInstance().toggleSoundEffects());
        cbEffetti.setAlignmentX(Component.CENTER_ALIGNMENT);
        cbEffetti.setPreferredSize(checkBoxSize);
        cbEffetti.setMaximumSize(checkBoxSize);
        center.add(cbEffetti);


        center.add(Box.createVerticalGlue());
        add(center, BorderLayout.CENTER);
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgImage, 0, 0, getWidth(), getHeight(), this);
    }
}