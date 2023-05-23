package server;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.Tile.Bag;
import static server.PlayerHandler.client1;

import java.util.Scanner;

public class BoardTest {

    public static final int PORT = 6123;
    final String DICTIONARY_1 = "test/resources/dictionary_1.txt";
    final String DICTIONARY_2 = "test/resources/dictionary_2.txt";


    public Tile[] stringToTiles(String string) {
        Bag bag = new Bag();
        return string.chars().mapToObj(c->(char)c).map(bag::getTile).toArray(Tile[]::new);
    }

    @Test
    public void testDictionaryLegalWordLegal() {
        Board board = new Board(new String[]{DICTIONARY_1});
        Tile[] tiles = stringToTiles("HELLO");
        Word word = new Word(tiles, 0, 0, false);
        Assertions.assertTrue(board.dictionaryLegal(word));
    }

    @Test
    public void testDictionaryLegalWordIllegal() {
        Board board = new Board(new String[]{DICTIONARY_1});
        Tile[] tiles = stringToTiles("ME");
        Word word = new Word(tiles, 0, 0, false);
        Assertions.assertFalse(board.dictionaryLegal(word));
    }

    @Test
    public void testDictionaryLegalTwoDictionaries() {
        Board board = new Board(new String[]{DICTIONARY_1, DICTIONARY_2});
        Tile[] tiles = stringToTiles("FIRST");
        Word word = new Word(tiles, 0, 0, false);
        Assertions.assertTrue(board.dictionaryLegal(word));
    }

    @Test
    public void testServer(){
        Scanner scanner = new Scanner(System.in);
        String input;

        MyServer server = new MyServer(PORT, new PlayerHandler(), 3);
        server.start();

        // Wait for some time to allow server to finish
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Test of the server

        // Create and start clients
        client1(PORT);
        client1(PORT);
        client1(PORT);

        client1(PORT);

        client1(PORT);
        client1(PORT);
        client1(PORT);


        while (true) {
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("stop")) {
                System.out.println("Stopping the main program...");

                // Perform any necessary cleanup or shutdown tasks
                server.close();

                // Close resources and exit the program
                scanner.close();
                System.exit(0);
            }
        }



    }

}
