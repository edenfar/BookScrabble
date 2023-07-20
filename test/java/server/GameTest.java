package server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GameTest {

    @Test
    public void testOrderPlayersByArray() {
        Player player1 = new Player("1", null);
        Player player2 = new Player("2", null);
        Player player3 = new Player("3", null);
        Player player4 = new Player("4", null);
        List<Player> players = List.of(new Player[]{player1, player2, player3, player4});
        List<Player> expectedOrderedPlayers = List.of(new Player[]{player2, player1, player4, player3});

        int[] values = {3, 2, 6, 5};
        List<Player> orderedPlayers = Game.orderPlayersByArray(players, values);
        Assertions.assertEquals(orderedPlayers, expectedOrderedPlayers);
    }
}
