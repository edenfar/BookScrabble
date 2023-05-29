package server.java.server;

public interface CacheReplacementPolicy {
    void add(String word);

    String remove();
}
