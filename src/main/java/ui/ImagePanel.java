package ui;

import video.ClientSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel{
    private BufferedImage image;


    public ImagePanel(){
        this.image = null;
    }

    public void update(BufferedImage image){
        this.image = image;
        repaint();

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(image!=null) {
            if (ClientSystem.flag)
                g.drawImage(image, 0, 0,200,200, null); // see javadoc for more info on the parameters
            image = null;
        }

    }

}