package server;

import HTTPServer.DictionaryManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler {
    PrintWriter out;
    Scanner in;

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromclient);
        String request = in.nextLine();
        String[] args = request.split(",");
        String queryType = args[0];
        String[] queryArgs = Arrays.copyOfRange(args, 1, args.length);
        DictionaryManager dm = DictionaryManager.get();
        boolean result = false;
        if (Objects.equals(queryType, "Q"))
            result = dm.query(queryArgs);
        else if (Objects.equals(queryType, "C"))
            result = dm.challenge(queryArgs);
        out.println(result);
        out.flush();
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}
