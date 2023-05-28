package server;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class PlayerHandler implements ClientHandler {
    PrintWriter out;
    Scanner in;
    Player player;
    Game game;

    /*
    All the communication with the player should happen here, and not in Player class
    We assume that the player has a thread listening to messages from the server (i.e., this class)
     */

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromClient);
        Object request = parseRequest(in.nextLine());
        if (request instanceof HostRequest hostRequest) {
            this.createGameByRequest(hostRequest);
            player.sendGameName(game.name);
            this.receiveStartGameSignal();
            game.setup();
        } else if (request instanceof GuestRequest guestRequest) {
            this.connectToGameByRequest(guestRequest);
        }
        while (!game.isOver()) {
            String move = in.nextLine();
            Word word = this.parseMove(move);
            game.playTurn(player, word);
        }
        out.flush();
    }


    public record HostRequest(String name, String[] fileNames) {
    }

    public record GuestRequest(String name, String gameName) {
    }

    private Word parseMove(String move) {
        throw new UnsupportedOperationException();
    }

    private void receiveStartGameSignal() {
        String request = in.nextLine();
        // assert it is a start request
        throw new UnsupportedOperationException();
    }

    private static Object parseRequest(String request) {
        /* Request structure:
        host,<player name>,<file names separated by commas>
        guest,<player name>,<game name>
         */
        throw new UnsupportedOperationException();
    }

    public void createGameByRequest(HostRequest request) {
        GamesManager gamesManager = GamesManager.get();
        player = new Player(request.name, out::println);
        game = gamesManager.createGame(request.fileNames, player);
    }

    public void connectToGameByRequest(GuestRequest request) {
        GamesManager gamesManager = GamesManager.get();
        player = new Player(request.name, out::println);
        game = gamesManager.getGame(request.gameName);
        game.addPlayer(player);
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}

