package video;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by akshit on 2/20/2016.
 */
public class ClientSystem {
    private static final int PORT =8088;
    private static InetAddress HOST_ADDRESS;
    private Socket sock = null;
    private void go(){
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.open();
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
            System.exit(1);
        }

        try {
            sock = new Socket("localhost", 8088);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {

            OutputStream out = null;
            try {
                Thread.currentThread().sleep(40);
                out = sock.getOutputStream();
                BufferedImage image = webcam.getImage();
                ImageIO.write(image, "JPG", out);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        byte [] addr = {10,60,82,1};
        try {
            HOST_ADDRESS = InetAddress.getByAddress(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(2);
        }
        new ClientSystem().go();
    }
}
