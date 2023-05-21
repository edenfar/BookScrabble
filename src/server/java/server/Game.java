package server;

import server.Tile.Bag;
import java.util.Arrays;

public class Game {

    final static int MAX_PLAYERS = 4;

    String name;
    Board board;
    Bag bag;
    Player[] players;
    int NumOfPlayers;
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
        this.NumOfPlayers = 1;
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
        int [] playersInt = new int [MAX_PLAYERS];
        Integer[] indices = new Integer[playersInt.length];
        Player [] NewPlayersList = new Player[MAX_PLAYERS];

        Tile tile;
        char letter;

        for (int i = 0; i < playersInt.length; i++) {
            playersInt[i] = 0;
        }

        if (NumOfPlayers != 1) {
            for (int i = 0; i < players.length; i++) {
                if (players[i] == null) {
                    playersInt[i] = 0;
                }
                else {
                    tile = this.bag.getRand();
                    letter = tile.letter;
                    playersInt[i] = letterToInt(letter);
                    this.bag.put(tile);
                }

            }

            // Create an array of indices
            for (int i = 0; i < indices.length; i++) {
                indices[i] = i;
            }

            // Sort the indices based on the values in playersInt array in descending order
            Arrays.sort(indices, (a, b) -> Integer.compare(playersInt[b], playersInt[a]));

            // Populate the Order array with the sorted indices
            for (int i = 0; i < indices.length; i++) {
                NewPlayersList[i] = this.players[indices[i]];
            }
            this.players = NewPlayersList;
        }
    }

    public void setup() {
        this.orderPlayers();
        currentRound = 1;
        currentPlayer = players[0];
    }

    public boolean isOver() {
        return currentRound == rounds;
    }

    public void addPlayer(Player player) {
        if (players == null) {
            players = new Player[MAX_PLAYERS];
        }

        if (NumOfPlayers >= MAX_PLAYERS) {
            throw new UnsupportedOperationException("Maximum number of players reached.");
        }
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                break;
            }
        }
        NumOfPlayers++;

        // if (NumOfPlayers == MAX_PLAYERS) {
        //     startGame();
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

    private void sendGameToPlayers() {
        for (Player player : players) {
            player.sendBoard(board);
            player.sendBag(bag);
            player.sendPlayers(players);
            player.sendCurrentPlayer(currentPlayer.name);
            player.sendCurrentRound(currentRound);
        }
    }

    private void advanceCurrentPlayer() {
        throw new UnsupportedOperationException();
    }
}
