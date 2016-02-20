package ui.client;

import com.github.sarxos.webcam.Webcam;
import config.Configuration;
import ui.ImagePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * Created by akshit on 2/20/2016.
 */
public class ClientGUI implements WindowListener {

    private JTextField jTextField;
    private TextArea chatArea;
    private ImagePanel imagePanel;

    public ClientGUI(){

        JFrame jFrame = new JFrame();

        JPanel imageArea = new JPanel();
        imageArea.setLayout(new BorderLayout());

        imagePanel = new ImagePanel();

        imageArea.add(imagePanel, BorderLayout.CENTER);

        chatArea = new TextArea();
        chatArea.setEditable(false);

        JPanel chatBar = new JPanel();
        jTextField = new JTextField();
        JButton sendButton = new JButton("Send");

        chatBar.setLayout(new BorderLayout());
        chatBar.add(jTextField, BorderLayout.CENTER);
        chatBar.add(sendButton,BorderLayout.EAST);

        imageArea.setSize(500, 200);

        jFrame.setLayout(new BorderLayout());

        jFrame.add(imageArea, BorderLayout.CENTER);
        jFrame.add(chatArea, BorderLayout.EAST);
        jFrame.add(chatBar, BorderLayout.SOUTH);

        jFrame.addWindowListener(this);

        jFrame.setSize(800,300);
        jFrame.setVisible(true);

        new VideoStreamer(imagePanel).start();
    }

    public static void main(String[] args) {
        for(int i=0;i<4;i++)
            Configuration.SERVER_ADDRESS[i] = Byte.parseByte(args[i]);
        if(args.length>4){
            Configuration.SERVER_PORT = Integer.parseInt(args[4]);
        }

        new ClientGUI();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        jTextField.requestFocus();
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }


}

class VideoStreamer extends Thread{

    private int status;
    private InputStream inputStream;
    private boolean connected;
    private static ImagePanel imagePanel;
    private static InetAddress clientAdd;

    // 0 -> control, 1 -> Recieve, 2 -> Transmit
    private VideoStreamer(int status){
        this.status = status;
        connected = false;
    }

    public VideoStreamer(ImagePanel imagePanel){
        this(0);
        VideoStreamer.imagePanel = imagePanel;
    }

    private void recieve(){
        ServerSocket serverSocket = null;
        Socket socket = null;
        BufferedImage image;

        while(true){
            try{
                serverSocket = new ServerSocket(Configuration.CLIENT_PORT);
                socket = serverSocket.accept();
                clientAdd = socket.getInetAddress();

                inputStream = socket.getInputStream();

                while (true){
                    try{
                        byte[] sizeAr = new byte[4];
                        inputStream.read(sizeAr);
                        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                        byte[] imageAr = new byte[size];
                        inputStream.read(imageAr);

                        image = ImageIO.read(new ByteArrayInputStream(imageAr));
                        imagePanel.update(image);
                        image = null;

                        serverSocket.close(); //right here
                        socket.close();
                        break;
                    }catch (IOException e){
                        e.printStackTrace();
                        break;
                    }
                }

            }catch (IOException e){
                System.err.println("Could not accept");
                e.printStackTrace();

            }
        }


    }

    private void transmit(){
        Socket sock = null;
        Webcam webcam = Webcam.getDefault();
        if(webcam != null){
            webcam.open();
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
            System.exit(1);
        }

        InetAddress connectAdd = null;

        try {
            connectAdd = InetAddress.getByAddress(Configuration.SERVER_ADDRESS);

            while(true){

                try {

                    sock = new Socket(connectAdd,Configuration.SERVER_PORT);

                    new VideoStreamer(1).start();

                    OutputStream outputStream = sock.getOutputStream();
                    BufferedImage image = webcam.getImage();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(image, "JPG", byteArrayOutputStream);

                    byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                    outputStream.write(size);
                    outputStream.write(byteArrayOutputStream.toByteArray());
                    outputStream.flush();
                    TimeUnit.MILLISECONDS.sleep(10);
                    sock.close();

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        switch (status){
            case 0: new VideoStreamer(2).start();
                break;
            case 1:
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recieve();
                break;
            case 2:
                transmit();
                break;
        }

    }
}
