package server;

import java.util.Base64;
import java.util.function.Consumer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

    public void setTiles(Tile[] tiles) {
        this.tiles = tiles;
    }

    public boolean hasTiles(Tile[] tiles) {
        return new HashSet<>(Arrays.asList(this.tiles)).containsAll(Arrays.asList(tiles));
    }
    public Tile getTile(char letter) {
        for (Tile tile : tiles) {
            if (tile.letter == letter) {
                return tile;
            }
        }
        return null;
    }

    public void replaceTiles(Tile[] currentTiles, Tile[] newTiles) {
        if (currentTiles.length != newTiles.length) {
            System.out.println("The number of current tiles and new tiles must be the same.");
            throw new IllegalArgumentException("The number of current tiles and new tiles must be the same.");
        }

        if (!hasTiles(currentTiles)) {
            System.out.println("You don't have the tiles you try to replace.");
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
        System.out.println("Illegal word notify");
        String[] words = word.getWordAsString().split(",");
        String message = String.format("Illegal: Illegal word: %s", words[0]);
        sendToPlayer.accept(message);
    }

    public void notifyIllegalBoard() {
        System.out.println("Illegal board");
        String message = String.format("Illegal: Illegal Board disposition");
        sendToPlayer.accept(message);
    }

    public void notifyIllegalGame() {
        System.out.println("Illegal game");
        String message = String.format("Illegal:No Such Game");
        sendToPlayer.accept(message);
    }

    public void sendBoard(Board board) {
        String boardString = boardToString(board.getBoardsLetters());
        boardString = "Board:" + boardString;
        sendToPlayer.accept(boardString);
    }

    public void sendPlayerTiles() {
        StringBuilder playerTilesString = new StringBuilder("PlayerTiles:");
        for (Tile tile : this.tiles) {
            playerTilesString.append(tile.letterToString());
        }
        sendToPlayer.accept(playerTilesString.toString());
    }

    public static String boardToString(String[][] array) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : array) {
            sb.append(String.join(",", row)).append("|");
        }
        return sb.toString();
    }

    public void sendPlayers(Player[] players) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Players:");
        for (Player player : players) {
            if (player != null) {
                stringBuilder.append(player.getName()).append(":").append(player.getScore());
                stringBuilder.append(",");
            }
        }

        // Get the final string representation
        String playerDataString = stringBuilder.toString();
        sendToPlayer.accept(playerDataString);
    }

    public void sendCurrentPlayer(String name) {
        String message = "CurrPlayer:" + name;
        sendToPlayer.accept(message);
    }

    public void sendCurrentRound(int round) {
        String message = "Round:" + round;
        sendToPlayer.accept(message);
    }

    public void sendGameName(String name) {
        String message = "GameName:" + name;
        sendToPlayer.accept(message);
    }

    public void sendScore() {
        String message = "PlayerScore:" + this.score;
        sendToPlayer.accept(message);
    }

    public void sendNewTurn() {
        String message = "NewTurn:";
        sendToPlayer.accept(message);

    }

    public void sendGameStart() {
        String message = "GameStarted:";
        sendToPlayer.accept(message);
    }

    public void sendGameEnd(String name, String score) {
        String message = "GameEnded:";
        sendToPlayer.accept(message + name + "," + score);
    }

    public void sendScoreBoard(String[] playersScores) {
        String message = "ScoreBoard:";
        String joinedString = String.join(",", playersScores);
        sendToPlayer.accept(message + joinedString);
    }

    public void sendMaxRounds(int maxRounds) {
        String message = "MaxRounds:" + maxRounds;
        sendToPlayer.accept(message);
    }
}

