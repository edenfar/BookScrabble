package server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GamesManager {

    private Map<String, Game> games;
    private static GamesManager manager;
    public static final int ROUNDS = 30;
    private Session session;

    private GamesManager() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        this.session = sessionFactory.openSession();
        this.games = new HashMap<>();
    }

    public static GamesManager get() {
        if (manager == null)
            manager = new GamesManager();
        return manager;
    }

    public void saveGame(String name) {
        Game game = this.getGame(name);
        Transaction transaction = session.beginTransaction();
        session.save(game);
        transaction.commit();
    }

    public Game loadGame(String name) {
        Query query = session.createQuery("from Game where name = :name");
        query.setParameter("name", name);
        List<Game> results = query.getResultList();
        if (results.size() > 1)
            throw new RuntimeException("More than one game found for name " + name);
        if (results.size() == 0)
            throw new RuntimeException("No game found for name " + name);
        return results.get(0);
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
