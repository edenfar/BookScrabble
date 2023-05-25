package server;

import java.util.Scanner;


public class Main {

    public static final int PORT = 6123;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = "";

        MyServer server = new MyServer(PORT, new PlayerHandler(), 3);
        server.start();


        while (!input.equals("stop")) {
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
