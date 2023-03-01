package publisher;
import java.io.File;
import java.io.FileInputStream;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.Locale;
import java.util.Scanner;

public class Publisher {
    public String FILE_TO_SEND = null;
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    private ThreadedSocket threadedSocket;

    public Publisher() {
        // initialize both sockets in the constructor
        threadedSocket = new ThreadedSocket();
    }
    private class ThreadedSocket extends Thread {
        private ServerSocket serverSocket;
        private boolean isRunning;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;

        public ThreadedSocket() {
            try {
                serverSocket = new ServerSocket(1515);
                isRunning = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
           while(isRunning){
                try {
                         Socket socket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         String response = in.readLine();
                         response=response.toUpperCase();
                         FILE_TO_SEND=response;
                         File myFile = new File(FILE_TO_SEND);
                         byte [] mybytearray  = new byte [(int)myFile.length()];
                         fis = new FileInputStream(myFile);
                         bis = new BufferedInputStream(fis);
                         bis.read(mybytearray,0,mybytearray.length);
                         os = socket.getOutputStream();
                         System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
                         os.write(mybytearray,0,mybytearray.length);
                         os.flush();
                         System.out.println("Done.");
                         bis.close();
                         os.close();
                         socket.close();

            } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
           }

        }
    }


    public static void main(String[] args) {
        Scanner entry = new Scanner(System.in);
        Publisher socketClass = new Publisher();
        socketClass.threadedSocket.start();
            try {
                Socket socket = new Socket("192.168.1.100", 3232);
                System.out.println("enter the name of the resource\n");
                String name=entry.next();
                System.out.println("enter the ip in this format 'ip-port''\n");
                String ipport=entry.next();
                System.out.println("enter the local path of the resource''\n");
                String path=entry.next();
                String message = "PUBLISH,"+name+","+ipport+","+path;
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = in.readLine();
                System.out.println(response);
                socket.close();
                System.out.println("Message sent to server.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}