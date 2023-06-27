package server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DBTest {

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
        Game retrieved = gamesManager.loadGame(game.name);
        Assertions.assertEquals(retrieved.players.get(0).getName(), playerName);

    }
}
