package video;

import com.github.sarxos.webcam.Webcam;

import javax.swing.*;
import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by akshit on 2/20/2016.
 */
public class ServerSystem {

    private static final int PORT = 8088;
    private ImageView imageView;

    ServerSystem(){
        JFrame jFrame = new JFrame();

        imageView = new ImageView();

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

        ServerSocket sock = null;
        try {
            sock = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Could not instantiate socket:");
            e.printStackTrace();
            return;
        }

        Socket clientSock = null;

        while (true) {

            try {
                System.out.println("Waiting for connection...");
                clientSock = sock.accept();
                final Socket fin = clientSock;
                System.out.println("Connection accepted");
                System.out.println("Spawning thread...");
                Thread trd = new Thread(new Runnable(){
                    public void run(){
                        try {
                            try {
                                Thread.sleep(40);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            InputStream is =fin.getInputStream();
                            byte[] data = new byte[1024];
                            int count=0;
                            count = is.read(data, 0, 1024);
                            while (count != -1){
                                count = is.read(data, 0, 1024);
                            }
                            fin.close();
                            System.out.println("Done receiving");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }}
                });
                trd.start();
            }catch (IOException e){
                System.out.println("Could not accept");
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        new ServerSystem().go();
    }

}
