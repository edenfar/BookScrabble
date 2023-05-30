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
    boolean Bag, Board, Round = false; //still ongoing

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

            if (Board) {
                try {
                    // Convert the received board string back to a byte array
                    byte[] boardBytes = response.getBytes();

                    // Create an ObjectInputStream to read the byte array and deserialize the board object
                    ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(boardBytes));
                    Board receivedBoard = (Board) objectIn.readObject();

                    System.out.println("Board received and deserialized");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (Bag) {
                try {
                    // Convert the received board string back to a byte array
                    byte[] bagBytes = response.getBytes();

                    // Create an ObjectInputStream to read the byte array and deserialize the board object
                    ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(bagBytes));
                    Tile.Bag receivedBag = (Tile.Bag) objectIn.readObject();

                    System.out.println("Bag received and deserialized");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (Round) {
                int round = Integer.parseInt(response);
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
