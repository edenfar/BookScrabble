package server;

public class Main {

    public static final int PORT = 6123;

    public static void main(String[] args) {
        MyServer server = new MyServer(PORT, new PlayerHandler(), 100);
        server.start();
    }
}
