package server;

import java.io.IOException;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;


public class MyServer {

    int port;
    boolean stop;
    ClientHandler ch;
    int maxThreadCount;
    ServerSocket server;
    ExecutorService threadPoolClient;
    Thread serverThread;
    private AtomicInteger activeClients = new AtomicInteger(0);

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
                try {
                        threadPoolClient.execute(() -> {
                            activeClients.incrementAndGet();
                            handleClient(clientSocket);
                            activeClients.decrementAndGet();
                        });
                    }catch (RejectedExecutionException e) {
                        try {
                            OutputStream outputStream = clientSocket.getOutputStream();
                            outputStream.write("Max thread count reached. Cannot accept more clients at the moment.\n".getBytes());
                            outputStream.flush();
                        } catch (IOException ioException) {
                        ioException.printStackTrace();
                        }
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

        while (activeClients.get() > 0) {
            //Wait
            //Not sure if a thread.sleep is necessary here
        }
        stop = true;
        try {
                threadPoolClient.shutdown();
                server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
