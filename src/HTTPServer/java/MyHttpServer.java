package HTTPServer; // Update the package declaration to match your directory structure

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import HTTPServer.DictionaryManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
            List<String> fileNames = new ArrayList<>();

            for (String param : queryParams) {
                if (param.startsWith("word=")) {
                    word = param.split("word=")[1]; // Extract the value of "word" parameter
                } else if (param.startsWith("files=")) {
                    String filesParam = param.split("files=")[1]; // Extract the value of "files" parameter
                    String[] filesArray = filesParam.split(",");
                    for (String file : filesArray) {
                        fileNames.add(URLDecoder.decode(file, StandardCharsets.UTF_8));
                    }
                }
            }

            System.out.println("Query received. Word: " + word + ", Files: " + fileNames);

            // Merge the word and file names into a single array
            String[] args = new String[fileNames.size() + 1];
            fileNames.toArray(args);
            args[args.length - 1] = word;

            // Invoke the query method of the DictionaryManager
            boolean result = DictionaryManager.get().query(args);

            // Set the response based on the result
            String response = result ? "True" : "False";

            System.out.println("Sending query response: " + response);

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
                    word = param.split("word=")[1]; // Extract the value of "word" parameter
                    break;
                }
            }

            System.out.println("Challenge received. Word: " + word);
            // Invoke the challenge method of the DictionaryManager
            boolean result = DictionaryManager.get().challenge(word);

            // Set the response based on the result
            String response = result ? "True" : "False";

            System.out.println("Sending challenge response: " + response);
            // Send the response back to the client
            exchange.sendResponseHeaders(200, response.length());
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }
    }
}