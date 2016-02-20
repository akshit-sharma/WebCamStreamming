package video;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by akshit on 2/20/2016.
 */
public class ServerSystem {

    private static final int PORT = 8088;
    private ImagePanel imagePanel;
    private InputStream inputStream;

    ServerSystem(){
        JFrame jFrame = new JFrame();

        imagePanel = new ImagePanel();

        jFrame.add(imagePanel);

        jFrame.setSize(400,600);
        jFrame.setVisible(true);
    }

    public void go(){
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
            System.exit(1);
        }

        ServerSocket serverSocket = null;
//        try {
//
//        } catch (IOException e) {
//            System.out.println("Could not instantiate socket:");
//            e.printStackTrace();
//            return;
//        }

        Socket socket = null;
        BufferedImage image;

        while (true) {

            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Waiting for connection...");
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                //final Socket fin = clientSock;
                System.out.println("Connection accepted");

                        while (true) {
                            System.out.println("Sleeping thread...");
                            try {

                                   // Thread.currentThread().sleep(200);
                                byte[] sizeAr = new byte[4];
                                inputStream.read(sizeAr);
                                int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                                byte[] imageAr = new byte[size];
                                inputStream.read(imageAr);

                                image = ImageIO.read(new ByteArrayInputStream(imageAr));
                                imagePanel.update(image);
                                image = null;

//                                System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
                                System.out.println("Done receiving");
                                //TimeUnit.SECONDS.sleep(2);
                                serverSocket.close(); //right here
                                socket.close();
                                break;
                                }  catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("Hell0ooo");
                                break;
                            }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        }
            }catch (IOException e){
                System.out.println("Could not accept");
                e.printStackTrace();
                break;
            }

        }

    }

    public static void main(String[] args) {
        new ServerSystem().go();
    }

}
