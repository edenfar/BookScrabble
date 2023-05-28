package server;

import java.io.IOException;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;


public class MyServer {

    int port;
    boolean stop;
    ClientHandler clientHandler;
    int maxThreadCount;
    ServerSocket server;
    ExecutorService threadPoolClient;
    Thread serverThread;

    public MyServer(int port, ClientHandler clientHandler, int maxThreadCount) {
        this.port = port;
        this.clientHandler = clientHandler;
        this.maxThreadCount = maxThreadCount;
        this.threadPoolClient = new ThreadPoolExecutor(1, maxThreadCount, 1L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    public void start() {
        stop = false;
        serverThread = new Thread(this::startServer);
        serverThread.start();
    }

    private void startServer() {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            while (!stop) {
                Socket clientSocket = server.accept();
                System.out.println("Client connected: " + clientSocket);
                try {
                    threadPoolClient.execute(() -> handleClient(clientSocket));
                } catch (RejectedExecutionException e) {
                    PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream());
                    outToClient.println("Max thread count reached. Cannot accept more clients at the moment.");
                    outToClient.flush();
                    outToClient.close();
                }
            }
        } catch (SocketException e) {
            System.out.println("Server stopped successfully");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }


    private void handleClient(Socket client) {
        try {
            clientHandler.handleClient(client.getInputStream(), client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientHandler.close();
        }
    }

    public void close() {
        stop = true;
        try {
            threadPoolClient.shutdown();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
