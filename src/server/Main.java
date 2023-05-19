package server;

import static server.PlayerHandler.client1;

public class Main {

    public static final int PORT = 6123;

    public static void main(String[] args) {
        MyServer server = new MyServer(PORT, new PlayerHandler(), 100);
        server.start();
        // Wait for some time to allow server to finish
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Test of the server
        /*
        // Create and start clients
        client1(PORT);
        client1(PORT);
        client1(PORT);
        client1(PORT);
        client1(PORT);


        // Wait for some time to allow clients to finish
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         */
    }
}
