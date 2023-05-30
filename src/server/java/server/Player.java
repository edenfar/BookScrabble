package server;

import java.util.function.Consumer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import server.Tile.Bag;

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

        String message = "You don't have the required tiles for the word \"" + word + "\"";
        sendToPlayer.accept(message);
    }

    public void notifyIllegalWord(Word word) {
        String message = "The word \"" + word + "\" can't fit";
        sendToPlayer.accept(message);
    }

    public void sendBoard(Board board) {
        try {
            // Serialize the Board object to a byte array
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(board);
            objectOut.flush();

            // Convert the byte array to a string and send it using the sendToPlayer consumer
            String boardString = byteOut.toString();
            sendToPlayer.accept(boardString);

            System.out.println("Board sent to the player");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBag(Bag bag) {
        try {
            // Serialize the Board object to a byte array
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(bag);
            objectOut.flush();

            // Convert the byte array to a string and send it using the sendToPlayer consumer
            String boardString = byteOut.toString();
            sendToPlayer.accept(boardString);

            System.out.println("Bag sent to the player");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
