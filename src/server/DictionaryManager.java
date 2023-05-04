package server;

import java.util.HashMap;
import java.util.Map;

public class DictionaryManager {

    public Map<String, Dictionary> dictionaryMap;
    public static DictionaryManager manager;

    private DictionaryManager() {
        this.dictionaryMap = new HashMap<>();
    }

    public static DictionaryManager get() {
        if (manager == null)
            manager = new DictionaryManager();
        return manager;
    }

    public boolean query(String... args) {
        boolean ret = false;
        String word = args[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            String fn = args[i];
            if (!this.dictionaryMap.containsKey(fn)) {
                Dictionary dic = new Dictionary(fn);
                this.dictionaryMap.put(fn, dic);
            }
        }
        for (Dictionary value : dictionaryMap.values()) {
            if (value.query(word)) {
                ret = true;
            }
            value.close();
        }
        return ret;
    }

    public boolean challenge(String... args) {
        boolean ret = false;
        String word = args[args.length - 1]; // Save the last arg in a variable
        for (int i = 0; i < args.length - 1; i++) { // Go over all the args except the last one
            String fn = args[i];
            if (!this.dictionaryMap.containsKey(fn)) {
                Dictionary dictionary = new Dictionary(fn);
                this.dictionaryMap.put(fn, dictionary);
            }
            for (Dictionary dict : dictionaryMap.values()) {
                if (dict.challenge(word)) {
                    ret = true;
                }
            }
        }
        for (Dictionary dic : dictionaryMap.values()) {
            if (dic.challenge(word)) {
                ret = true;
            }
            dic.close();
        }
        return ret;
    }


    public int getSize() {
        return this.dictionaryMap.size();
    }
}
