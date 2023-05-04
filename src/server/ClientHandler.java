package server;

import java.io.InputStream;
import java.io.OutputStream;

interface ClientHandler {
    void handleClient(InputStream inFromclient, OutputStream outToClient);

    void close();
}
