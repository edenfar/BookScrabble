package server;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class PlayerHandler_test implements ClientHandler {
    public record Connection(PrintWriter out, Scanner in) {
    }

    public Set<Connection> connections;

    public PlayerHandler_test() {
        this.connections = new HashSet<>();
    }

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        Connection connection = new Connection(new PrintWriter(outToClient), new Scanner(inFromclient));
        connections.add(connection);
        try {
            Thread.sleep(5000);
            System.out.println(connection.in.nextLine());
            String text = "Hello from Server !";
            Thread.sleep(2000);
            connection.out.println(text);
            connection.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void client1(int port) {
        new Thread(() -> {
            try {
                Socket server = new Socket("localhost", port);
                String text = "Hello from client !";
                PrintWriter outToServer = new PrintWriter(server.getOutputStream());
                Scanner in = new Scanner(server.getInputStream());

                outToServer.println(text);
                outToServer.flush();
                System.out.println(in.nextLine());
                if (in.hasNext()) {
                    String response = in.next();
                    System.out.println("Got unexpected message from server: " + response);
                }
                in.close();
                outToServer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void close() {
        for (Connection connection: connections) {
            connection.out.close();
            connection.in.close();
        }
    }
}

