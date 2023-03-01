package server;
import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(3232);
            System.out.println("Server started.");

            // cnx mysql
            String url = "jdbc:mysql://localhost/test";
            String user = "root";
            String password = "";
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("connection established");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("User connected.");

                //thread t3 handle request
                Thread thread = new Thread(new ClientHandler(socket, connection));
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class ClientHandler implements Runnable {
    private Socket socket;
    private Connection connection;
    public ClientHandler(Socket socket, Connection connection) {
        this.socket = socket;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {

            // message t3 client
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = in.readLine();


            // parsing
            String[] parts = message.split(",");
            String type = parts[0];
            String name = parts[1];
            String ip = parts[2];
            String path = parts[3];

            // type based handling
            if (type.equals("PUBLISH")) {
                handlePublisher(name, ip ,path);

            } else if (type.equals("LOOKUP")) {
                handleLookup(name);
            }
            // 9fla t3 socket
            socket.close();
            System.out.println("User disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePublisher(String name, String ip,String path) throws SQLException, IOException {

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM publishers WHERE name = ? AND ip = ?");
        statement.setString(1, name);
        statement.setString(2, ip);
        ResultSet result = statement.executeQuery();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        if (!result.next()) {
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO publishers (name, ip ,path) VALUES (?, ?, ?)");
            insertStatement.setString(1, name);
            insertStatement.setString(2, ip);
            insertStatement.setString(3, path);
            insertStatement.executeUpdate();
            out.println("New publisher " + name + " added with IP " + ip+"and path"+path);
        } else {

            out.println("Publisher " + name + " with IP " + ip + " already exists");
        }
    }

    private void handleLookup(String name) throws SQLException, IOException {
        String S="";
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM publishers WHERE name = ?");
        statement.setString(1, name);
        ResultSet result = statement.executeQuery();

        if (result.next()) {

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            do {
                String ip = result.getString("ip");
                String path = result.getString("path");
                S=S+"("+ip+"-"+path+") ";

            } while (result.next());
            out.println(S);
            System.out.println("Lookup for " + name + " succeeded.");
        } else {

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("NOT FOUND");
        }
    }
}
