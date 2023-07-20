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

    public Tile[] tilesArr = {
            new Tile('A', 1),
            new Tile('B', 3),
            new Tile('C', 3),
            new Tile('D', 2),
            new Tile('E', 1),
            new Tile('F', 4),
            new Tile('G', 2),
            new Tile('H', 4),
            new Tile('I', 1),
            new Tile('J', 8),
            new Tile('K', 5),
            new Tile('L', 1),
            new Tile('M', 3),
            new Tile('N', 1),
            new Tile('O', 1),
            new Tile('P', 3),
            new Tile('Q', 10),
            new Tile('R', 1),
            new Tile('S', 1),
            new Tile('T', 1),
            new Tile('U', 1),
            new Tile('V', 4),
            new Tile('W', 4),
            new Tile('X', 8),
            new Tile('Y', 4),
            new Tile('Z', 10)
    };

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        PrintWriter out;
        Scanner in;
        Consumer<String> sendToPlayer;
        Player player = null;
        Game game = null;
        String name;

        out = new PrintWriter(outToClient);
        outList.add(out);
        in = new Scanner(inFromClient);
        inList.add(in);
        sendToPlayer = (String message) -> {
            out.println(message);
            out.flush();
        };

        while (game == null) {
            Object request = parseRequest(in.nextLine());

            if (request instanceof HostRequest hostRequest) {
                name = hostRequest.name;
                player = getPlayer(name, sendToPlayer);
                game = createGame(hostRequest, player);

                player.sendGameName(game.name);
                player.sendPlayers(game.players);
                player.sendBoard(game.board);
                player.sendMaxRounds(game.maxRounds);
                player.sendScore();

                this.receiveStartGameSignal(in);
                game.setup();
            } else if (request instanceof GuestRequest guestRequest) {
                name = guestRequest.name;
                player = getPlayer(name, sendToPlayer);
                game = connectToGame(guestRequest);
                if (game == null) {
                    player.notifyIllegalGame();
                } else
                    game.addPlayer(player);
            }
        }
        while (true) {
            if (game.isOver()) {
                game.endGame();
                break;
            }

            String move = in.nextLine();
            if (move.startsWith(","))//No word played
                game.playNullTurn();
            else {
                //The tiles to replace can be different from the tiles in the word
                int lastCommaIndex = move.lastIndexOf(",");
                String wordToReplace = move.substring(lastCommaIndex + 1).trim();
                move = move.substring(0, lastCommaIndex).trim();

                String[] words = move.split(",");
                StringBuilder temp = new StringBuilder();
                for (String word : words) {
                    if (!Objects.equals(word, words[0]))
                        temp.append(",").append(word);
                }
                wordToReplace = wordToReplace + temp;

                Word wordToReplaceW = this.parseMove(wordToReplace);
                Word word = this.parseMove(move);
                game.playTurn(player, word, wordToReplaceW);
            }
        }
        out.flush();
    }

    public record HostRequest(String name, String[] fileNames) {
    }

    public record GuestRequest(String name, String gameName) {
    }

    private Word parseMove(String move) {
        String[] substrings = extractSubstrings(move);
        // Accessing each substring
        String wordString = substrings[0];
        int row = Integer.parseInt(substrings[1]);
        int col = Integer.parseInt(substrings[2]);
        boolean vertical = Boolean.parseBoolean(substrings[3]);


        Tile[] tiles = new Tile[wordString.length()];
        int i = 0;

        for (char c : wordString.toCharArray()) {
            for (Tile t : tilesArr) {
                if (t.letter == c) {
                    tiles[i] = tilesArr[Arrays.asList(tilesArr).indexOf(t)];
                    break;
                }
            }
            i++;
        }

        Word word = new Word(tiles, row, col, vertical);
        System.out.println(move);
        return word;
    }

    public static String[] extractSubstrings(String input) {
        String[] substrings = input.split(",");
        return substrings;
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
        System.out.println("Invalid request received: " + request);
        throw new UnsupportedOperationException("Invalid request received: " + request);
    }

    public Game createGame(HostRequest request, Player p) {
        GamesManager gamesManager = GamesManager.get();
        return gamesManager.createGame(request.fileNames, p);
    }

    public Player getPlayer(String name, Consumer<String> sendToPlayer) {
        return new Player(name, sendToPlayer);
    }

    public Game connectToGame(GuestRequest request) {
        Game game = null;
        GamesManager gamesManager = GamesManager.get();
        game = gamesManager.getGame(request.gameName);
        return game;
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

