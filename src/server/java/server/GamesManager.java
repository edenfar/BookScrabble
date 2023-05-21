package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GamesManager {

    public Map<String, Game> games;
    private static GamesManager manager;
    public static final int ROUNDS = 30;

    public GamesManager() {
        this.games = new HashMap<>();
    }

    public static GamesManager get() {
        if (manager == null)
            manager = new GamesManager();
        return manager;
    }

    public Game createGame(String[] fileNames, Player host) {
        String name = this.getRandomName();
        Game game = new Game(name, fileNames, host, ROUNDS);
        games.put(name, new Game(name, fileNames, host, ROUNDS));
        return game;
    }

    public Game getGame(String gameName) {
        return games.get(gameName);
    }

    private String getRandomName() {
        if (games.isEmpty()) {
            throw new IllegalStateException("No games available.");
        }

        Random random = new Random();
        int index = random.nextInt(games.size());
        return (String) games.keySet().toArray()[index];
    }
}
