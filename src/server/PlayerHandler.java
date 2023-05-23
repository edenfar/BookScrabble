package server;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PlayerHandler implements ClientHandler {
    PrintWriter out;
    Scanner in;
    Player player;
    Game game;

    /*
    All the communication with the player should happen here, and not in Player class
    We assume that the player has a thread listening to messages from the server (i.e., this class)
     */

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        out = new PrintWriter(outToClient);
        in = new Scanner(inFromClient);
        Object request = parseRequest(in.nextLine());
        if (request instanceof HostRequest hostRequest) {
            this.createGameByRequest(hostRequest);
            player.sendGameName(game.name);
            this.receiveStartGameSignal();
            game.setup();
        } else if (request instanceof GuestRequest guestRequest) {
            this.connectToGameByRequest(guestRequest);
        }
        while (!game.isOver()) {
            String move = in.nextLine();
            Word word = this.parseMove(move);
            game.playTurn(player, word);
        }
        out.flush();
    }


    //Test of the server
/*
    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        PrintWriter out=new PrintWriter(outToClient);
        Scanner in=new Scanner(inFromclient);
        try {
            Thread.sleep(5000);
            System.out.println(in.nextLine());
            String text = "Hello from Server !";
            Thread.sleep(2000);
            out.println(text);
            out.flush();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }        finally {
            out.close();
            in.close();
        }
    }

 */



    //Test of the server
    public static void client1(int port) {
        new Thread(() -> {
            try {
                Socket server = new Socket("localhost", port);
                String text = "Hello from client !";
                PrintWriter outToServer = new PrintWriter(server.getOutputStream());
                Scanner in = new Scanner(server.getInputStream());

                outToServer.println(text);
                outToServer.flush();
                System.out.println(in.nextLine());
                if (in.hasNext()) {
                    String response = in.next();
                    if (response == null)
                        System.out.println("problem getting the right response from your server, cannot continue the test (-25)");
                    in.close();
                    outToServer.close();}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }




    public record HostRequest(String name, String[] fileNames) {
    }

    public record GuestRequest(String name, String gameName) {
    }

    private Word parseMove(String move) {
        throw new UnsupportedOperationException();
    }

    private void receiveStartGameSignal() {
        String request = in.nextLine();
        // assert it is a start request
        throw new UnsupportedOperationException();
    }

    private static Object parseRequest(String request) {
        /* Request structure:
        host,<player name>,<file names separated by commas>
        guest,<player name>,<game name>
         */
        throw new UnsupportedOperationException();
    }

    public void createGameByRequest(HostRequest request) {
        GamesManager gamesManager = GamesManager.get();
        player = new Player(request.name, out::println);
        game = gamesManager.createGame(request.fileNames, player);
    }

    public void connectToGameByRequest(GuestRequest request) {
        GamesManager gamesManager = GamesManager.get();
        player = new Player(request.name, out::println);
        game = gamesManager.getGame(request.gameName);
        game.addPlayer(player);
    }

    @Override
    public void close() {

    }
}

