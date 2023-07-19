package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Consumer;

public class PlayerHandler implements ClientHandler {
    /*
    All the communication with the player should happen here, and not in Player class
    We assume that the player has a thread listening to messages from the server (i.e., this class)
     */
    List<PrintWriter> outList = new ArrayList<>();
    List<Scanner> inList = new ArrayList<>();

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        PrintWriter out;
        Scanner in;
        Consumer<String> sendToPlayer;
        Player player;
        Game game;
        String name;

        out = new PrintWriter(outToClient);
        outList.add(out);
        in = new Scanner(inFromClient);
        inList.add(in);
        sendToPlayer = (String message) -> {
            out.println(message);
            out.flush();
        };
        String request = in.nextLine();
        Object parsedRequest = parseRequest(request);

        if (parsedRequest instanceof HostRequest hostRequest) {
            name = hostRequest.name;
            player = createPlayer(name, sendToPlayer);
            game = createGame(hostRequest, player);

            player.sendGameName(game.name);
            player.sendPlayers(game.players);
            player.sendBoard(game.board);
            player.sendScore();

            receiveStartGameSignal(in);
            game.setup();
        } else if (parsedRequest instanceof GuestRequest guestRequest) {
            name = guestRequest.name;
            player = createPlayer(name, sendToPlayer);
            game = connectToGame(guestRequest);
            game.addPlayer(player);
        } else {
            sendToPlayer.accept("Invalid request accepted: " + request);
            return;
        }
        while (!game.isOver()) {
            request = in.nextLine();
            if (request.equals("save"))
                saveGame(game);
            else if (request.startsWith(",")) // No word played
                game.playNullTurn();
            else {
                Word word = parseMove(request, player);
                game.playTurn(player, word);
            }
        }
    }

    private static void saveGame(Game game) {
        GamesManager gamesManager = GamesManager.get();
        gamesManager.saveGame(game.name);
        System.out.printf("Game %s saved%n", game.name);
    }

    public record HostRequest(String name, String[] fileNames) {
    }

    public record GuestRequest(String name, String gameName) {
    }

    private static Word parseMove(String move, Player player) {
        String[] substrings = move.split(",");

        // Accessing each substring
        String wordString = substrings[0];
        int row = Integer.parseInt(substrings[1]);
        int col = Integer.parseInt(substrings[2]);
        boolean vertical = Boolean.parseBoolean(substrings[3]);

        Tile[] tiles = new Tile[wordString.length()];
        int i = 0;
        for (char c : wordString.toCharArray()) {
            if (player.getTile(c) == null)
                throw new IllegalArgumentException("Player does not have tile " + c);
            tiles[i] = player.getTile(c);
            i++;
        }
        return new Word(tiles, row, col, vertical);
    }

    private static void receiveStartGameSignal(Scanner in) {
        String request = in.nextLine();
        //TODO: to change
        if (!Objects.equals(request, "start"))
            throw new UnsupportedOperationException("Invalid request received: " + request);
    }

    private static Object parseRequest(String request) {
        /* Request structure:
        host,<player name>,<file names separated by commas>
        guest,<player name>,<game name>
         */
        System.out.println("received request: " + request);
        String[] params = request.split(",");
        if (Objects.equals(params[0], "guest"))
            return new GuestRequest(params[1], params[2]);
        if (Objects.equals(params[0], "host"))
            return new HostRequest(params[1], Arrays.copyOfRange(params, 2, params.length));
        throw new UnsupportedOperationException("Invalid request received: " + request);
    }

    public static Game createGame(HostRequest request, Player p) {
        GamesManager gamesManager = GamesManager.get();
        return gamesManager.createGame(request.fileNames, p);
    }

    public static Player createPlayer(String name, Consumer<String> sendToPlayer) {
        return new Player(name, sendToPlayer);
    }

    public static Game connectToGame(GuestRequest request) {
        GamesManager gamesManager = GamesManager.get();
        return gamesManager.getGame(request.gameName);
    }

    @Override
    public void close() {
        for (Scanner in : inList) {
            in.close();
        }
        for (PrintWriter out : outList) {
            out.close();
        }
    }
}

