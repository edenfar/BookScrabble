package model;

import javafx.beans.property.StringProperty;
import server.Board;
import server.Game;
import server.Player;
import server.Tile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.Objects;
import java.util.Observable;
import java.util.Scanner;

public class Model extends Observable {
    private String gameName;
    private Socket server;
    private PrintWriter outToServer;
    private Scanner inFromServer;
    private Thread serverListener;
    private Board board;
    private Tile.Bag bag;
    private int round;
    private String[] playersArray;
    private String currPlayerName;

    public void connect(String host, int port) {
        try {
            server = new Socket(host, port);
            outToServer = new PrintWriter(server.getOutputStream());
            inFromServer = new Scanner(server.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverListener = new Thread(this::listen);
        serverListener.start();
    }

    protected void sendMessage(String message) {
        outToServer.println(message);
        outToServer.flush();
    }

    public void listen() {
        String response = inFromServer.nextLine();
        while (!Objects.equals(response, "end")) {
            if (response.startsWith("Board:")) {
                String boardString = response.substring("Board:".length());
                try {
                    this.board = (Board) deSerialize(boardString);

                    System.out.println("Board received and deserialized");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (response.startsWith("Bag:")) {
                String bagString = response.substring("Bag:".length());
                try {

                    this.bag = (Tile.Bag) deSerialize(bagString);

                    System.out.println("Bag received and deserialized");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (response.startsWith("Round:")) {
                String roundString = response.substring("Round:".length());
                this.round = Integer.parseInt(roundString);
            }

            if (response.startsWith("Players:")) {
                String temp = response.substring("Players:".length());
                this.playersArray = temp.split(","); //Will need to be change depending on how we use this data in the game
            }

            if (response.startsWith("CurrPlayer:")) {
                this.currPlayerName = response.substring("CurrPlayer:".length());
            }

            if (response.startsWith("GameName:")) {
                this.gameName = response.substring("GameName:".length());
            }

            while (this.hasChanged());
            this.setChanged();
            this.notifyObservers(response.split(":")[0]);
            response = inFromServer.nextLine();
        }
        inFromServer.close();
        outToServer.close();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Object deSerialize(String response) throws IOException, ClassNotFoundException {
        // Convert the received board string back to a byte array
        byte[] respToBytes = Base64.getDecoder().decode(response);

        // Create an ObjectInputStream to read the byte array and deserialize the board object
        ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(respToBytes));
        return objectIn.readObject();
    }

    public void playTurn(String word, int row, int col, boolean vertical) {
        String concatenatedString = word + "," + row + "," + col + "," + vertical;

        this.sendMessage(concatenatedString);

    }

    public Board getBoard() {
        return board;
    }

    public Tile.Bag getBag() {
        return bag;
    }

    public int getRound() {
        return round;
    }

    public String[] getPlayersArray() {
        return playersArray;
    }

    public String getCurrPlayerName() {
        return currPlayerName;
    }

    public String getGameName() {
        return gameName;
    }
}
