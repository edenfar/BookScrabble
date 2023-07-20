package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Observable;
import java.util.Scanner;

public class Model extends Observable {
    private String gameName;
    private Socket server;
    private PrintWriter outToServer;
    private Scanner inFromServer;
    private Thread serverListener;
    private String playerTilesLetters;
    private String[][] boardData;
    private int round = 1;
    private String[] playersArray;
    private String currPlayerName;
    private String playerScore;
    private String rounds;
    private String[] scoreBoard;
    private String illegal;
    private String winner;
    private boolean end = false;

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
        while (!end) {
            if (response.startsWith("Board:")) {
                String boardString = response.substring("Board:".length());
                this.boardData = stringToArray(boardString);
            } else if (response.startsWith("Round:")) {
                String roundString = response.substring("Round:".length());
                this.round = Integer.parseInt(roundString);
            } else if (response.startsWith("Players:")) {
                String temp = response.substring("Players:".length());
                this.playersArray = temp.split(",");
                this.scoreBoard = new String[this.playersArray.length];
                Arrays.fill(this.scoreBoard, "0");
            } else if (response.startsWith("CurrPlayer:")) {
                this.currPlayerName = response.substring("CurrPlayer:".length());
            } else if (response.startsWith("GameName:")) {
                this.gameName = response.substring("GameName:".length());
            } else if (response.startsWith("PlayerTiles:")) {
                String temp = response.substring("PlayerTiles:".length());
                this.playerTilesLetters = temp;
            } else if (response.startsWith("PlayerScore:")) {
                this.playerScore = response.substring("PlayerScore:".length());
            } else if (response.startsWith("Illegal:")) {
                this.illegal = response.substring("Illegal:".length());
            } else if (response.startsWith("ScoreBoard:")) {
                String temp = response.substring("ScoreBoard:".length());
                this.scoreBoard = temp.split(",");
            } else if (response.startsWith("Rounds:")) {
                String temp = response.substring("Rounds:".length());
                this.rounds = temp;
            } else if (response.startsWith("GameEnded:")) {
                String temp = response.substring("GameEnded:".length());
                this.winner = temp;
                this.end = true;
            } else {
                System.out.println("Unknown : " + response);
            }
            while (this.hasChanged()) ;
            this.setChanged();
            this.notifyObservers(response.split(":")[0]);
            if (!response.startsWith("GameEnded:")) {
                response = inFromServer.nextLine();
            }
        }
        inFromServer.close();
        outToServer.close();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playTurn(String word, int row, int col, boolean vertical, String wordToReplace) {
        String concatenatedString = word + "," + row + "," + col + "," + vertical + "," + wordToReplace;
        this.sendMessage(concatenatedString);

    }

    public int getRound() {
        return round;
    }

    public String getIllegal() {
        return illegal;
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

    public String getPlayerTilesLetters() {
        return playerTilesLetters;
    }

    public String getPlayerScore() {
        return playerScore;
    }

    public String[] getPlayersScoreArray() {
        return scoreBoard;
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


    public String getrounds() {
        return rounds;
    }

    public String getWinner() {
        return winner;
    }
}
