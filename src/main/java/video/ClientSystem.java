package video;

import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * Created by akshit on 2/20/2016.
 */
public class ClientSystem {
    private static final int PORT =8088;
    private static InetAddress HOST_ADDRESS;
    private Socket sock = null;
    public static boolean flag = true;
    private void go() {
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.open();
            System.out.println("Webcam: " + webcam.getName());
        } else {
            System.out.println("No webcam detected");
            System.exit(1);
        }


        while (true) {

            try {




                    sock = new Socket("localhost", 8088);
                    OutputStream outputStream = sock.getOutputStream();
                    BufferedImage image = webcam.getImage();
                    //FileOutputStream fileOutputStream = (FileOutputStream) sock.getOutputStream();
                    //File file = new File("test.jpg");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(image, "JPG", byteArrayOutputStream);

                    byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                    outputStream.write(size);
                    outputStream.write(byteArrayOutputStream.toByteArray());
                    outputStream.flush();
                     TimeUnit.MILLISECONDS.sleep(10);
                    sock.close();

                //outputStream.flush();



                    //Path path = Paths.get(file.getPath());
                    //byte[] data = Files.readAllBytes(path);
                    //fileOutputStream.write(data);
                    //out.writeTo(sock.getOutputStream());
                    //sock.getOutputStream().write(out.toByteArray());

                } catch (IOException e) {
                    e.printStackTrace();

                } catch (InterruptedException e) {
                    e.printStackTrace();


            }
//
//        try {
//            sock.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//
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
