package server;

import server.Tile.Bag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class Game {

    final static int MAX_PLAYERS = 4;

    private Integer id;
    String name;
    Board board;
    Bag bag;
    List<Player> players;
    int numOfPlayers;
    Player currentPlayer;
    int rounds;
    int currentRound;

    public Game(String name, String[] fileNames, Player host, int rounds) {
        this.name = name;
        this.board = new Board(fileNames);
        this.bag = new Bag();
        this.players = new ArrayList<>(MAX_PLAYERS);
        this.players.add(host);
        this.currentPlayer = players.get(0);
        this.rounds = rounds;
        this.numOfPlayers = 1;
    }

    public int letterToInt(char letter) {
        //note that here A = 1 and in tails class A = 0
        if (Character.isUpperCase(letter)) {
            return letter - 'A' + 1;
        } else if (Character.isLowerCase(letter)) {
            return letter - 'a' + 1;
        } else {
            throw new IllegalArgumentException("Invalid letter: " + letter);
        }
    }

    public void orderPlayers() {
        int[] playersInt = new int[numOfPlayers];

        Tile tile;
        char letter;

        if (numOfPlayers == 1) {
            return;
        }

        for (int i = 0; i < numOfPlayers; i++) {
            if (players.get(i) != null) {
                tile = this.bag.getRand();
                letter = tile.letter;
                playersInt[i] = letterToInt(letter);
                this.bag.put(tile);
            }
        }

        players = Arrays.asList(orderPlayersByArray(players, playersInt));
        currentPlayer = players.get(0);
        sendCurrentPlayer();

    }

    public void sendCurrentPlayer() {
        for (Player p : players) {
            if (p != null) {
                p.sendCurrentPlayer(currentPlayer.getName());
            }
        }
    }

    public void sendCurrentRound() {
        for (Player p : players) {
            if (p != null) {
                p.sendCurrentRound(currentRound);
            }
        }
    }

    public static Player[] orderPlayersByArray(List<Player> players, int[] values) {
        List<Player> nonNullPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player != null) {
                nonNullPlayers.add(player);
            }
        }
        List<Player> sortedPlayers = new ArrayList<>(nonNullPlayers);
        sortedPlayers.sort(Comparator.comparing(player -> values[nonNullPlayers.indexOf(player)]));

        return sortedPlayers.toArray(new Player[0]);
    }

    public void setup() {
        this.orderPlayers();
        currentPlayer = players.get(0);
        sendCurrentPlayer();
        currentRound = 1;
        sendCurrentRound();
        for (Player player : players) {
            if (player != null){
                player.setTiles(bag.getRandomTiles(7));
                player.sendPlayerTiles();
                player.sendGameStart();
            }
        }
    }

    public boolean isOver() {
        return currentRound == rounds;
    }

    public void addPlayer(Player player) {
        if (numOfPlayers >= MAX_PLAYERS) {
            throw new UnsupportedOperationException("Maximum number of players reached.");
        }
        players.add(player);
        numOfPlayers++;
        sendGameToPlayer(player);
        sendPlayersToPlayers(player);
    }

    public void playTurn(Player player, Word word) {
        if (player != currentPlayer) {
            player.sendToPlayer.accept("Player " + player.getName() + " is not the current player");
            return;
        }
        playCurrentTurn(word);
    }

    private void playCurrentTurn(Word word) {
        if (!currentPlayer.hasTiles(word.getTiles())) {
            currentPlayer.notifyMissingTilesForWord(word);
            return;
        }
        int wordScore = board.tryPlaceWord(word);
        if (wordScore == 0) {
            currentPlayer.notifyIllegalWord(word);
            return;
        }
        this.sendBoard();
        currentPlayer.addScore(wordScore);
        int wordTilesCount = word.getTiles().length;
        currentPlayer.replaceTiles(word.getTiles(), bag.getRandomTiles(wordTilesCount));
        this.advanceCurrentPlayer();
        currentRound += 1;
        this.sendGameToPlayers();
    }

    private void sendBoard() {
        for (Player player : players) {
            if (player != null) {
                player.sendBoard(board);
            }
        }
    }
    public void playNullTurn() {
        this.advanceCurrentPlayer();
        currentRound += 1;
        this.sendGameToPlayers();
    }

    private void sendPlayersToPlayers(Player Except) {
        for (Player player : players) {
            if (player != null) {
                if (player != Except) player.sendPlayers(players);
            }
        }
    }

    private void sendGameToPlayers() {
        for (Player player : players) {
            if (player != null) {
                player.sendBoard(board);
                player.sendPlayers(players);
                player.sendCurrentPlayer(currentPlayer.getName());
                player.sendCurrentRound(currentRound);
                player.sendPlayerTiles();
                player.sendNewTurn();
                player.sendScore();
            }
        }
    }

    private void sendGameToPlayer(Player p) {
        p.sendBoard(board);
        p.sendPlayers(players);
        p.sendCurrentRound(currentRound);
        p.sendScore();
    }

    private void advanceCurrentPlayer() {
        int currentPlayerIndex = players.indexOf(currentPlayer);
        currentPlayerIndex = (currentPlayerIndex + 1) % numOfPlayers;
        currentPlayer = players.get(currentPlayerIndex);
    }
}
