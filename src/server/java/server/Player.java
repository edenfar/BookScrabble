package server;

import java.io.Serializable;
import java.util.function.Consumer;

import server.Tile.Bag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Player implements Serializable {
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
        return new HashSet<>(Arrays.asList(this.tiles)).containsAll(Arrays.asList(tiles));
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
        try {
            //Serialize and Convert the byte array to a string and send it using the sendToPlayer consumer
            String boardString = seriallizeObject(board).toString();
            sendToPlayer.accept(boardString);

            System.out.println("Board sent to the player");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBag(Bag bag) {
        try {
            // Serialize and Convert the object to a string and send it using the sendToPlayer consumer
            String boardString = seriallizeObject(bag).toString();
            sendToPlayer.accept(boardString);

            System.out.println("Bag sent to the player");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ByteArrayOutputStream seriallizeObject(Object o) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
        objectOut.writeObject(o);
        objectOut.flush();

        return byteOut;
    }

    public void sendPlayers(Player[] players) {
        throw new UnsupportedOperationException();
    }

    public void sendCurrentPlayer(String name) {
        sendToPlayer.accept(name);
    }

    public void sendCurrentRound(int round) {
        sendToPlayer.accept(String.valueOf(round));
    }

    public void sendGameName(String name) {
        sendToPlayer.accept(name);
    }
}
