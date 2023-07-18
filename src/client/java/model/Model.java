package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Observable;
import java.util.Scanner;

public class Model extends Observable {
    private String gameName;
    private Socket server;
    private PrintWriter outToServer;
    private Scanner inFromServer;
    private Thread serverListener;
    private String playerTiles;
    private String playerTilesLetters;

    private String[][] boardData;
    private int round = 1;
    private String[] playersArray;
    private String currPlayerName;
    private String playerScore;

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
                this.boardData = stringToArray(boardString);
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
            if (response.startsWith("PlayerTiles:")) {
                String temp = response.substring("PlayerTiles:".length());
                String[] tempArray = temp.split(":");
                this.playerTiles = tempArray[0];
                this.playerTilesLetters = tempArray[1];
            }
            if (response.startsWith("PlayerScore:")) {
                this.playerScore = response.substring("PlayerScore:".length());
            }


            while (this.hasChanged()) ;
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

    public void playTurn(String word, int row, int col, boolean vertical) {
        String concatenatedString = word + "," + row + "," + col + "," + vertical;
        this.sendMessage(concatenatedString);
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

    public String getPlayerTiles() {
        return playerTiles;
    }

    public String getPlayerTilesLetters() {
        return playerTilesLetters;
    }

    public String getPlayerScore() {
        return playerScore;
    }

    public String[][] getBoardData() {
        return boardData;
    }

    public static String[][] stringToArray(String arrayAsString) {
        String[] rows = arrayAsString.split("\\|");
        String[][] array = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            array[i] = rows[i].split(",");
        }
        return array;
    }
}
