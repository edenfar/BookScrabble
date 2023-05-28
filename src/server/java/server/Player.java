package server;

import java.util.function.Consumer;

import server.Tile.Bag;

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

    public void addScore(int additionalScore) {
        if (additionalScore > 0) {
            this.score += additionalScore;
        } else throw new IllegalArgumentException(additionalScore + " is not a positive number");
    }

    public boolean hasTiles(Tile[] tiles) {
        throw new UnsupportedOperationException();
    }

    public void replaceTiles(Tile[] currentTiles, Tile[] newTiles) {
        throw new UnsupportedOperationException();
    }

    public void notifyMissingTilesForWord(Word word) {
        throw new UnsupportedOperationException();
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
