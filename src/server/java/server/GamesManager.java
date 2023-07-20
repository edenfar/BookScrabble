package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GamesManager {

    private Map<String, Game> games;
    private static GamesManager manager;
    public static final int ROUNDS = 5;

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
        String prefix = "src/server/java/resources/";

        String[] fileWithPath = new String[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            fileWithPath[i] = prefix + fileNames[i];
        }
        Game game = new Game(name, fileWithPath, host, ROUNDS);
        games.put(name, game);
        return game;
    }

    public Game getGame(String gameName) {
        Game game = games.get(gameName);
        if (game == null) {
            System.out.println("Game is not found");
            return null;
        }
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
