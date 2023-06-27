package server;

import server.Tile.Bag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class Game {

    final static int MAX_PLAYERS = 4;

    String name;
    Board board;
    Bag bag;
    Player[] players;
    int numOfPlayers;
    Player currentPlayer;
    int rounds;
    int currentRound;

    public Game(String name, String[] fileNames, Player host, int rounds) {
        this.name = name;
        this.board = new Board(fileNames);
        this.bag = new Bag();
        this.players = new Player[MAX_PLAYERS];
        this.players[0] = host;
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
            if (players[i] != null) {
                tile = this.bag.getRand();
                letter = tile.letter;
                playersInt[i] = letterToInt(letter);
                this.bag.put(tile);
            }
        }

        players = orderPlayersByArray(players, playersInt);
        currentPlayer = players[0];
        sendCurrentPlayer();

    }

    public void sendCurrentPlayer(){
        for (Player p : players) {
            if (p != null) {
                p.sendCurrentPlayer(currentPlayer.name);
            }
        }
    }

    public static Player[] orderPlayersByArray(Player[] players, int[] values) {
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
        currentPlayer = players[0];
        sendCurrentPlayer();
        currentRound = 1;
        for (Player player : players) {
            if (player != null) {
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
        players[numOfPlayers] = player;
        numOfPlayers++;
        sendGameToPlayer(player);
        sendPlayersToPlayers(player);
    }

    public void playTurn(Player player, Word word) {
        if (player != currentPlayer) {
            player.sendToPlayer.accept("Player " + player.name + " is not the current player");
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
        currentPlayer.addScore(wordScore);
        int wordTilesCount = word.getTiles().length;
        currentPlayer.replaceTiles(word.getTiles(), bag.getRandomTiles(wordTilesCount));
        this.advanceCurrentPlayer();
        currentRound += 1;
        this.sendGameToPlayers();
    }

    private void sendPlayersToPlayers(Player Except) {
        for (Player player : players) {
            if (player != null) {
                if (player != Except)
                    player.sendPlayers(players);
            }
        }
    }

    private void sendGameToPlayers() {
        for (Player player : players) {
            if (player != null) {
                player.sendBoard(board);
                player.sendBag(bag);
                player.sendPlayers(players);
                player.sendCurrentPlayer(currentPlayer.name);
                player.sendCurrentRound(currentRound);
                player.sendPlayerTiles();
            }
        }
    }

    private void sendGameToPlayer(Player p) {
        p.sendBoard(board);
        p.sendBag(bag);
        p.sendPlayers(players);
        p.sendCurrentRound(currentRound);
    }

    private void advanceCurrentPlayer() {
        throw new UnsupportedOperationException();
    }
}
