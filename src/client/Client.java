package client;
import java.io.*;
import java.net.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public final static String
            File_Recieved = "C:/downloader_java/downloaded1.pdf";
    public final static int FILE_SIZE = 6022386;

    public static void main(String[] args) {
        int bytesRead;
        int current = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        Scanner entry = new Scanner(System.in);

        try {
            Socket socket = new Socket("192.168.1.102", 3232);
            System.out.println("enter the name of the resource");
            String resource=entry.next();
            String message = "LOOKUP,"+resource+",_,_";
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);


            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();
            if (response.equals("NOT FOUND")) {
                System.out.println("resource not found.");
            } else {
                System.out.println("the resource " +resource+ " is available in these ips:\n" + response);
                Socket sock = null;
                System.out.println("copy one of the above ips in this format ip-port-path to try and connect to the host \n");
                String pubres=entry.next();
                String[] parts = pubres.split("-");
                String ip = parts[0];
                String port = parts[1];
                String path = parts[2];

                int SOCKET_PORT = Integer.parseInt(String.valueOf(port));
                String SERVER = ip;

                try{
                sock = new Socket(SERVER, SOCKET_PORT);
                PrintWriter outP = new PrintWriter(sock.getOutputStream(), true);
                outP.println(path);
                System.out.println("Downloading...");
                    byte [] mybytearray  = new byte [FILE_SIZE];
                    InputStream is = sock.getInputStream();
                    fos = new FileOutputStream(File_Recieved);
                    bos = new BufferedOutputStream(fos);
                    bytesRead = is.read(mybytearray,0,mybytearray.length);
                    current = bytesRead;
                    do {
                        bytesRead =
                                is.read(mybytearray, current, (mybytearray.length-current));
                        if(bytesRead >= 0) current += bytesRead;
                    } while(bytesRead > -1);

                    bos.write(mybytearray, 0 , current);
                    bos.flush();
                    System.out.println("File " + File_Recieved
                            + " downloaded (" + current + " bytes read)");
                    sock.close();


                }catch (Exception e) {
                    System.out.println("The publisher isn't available right now.");
                }
            }
            // Close the socket
            socket.close();
            System.out.println("Message sent to server.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
