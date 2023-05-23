package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.*;

public class MyServer {

    int port;
    boolean stop;
    ClientHandler ch;
    int maxThreadCount;
    ServerSocket server;
    ExecutorService threadPoolClient;
    Thread serverThread;

    public MyServer(int port, ClientHandler ch, int maxThreadCount) {
        this.port = port;
        this.ch = ch;
        this.maxThreadCount = maxThreadCount;
        this.threadPoolClient = new ThreadPoolExecutor(1, maxThreadCount, 1L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    public void start() {
        stop = false;
        serverThread = new Thread(this::startServer);
        serverThread.start();
    }

    public void startServer() {

        try {
            server = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (!stop) {

                    Socket clientSocket = server.accept();
                    System.out.println("Client connected: " + clientSocket);
                try {  threadPoolClient.execute(() -> {
                        handleClient(clientSocket);
                    });
                }catch (RejectedExecutionException e) {
                        System.out.println("Max thread count reached. Cannot accept more clients at the moment.\n");
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }


    private void handleClient(Socket client) {
        try {
            ch.handleClient(client.getInputStream(), client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ch.close();

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
