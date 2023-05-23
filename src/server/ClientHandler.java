package server;

import java.io.InputStream;
import java.io.OutputStream;

interface ClientHandler {
    void handleClient(InputStream inFromClient, OutputStream outToClient);

    void close();
}
