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
        }

        System.out.println("Stopping the main program...");
        server.close();
        scanner.close();
    }
}
