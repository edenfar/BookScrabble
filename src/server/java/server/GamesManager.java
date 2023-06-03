package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GamesManager {

    private Map<String, Game> games;
    private static GamesManager manager;
    public static final int ROUNDS = 30;

    private GamesManager() {
        this.games = new HashMap<>();
    }

    public static GamesManager get() {
        if (manager == null)
            manager = new GamesManager();
        return manager;
    }

    public Game createGame(String[] fileNames, Player host) {
        String name = getNewRandomName();
        Game game = new Game(name, fileNames, host, ROUNDS);
        games.put(name, new Game(name, fileNames, host, ROUNDS));
        return game;
    }

    public Game getGame(String gameName) {
        Game game = games.get(gameName);
        if (game == null)
            throw new RuntimeException("Game is not found: " + gameName);
        return game;
    }

    private static String getNewRandomName() {
        String ALPHA_NUMERICS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        while (salt.length() < 6) {
            int index = (int) (random.nextFloat() * ALPHA_NUMERICS.length());
            salt.append(ALPHA_NUMERICS.charAt(index));
        }
        return salt.toString();
    }
}
