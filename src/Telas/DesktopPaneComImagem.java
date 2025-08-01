package Telas;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;

public class DesktopPaneComImagem extends JDesktopPane {
    
    private Image imagemDeFundo;

    public DesktopPaneComImagem(String caminhoImagem) {
        imagemDeFundo = new ImageIcon(getClass().getResource(caminhoImagem)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagemDeFundo != null) {
            g.drawImage(imagemDeFundo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
