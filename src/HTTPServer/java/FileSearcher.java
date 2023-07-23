package HTTPServer;

interface FileSearcher {
    boolean search(String word, String... fileNames);

    void stop();
}

