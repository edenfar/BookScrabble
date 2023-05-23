package server;

import java.util.Scanner;

//import static server.PlayerHandler.client1;

public class Main {

    public static final int PORT = 6123;

    public static void main(String[] args) {
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
/*
        // Create and start clients
        client1(PORT);
        client1(PORT);
        client1(PORT);

        client1(PORT);

        client1(PORT);
        client1(PORT);
        client1(PORT);
 */

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
