package server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DBTest {

    public Game createGameByPlayerName(String playerName, GamesManager gamesManager) {
        Player player = new Player(playerName, null);
        String[] fileNames = {"a", "c"};
        Game game = gamesManager.createGame(fileNames, player);
        Tile[] tiles = {game.bag.getRand()};
        player.setTiles(tiles);
        return game;
    }

    @Test
    public void testSaveGame() {
        GamesManager gamesManager = GamesManager.get();
        Game game = this.createGameByPlayerName("my-name", gamesManager);

        gamesManager.saveGame(game.name);
        gamesManager.close();

        gamesManager = GamesManager.get();
        Game retrieved = gamesManager.loadGame(game.name);
        Assertions.assertEquals(game, retrieved);
    }

    @Test
    public void testSaveTwoGames() {
        GamesManager gamesManager = GamesManager.get();
        Game first = this.createGameByPlayerName("my-name", gamesManager);
        Game second = this.createGameByPlayerName("my-name-2", gamesManager);

        gamesManager.saveGame(first.name);
        gamesManager.saveGame(second.name);
        gamesManager.close();

        gamesManager = GamesManager.get();
        Game retrievedFirst = gamesManager.loadGame(first.name);
        Game retrievedSecond = gamesManager.loadGame(second.name);
        Assertions.assertEquals(first, retrievedFirst);
        Assertions.assertEquals(second, retrievedSecond);
    }
}
