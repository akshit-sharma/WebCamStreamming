package video;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel{
    private int i;
    private BufferedImage image;


    ImagePanel(){
        this.image = null;
        i=0;
    }

    public void update(BufferedImage image){
        this.image = image;
        System.out.println("repaint called");
        repaint();
        System.out.println("After repaint called");
//        try {
//            System.out.println("After repaint called 1");
//            //wait(100);
//            System.out.println("After repaint called 2");
//            //notify();
//            System.out.println("After repaint called 3");
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        System.out.println("Paint called");
        if(image!=null) {
            if (ClientSystem.flag)
                g.drawImage(image, 0, 0,200,200, null); // see javadoc for more info on the parameters
            image = null;
        }

    }

}