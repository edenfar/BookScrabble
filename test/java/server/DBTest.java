package server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.MutationQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DBTest {

//    @BeforeEach
//    public void deleteAllGames() {
//        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
//        Session session = sessionFactory.openSession();
//        Transaction transaction = session.beginTransaction();
//        String stringQuery = "drop *";
//        MutationQuery query = session.createMutationQuery(stringQuery);
//        query.executeUpdate();
//        transaction.commit();
//        session.close();
//    }

    @Test
    public void testGameSave() {
        Tile.Bag bag = new Tile.Bag();
        Tile[] tiles = {bag.getRand()};
        String playerName = "my-name";
        Player player = new Player(playerName, null);
        player.setTiles(tiles);
        String[] fileNames = {"a", "c"};
        GamesManager gamesManager = GamesManager.get();
        Game game = gamesManager.createGame(fileNames, player);

        gamesManager.saveGame(game.name);
        gamesManager.close();

        gamesManager = GamesManager.get();
        Game retrieved = gamesManager.loadGame(game.name);
        Assertions.assertEquals(retrieved.players.get(0).getName(), playerName);
        Assertions.assertEquals(retrieved.currentPlayer.getName(), playerName);
        Assertions.assertArrayEquals(retrieved.currentPlayer.getTiles(), tiles);
        Assertions.assertArrayEquals(retrieved.board.getTiles()[0], game.board.getTiles()[0]);
        Assertions.assertArrayEquals(retrieved.board.getFileNames(), game.board.fileNames);
    }
}
