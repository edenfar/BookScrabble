package server;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static server.PlayerHandler_test.client1;

public class ServerTest {

    public static final int PORT = 6123;

    @Test
    public void testServer(){
        Scanner scanner = new Scanner(System.in);
        String input;

        MyServer server = new MyServer(PORT, new PlayerHandler_test(), 3);
        server.start();

        //Test of the server

        // Create and start clients
        client1(PORT);
        client1(PORT);
        client1(PORT);

        client1(PORT);
        client1(PORT);
        client1(PORT);

        // Wait for some time to allow client to finish
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //server.stop();
        server.close();
        scanner.close();




    }



}
