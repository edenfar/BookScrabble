package HTTPServer; // Update the package declaration to match your directory structure

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import server.DictionaryManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
public class MyHttpServer {

    public static void main(String[] args) throws IOException {
        // Create an HTTP server on localhost, port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);

        // Set the context and handler for incoming requests
        server.createContext("/query", new QueryHandler());
        server.createContext("/challenge", new ChallengeHandler());

        // Start the server
        server.start();

        System.out.println("Server is listening on port 8000");
    }

    static class QueryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the query parameter "word" from the request URL
            String query = exchange.getRequestURI().getQuery();
            String[] queryParams = query.split("&");
            String word = "";
            for (String param : queryParams) {
                if (param.startsWith("word=")) {
                    word = param.substring(5); // Extract the value of "word" parameter
                    break;
                }
            }

            // Invoke the query method of the DictionaryManager
            boolean result = DictionaryManager.get().query(word);

            // Set the response based on the result
            String response = result ? "Word found in dictionary" : "Word not found in dictionary";

            // Send the response back to the client
            exchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }
    }

    static class ChallengeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the query parameter "word" from the request URL
            String query = exchange.getRequestURI().getQuery();
            String[] queryParams = query.split("&");
            String word = "";
            for (String param : queryParams) {
                if (param.startsWith("word=")) {
                    word = param.substring(5); // Extract the value of "word" parameter
                    break;
                }
            }

            // Invoke the challenge method of the DictionaryManager
            boolean result = DictionaryManager.get().challenge(word);

            // Set the response based on the result
            String response = result ? "Word is a challenge" : "Word is not a challenge";

            // Send the response back to the client
            exchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }
    }
}
