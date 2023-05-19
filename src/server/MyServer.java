package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MyServer {

    int port;
    boolean stop;
    ClientHandler ch;
    int maxThreadCount;
    ServerSocket server;
    ExecutorService threadPool;

    public MyServer(int port, ClientHandler ch, int maxThreadCount) {
        this.port = port;
        this.ch = ch;
        this.maxThreadCount = maxThreadCount;
        this.threadPool = Executors.newFixedThreadPool(maxThreadCount);
    }

    public void start() {
        stop = false;
        threadPool.execute(this::startServer);
    }

    public void startServer() {
        AtomicInteger activeThreads = new AtomicInteger(0);

        try {
            server = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (!stop) {
                if (activeThreads.get() >= maxThreadCount) {
                    // Maximum thread count reached, send "Can't connect" message to the client
                    Socket clientSocket = server.accept();
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                    out.println("Can't connect: Maximum thread count reached.");
                    out.flush();
                    out.close();
                    clientSocket.close();
                    continue; // Skip handling this client and continue accepting new clients
                }
                Socket clientSocket = server.accept();
                System.out.println("Client connected: " + clientSocket);
                activeThreads.incrementAndGet();
                threadPool.execute(() -> {
                    handleClient(clientSocket);
                    activeThreads.decrementAndGet();
                });
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
        } catch (IOException | InterruptedException e) {
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
        threadPool.shutdown();
        try {
            if (server.isClosed())
                server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
