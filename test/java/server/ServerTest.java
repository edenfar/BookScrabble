package server;

import org.junit.jupiter.api.Test;

import static server.PlayerHandler_test.client1;

public class ServerTest {

    public static final int PORT = 6123;

    @Test
    public void testServer() {
        MyServer server = new MyServer(PORT, new PlayerHandler_test(), 3);
        server.start();

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

        server.close();
    }
}
