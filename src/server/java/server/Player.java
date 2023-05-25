package server;

import java.util.function.Consumer;

import server.Tile.Bag;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Player {
    String name;
    private int score;
    private Tile[] tiles;
    Consumer<String> sendToPlayer;

    public Player(String name, Consumer<String> sendToPlayer) {
        this.name = name;
        this.score = 0;
        this.tiles = new Tile[]{};
        this.sendToPlayer = sendToPlayer;
    }
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int additionalScore) {
        if (additionalScore > 0) {
            this.score += additionalScore;
        } else throw new IllegalArgumentException(additionalScore + " is not a positive number");
    }

    public boolean hasTiles(Tile[] tiles) {
        return Arrays.asList(this.tiles).containsAll(Arrays.asList(tiles));
    }

    public void replaceTiles(Tile[] currentTiles, Tile[] newTiles) {
        if (currentTiles.length != newTiles.length) {
            throw new IllegalArgumentException("The number of current tiles and new tiles must be the same.");
        }

        if (!hasTiles(currentTiles)) {
            throw new IllegalArgumentException("You don't have the tiles you try to replace.");
        }

        for (int i = 0; i < currentTiles.length; i++) {
            int index = Arrays.asList(this.tiles).indexOf(currentTiles[i]);
            this.tiles[index] = newTiles[i];
        }
    }

    public void notifyMissingTilesForWord(Word word) {
        Set<Tile> wordTiles = new HashSet<>(Arrays.asList(word.getTiles()));
        Set<Tile> playerTiles = new HashSet<>(Arrays.asList(tiles));
        Set<Tile> missingTiles = new HashSet<>(wordTiles);

        missingTiles.removeAll(playerTiles);

        if (!missingTiles.isEmpty()) {
            String message = String.format("Missing tiles for word: %s", missingTiles);
            sendToPlayer.accept(message);
        }
    }

    public void notifyIllegalWord(Word word) {
        throw new UnsupportedOperationException();
    }

    public void sendBoard(Board board) {
        throw new UnsupportedOperationException();
    }

    public void sendBag(Bag bag) {
        throw new UnsupportedOperationException();
    }

    public void sendPlayers(Player[] players) {
        throw new UnsupportedOperationException();
    }

    public void sendCurrentPlayer(String name) {
        throw new UnsupportedOperationException();
    }

    public void sendCurrentRound(int round) {
        throw new UnsupportedOperationException();
    }

    public void sendGameName(String name) {
        throw new UnsupportedOperationException();
    }
}
