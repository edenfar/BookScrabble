package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        threadPool.execute(() -> StartServer());
    }

    public void StartServer() {
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(1000);
            while (!stop) {
                try {
                    Socket client = server.accept();
                    this.handleClient(client);

                } catch (SocketTimeoutException l) {
                }
            }
            //server.close();
        } catch (IOException t) {
            t.printStackTrace();
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
                ch.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
