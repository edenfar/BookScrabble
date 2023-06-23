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
        Player player = null;
        Game game = null;

        out = new PrintWriter(outToClient);
        outList.add(out);
        in = new Scanner(inFromClient);
        inList.add(in);
        sendToPlayer = (String message) -> {
            out.println(message);
            out.flush();
        };
        Object request = parseRequest(in.nextLine());

        if (request instanceof HostRequest hostRequest) {

            player = getPlayer(hostRequest, sendToPlayer);
            game = createGameGetGame(hostRequest, player);

            player.sendGameName(game.name);
            player.sendPlayers(game.players);

            this.receiveStartGameSignal(in);
            game.setup();
        } else if (request instanceof GuestRequest guestRequest) {
            player = getPlayer(guestRequest, sendToPlayer);
            game = connectToGameGetGame(guestRequest);
            game.addPlayer(player);


        }
        while (!game.isOver()) {
            String move = in.nextLine();
            Word word = this.parseMove(move);
            game.playTurn(player, word);
        }
        out.flush();
    }

    public interface Request {
        String getName();
    }

    public record HostRequest(String name, String[] fileNames) implements Request {
        @Override
        public String getName() {
            return name;
        }
    }

    public record GuestRequest(String name, String gameName) implements Request {
        @Override
        public String getName() {
            return name;
        }
    }

    private Word parseMove(String move) {
        throw new UnsupportedOperationException();
    }

    private void receiveStartGameSignal(Scanner in) {
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

    public Game createGameGetGame(HostRequest request, Player p) {
        GamesManager gamesManager = GamesManager.get();
        return gamesManager.createGame(request.fileNames, p);
    }

    public Player getPlayer(Request request, Consumer<String> sendToPlayer) {
        return new Player(request.getName(), sendToPlayer);
    }

    public Game connectToGameGetGame(GuestRequest request) {
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

