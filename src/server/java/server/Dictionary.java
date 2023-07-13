package server;

import java.io.File;
import java.util.Scanner;

public class Dictionary {

    CacheManager exists, notExists;
    BloomFilter bf;
    String[] fileNames;
    ParIOSearcher searcher;

    public Dictionary(String... fileNames) {
        this.fileNames = fileNames;
        exists = new CacheManager(400, new LRU());
        notExists = new CacheManager(100, new LFU());
        bf = new BloomFilter(256, "MD5", "SHA1");

        for (String fn : fileNames) {
            try {
                Scanner s = new Scanner(new File(fn));
                while (s.hasNext())
                    bf.add(s.next());
                s.close();
            } catch (Exception e) {
            }
        }
        searcher = new ParIOSearcher();
    }

    //Edited version, working but somehow say everything is legal
//    public boolean query(String word) {
//        String capFirst = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
//        if (exists.query(word.toLowerCase()) || (exists.query(capFirst)) || (exists.query(word)))
//            return true;
//        if (notExists.query(word.toLowerCase()) || notExists.query(capFirst) || notExists.query(word))
//            return false;
//
//        boolean bool1 = bf.contains(word.toLowerCase());
//        boolean bool2 = bf.contains(capFirst);
//        boolean bool3 = bf.contains(word);
//        boolean doesExist = bool1 || bool2 || bool3;
//        if (doesExist) {
//            exists.add(word.toLowerCase());
//            exists.add(capFirst);
//            exists.add(word);
//        }
//        else {
//            notExists.add(word.toLowerCase());
//            notExists.add(capFirst);
//            notExists.add(word);
//        }
//        return doesExist;
//    }

    //Original
    public boolean query(String word) {
        if (exists.query(word))
            return true;
        if (notExists.query(word))
            return false;

        boolean doesExist = bf.contains(word);
        if (doesExist) {
            exists.add(word);
        }
        else {
            notExists.add(word);
        }
        return doesExist;
    }

    public boolean challenge(String word) {
        boolean doesExist = searcher.search(word, fileNames);
        if (doesExist)
            exists.add(word);
        else
            notExists.add(word);

        return doesExist;

    }

    public void close() {
        searcher.stop();
    }

}
