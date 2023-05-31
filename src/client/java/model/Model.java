package model;

import server.Board;
import server.Tile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Observable;
import java.util.Scanner;

public class Model extends Observable {
    private String gameName;
    private String playerName;
    // Feel free to add fields and classes for the game data
    private Socket server;
    private PrintWriter outToServer;
    private Scanner inFromServer;
    private Thread serverListener;

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

    private void sendMessage(String message) {
        outToServer.println(message);
        outToServer.flush();
    }

    public void listen() {
        String response = inFromServer.next();
        while (!Objects.equals(response, "end")) {

            // Handle the response - when it is the game data (board, bag, players) we need to de-serialize it

            if (response.startsWith("Board:")) {
                String boardString = response.substring("Board:".length());
                try {
                    // Convert the received board string back to a byte array
                    byte[] boardBytes = boardString.getBytes();

                    // Create an ObjectInputStream to read the byte array and deserialize the board object
                    ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(boardBytes));
                    Board receivedBoard = (Board) objectIn.readObject();

                    System.out.println("Board received and deserialized");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (response.startsWith("Bag:")) {
                String bagString = response.substring("Bag:".length());
                try {
                    // Convert the received board string back to a byte array
                    byte[] bagBytes = bagString.getBytes();

                    // Create an ObjectInputStream to read the byte array and deserialize the board object
                    ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bagBytes));
                    Tile.Bag receivedBag = (Tile.Bag) objectIn.readObject();

                    System.out.println("Bag received and deserialized");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (response.startsWith("Round:")) {
                String roundString = response.substring("Round:".length());
                int round = Integer.parseInt(roundString);
            }

            if (response.startsWith("Players:")) {
                String temp = response.substring("Players:".length());
                String[] playersArray = temp.split(", "); //Will need to be change depending on how we use this data in the game
            }

            if (response.startsWith("CurrPlayer:")) {
                String CurrPlayerName = response.substring("CurrPlayer:".length());
            }

            if (response.startsWith("GameName:")) {
                String GameName = response.substring("GameName:".length());
            }

            this.setChanged();
            this.notifyObservers();
            response = inFromServer.next();
        }
        inFromServer.close();
        outToServer.close();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playTurn(String word, int row, int col, boolean vertical) {
        throw new UnsupportedOperationException();
    }
}
