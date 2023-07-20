package server;

import java.util.*;
import java.util.function.Consumer;

public class Player {
    private int id;
    private String name;
    private int score;
    private Tile[] tiles;
    Consumer<String> sendToPlayer;

    public Player() {
        this.score = 0;
        this.tiles = new Tile[]{};
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player player)) return false;
        return player.id == this.id
                && Objects.equals(player.name, this.name)
                && player.score == this.score
                && Arrays.equals(player.tiles, this.tiles);
    }

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

    public void addScore(int additionalScore) {
        if (additionalScore > 0) {
            this.score += additionalScore;
        } else throw new IllegalArgumentException(additionalScore + " is not a positive number");
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public void setTiles(Tile[] tiles) {
        if (this.tiles.length > 0)
            throw new RuntimeException("Tiles are already set");
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
        String[] words = word.getWordAsString().split(",");
        String message = String.format("Illegal: Illegal word: %s", words[0]);
        sendToPlayer.accept(message);
    }

    public void notifyIllegalBoard() {
        String message = String.format("Illegal: Illegal Board disposition");
        sendToPlayer.accept(message);
    }

    public void notifyIllegalGame() {
        String message = String.format("Illegal:No Such Game");
        sendToPlayer.accept(message);
    }

    public void sendBoard(Board board) {
        sendToPlayer.accept("Board:" + board.toString());
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

    public void sendPlayers(List<Player> players) {
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

    public void sendRounds(int rounds) {
        String message = "Rounds:" + rounds;
        sendToPlayer.accept(message);
    }
}

