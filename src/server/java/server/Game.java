package server;

import server.Tile.Bag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Game {

    final static int MAX_PLAYERS = 4;

    private int id;
    String name;
    Board board;
    Bag bag;
    List<Player> players;
    int numOfPlayers;
    Player currentPlayer;

    int maxRounds;
    int currentRound;

    public Game() {
        this.players = new ArrayList<>(MAX_PLAYERS);
    }

    public Game(String name, String[] fileNames, Player host, int maxRounds) {
        this.name = name;
        this.board = new Board(fileNames);
        this.bag = new Bag();
        this.players = new ArrayList<>(MAX_PLAYERS);
        this.players.set(0, host);
        this.maxRounds = maxRounds;
        this.players = new ArrayList<>(MAX_PLAYERS);
        this.players.add(host);
        this.numOfPlayers = 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Game game)) return false;
        return game.id == this.id
                && Objects.equals(game.name, this.name)
                && game.maxRounds == this.maxRounds
                && game.numOfPlayers == this.numOfPlayers
                && ((game.currentPlayer == null && this.currentPlayer == null)
                || game.currentPlayer.equals(this.currentPlayer))
                && game.players.equals(this.players)
                && game.bag.equals(this.bag)
                && game.board.equals(this.board);
    }

    public int letterToInt(char letter) {
        //note that here A = 1 and in tails class A = 0
        if (Character.isUpperCase(letter)) {
            return letter - 'A' + 1;
        } else if (Character.isLowerCase(letter)) {
            return letter - 'a' + 1;
        } else {
            System.out.println("Invalid letter: " + letter);
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

        players = orderPlayersByArray(players, playersInt);
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

    public static List<Player> orderPlayersByArray(List<Player> players, int[] values) {
        List<Player> nonNullPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player != null) {
                nonNullPlayers.add(player);
            }
        }
        List<Player> sortedPlayers = new ArrayList<>(nonNullPlayers);
        sortedPlayers.sort(Comparator.comparing(player -> values[nonNullPlayers.indexOf(player)]));

        return sortedPlayers;
    }

    public void setup() {
        this.orderPlayers();
        currentPlayer = players.get(0);
        sendCurrentPlayer();
        currentRound = 1;
        sendCurrentRound();
        for (Player player : players) {
            if (player != null) {
                player.setTiles(bag.getRandomTiles(7));
                player.sendPlayerTiles();
                player.sendGameStart();
            }
        }
    }

    public boolean isOver() {
        return currentRound > maxRounds;
    }

    public void addPlayer(Player player) {
        if (numOfPlayers >= MAX_PLAYERS) {
            throw new UnsupportedOperationException("Maximum number of players reached.");
        }
        players.add(player);
        numOfPlayers++;
        sendMaxRounds(player);
        sendGameToPlayer(player);
        sendPlayersToPlayers(player);
    }

    private void sendMaxRounds(Player player) {
        player.sendMaxRounds(maxRounds);
    }

    public void playTurn(Player player, Word word, Word wordToReplaceW) {
        if (player != currentPlayer) {
            player.sendToPlayer.accept("Player " + player.getName() + " is not the current player");
            return;
        }
        playCurrentTurn(word, wordToReplaceW);
    }

    private void playCurrentTurn(Word word, Word wordToReplaceW) {
        if (!currentPlayer.hasTiles(wordToReplaceW.getTiles())) {
            currentPlayer.notifyMissingTilesForWord(word);
            return;
        }
        int wordScore = board.tryPlaceWord(word);
        if (wordScore == 0) {
            currentPlayer.notifyIllegalWord(word);
            return;
        }
        if (wordScore == -1) {
            currentPlayer.notifyIllegalBoard();
            return;
        }
        this.sendBoard();
        currentPlayer.addScore(wordScore);
        int wordTilesCount = wordToReplaceW.getTiles().length;
        currentPlayer.replaceTiles(wordToReplaceW.getTiles(), bag.getRandomTiles(wordTilesCount));
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
                player.sendScoreBoard(getPlayersScores());
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

    public void endGame() {
        System.out.println("Game ended");
        int winnerIndex = findIndexOfHighestValue(getPlayersScores());
        String winnerName = players.get(winnerIndex).getName();
        String winnerScore = String.valueOf(players.get(winnerIndex).getScore());
        for (Player player : players) {
            if (player != null) {
                player.sendGameEnd(winnerName, winnerScore);
            }
        }
    }

    public int findIndexOfHighestValue(String[] numbers) {
        int highestIndex = 0;
        int highestValue = Integer.parseInt(numbers[0]);

        for (int i = 1; i < numbers.length; i++) {
            int currentValue = Integer.parseInt(numbers[i]);
            if (currentValue > highestValue) {
                highestValue = currentValue;
                highestIndex = i;
            }
        }

        return highestIndex;
    }


    public String[] getPlayersScores() {
        String playersScores[] = new String[numOfPlayers];
        for (Player player : players) {
            if (player != null) {
                playersScores[players.indexOf(player)] = String.valueOf(player.getScore());
            }
        }
        return playersScores;
    }

}
