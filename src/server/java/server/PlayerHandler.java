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
            player.sendRounds(game.rounds);
            player.sendScore();

            receiveStartGameSignal(in);
            game.setup();
        } else if (parsedRequest instanceof GuestRequest guestRequest) {
            name = guestRequest.name;
            player = createPlayer(name, sendToPlayer);
            game = connectToGame(guestRequest.gameName);
            if (game == null) {
                player.notifyIllegalGame();
                return;
            } else
                game.addPlayer(player);
        } else if (parsedRequest instanceof LoadRequest loadRequest) {
            name = loadRequest.name;
            String gameName = loadRequest.gameName;
            game = loadGame(gameName);
            player = game.players.stream().filter(p -> p.getName().equals(name)).findAny().orElse(null);
            if (player == null) {
                sendToPlayer.accept("Player " + name + " not found on game " + gameName);
                return;
            }
            player.setSendToPlayer(sendToPlayer);
            game.sendGameToPlayer(player);

            receiveStartGameSignal(in);
            game.sendOnStart();
        } else {
            sendToPlayer.accept("Invalid request accepted: " + request);
            return;
        }
        while (!game.isOver()) {
            request = in.nextLine();
            if (request.equals("save"))
                saveGame(game);
            else if (request.startsWith(","))//No word played
                game.playNullTurn();
            else {
                //The tiles to replace can be different from the tiles in the word
                int lastCommaIndex = request.lastIndexOf(",");
                String tilesToReplace = request.substring(lastCommaIndex + 1).trim();
                request = request.substring(0, lastCommaIndex).trim();
                Word word = this.parseMove(request, player, game);
                game.playTurn(player, word, stringToTile(tilesToReplace, player, game));
            }
        }
        game.endGame();
    }

    private Tile[] stringToTile(String s, Player p, Game g) {
        Tile[] tiles = new Tile[s.length()];
        int i = 0;
        for (char c : s.toCharArray()) {
            for (Tile t : p.getTiles()) {
                if (t.letter == c) {
                    tiles[i] = p.getTile(c);
                    break;
                } else
                    tiles[i] = g.getBoard().getTile(c);
            }
            i++;
        }
        return tiles;
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

    public record LoadRequest(String name, String gameName) {
    }

    private Word parseMove(String move, Player p, Game g) {
        String[] substrings = move.split(",");
        // Accessing each substring
        String wordString = substrings[0];
        int row = Integer.parseInt(substrings[1]);
        int col = Integer.parseInt(substrings[2]);
        boolean vertical = Boolean.parseBoolean(substrings[3]);

        Tile[] tiles;

        tiles = stringToTile(wordString, p, g);
        return new Word(tiles, row, col, vertical);
    }

    private static void receiveStartGameSignal(Scanner in) {
        String request = in.nextLine();
        //TODO: to change
        if (!Objects.equals(request, "start")) {
            throw new UnsupportedOperationException("Invalid request received: " + request);
        }
    }

    private static Object parseRequest(String request) {
        /* Request structure:
        host,<player name>,<file names separated by commas>
        guest,<player name>,<game name>
        load,<player name>,<game name>
         */
        String[] params = request.split(",");
        if (Objects.equals(params[0], "guest"))
            return new GuestRequest(params[1], params[2]);
        if (Objects.equals(params[0], "host"))
            return new HostRequest(params[1], Arrays.copyOfRange(params, 2, params.length));
        if (Objects.equals(params[0], "load"))
            return new LoadRequest(params[1], params[2]);
        throw new UnsupportedOperationException("Invalid request received: " + request);
    }

    public static Game createGame(HostRequest request, Player p) {
        GamesManager gamesManager = GamesManager.get();
        return gamesManager.createGame(request.fileNames, p);
    }

    public static Player createPlayer(String name, Consumer<String> sendToPlayer) {
        return new Player(name, sendToPlayer);
    }

    public static Game connectToGame(String gameName) {
        GamesManager gamesManager = GamesManager.get();
        return gamesManager.getGame(gameName);
    }

    private static Game loadGame(String gameName) {
        GamesManager gamesManager = GamesManager.get();
        return gamesManager.loadGame(gameName);
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

