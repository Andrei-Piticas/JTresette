package view.MainMenu;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private final Image backImg;

    public BackgroundPanel(Image backImg) {
        this.backImg = backImg;

        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backImg,0,0,getWidth(),getHeight(),this);
    }
}
