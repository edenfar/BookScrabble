package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    // indexes
    final byte dl = 2;    // double letter
    final byte tl = 3;    // triple letter
    final byte dw = 20;    // double word
    final byte tw = 30;    // triple word

    private byte[][] bonus = {
            {tw, 0, 0, dl, 0, 0, 0, tw, 0, 0, 0, dl, 0, 0, tw},
            {0, dw, 0, 0, 0, tl, 0, 0, 0, tl, 0, 0, 0, dw, 0},
            {0, 0, dw, 0, 0, 0, dl, 0, dl, 0, 0, 0, dw, 0, 0},
            {dl, 0, 0, dw, 0, 0, 0, dl, 0, 0, 0, dw, 0, 0, dl},
            {0, 0, 0, 0, dw, 0, 0, 0, 0, 0, dw, 0, 0, 0, 0},
            {0, tl, 0, 0, 0, tl, 0, 0, 0, tl, 0, 0, 0, tl, 0},
            {0, 0, dl, 0, 0, 0, dl, 0, dl, 0, 0, 0, dl, 0, 0},
            {tw, 0, 0, dl, 0, 0, 0, dw, 0, 0, 0, dl, 0, 0, tw},
            {0, 0, dl, 0, 0, 0, dl, 0, dl, 0, 0, 0, dl, 0, 0},
            {0, tl, 0, 0, 0, tl, 0, 0, 0, tl, 0, 0, 0, tl, 0},
            {0, 0, 0, 0, dw, 0, 0, 0, 0, 0, dw, 0, 0, 0, 0},
            {dl, 0, 0, dw, 0, 0, 0, dl, 0, 0, 0, dw, 0, 0, dl},
            {0, 0, dw, 0, 0, 0, dl, 0, dl, 0, 0, 0, dw, 0, 0},
            {0, dw, 0, 0, 0, tl, 0, 0, 0, tl, 0, 0, 0, dw, 0},
            {tw, 0, 0, dl, 0, 0, 0, tw, 0, 0, 0, dl, 0, 0, tw}
    };

    Tile[][] tiles;
    boolean isEmpty;
    String[] fileNames;

    public Board(String[] fileNames) {
        tiles = new Tile[15][15];
        isEmpty = true;
        this.fileNames = fileNames;
    }

    public Tile[][] getTiles() {
        return tiles.clone();
    }


    private boolean inBoard(int row, int col) {
        return (col >= 0 && col < 15 && row >= 0 && row < 15);
    }

    private boolean onStar(Word w) {
        int i = w.getRow(), j = w.getCol();
        for (int k = 0; k < w.getTiles().length; k++) {
            if (i == 7 && j == 7)
                return true;
            if (w.isVertical()) i++;
            else j++;
        }
        return false;
    }

    private boolean crossTile(Word w) {
        int i = w.getRow(), j = w.getCol();
        for (int k = 0; k < w.getTiles().length; k++) {

            if (tiles[i][j] != null ||
                    (inBoard(i + 1, j) && tiles[i + 1][j] != null) ||
                    (inBoard(i + 1, j + 1) && tiles[i + 1][j + 1] != null) ||
                    (inBoard(i, j + 1) && tiles[i][j + 1] != null) ||
                    (inBoard(i - 1, j + 1) && tiles[i - 1][j + 1] != null) ||
                    (inBoard(i - 1, j) && tiles[i - 1][j] != null) ||
                    (inBoard(i - 1, j - 1) && tiles[i - 1][j - 1] != null) ||
                    (inBoard(i, j - 1) && tiles[i][j - 1] != null) ||
                    (inBoard(i + 1, j - 1) && tiles[i + 1][j - 1] != null)
            )
                return true;

            if (w.isVertical()) i++;
            else j++;
        }
        return false;
    }

    private boolean changesTile(Word w) {
        int i = w.getRow(), j = w.getCol();
        for (Tile t : w.getTiles()) {
            if (tiles[i][j] != null && tiles[i][j] != t)
                return true;
            if (w.isVertical()) i++;
            else j++;
        }
        return false;
    }


    public boolean boardLegal(Word w) {
        int row = w.getRow();
        int col = w.getCol();

        if (!inBoard(row, col))
            return false;
        int eCol, eRow;
        if (w.isVertical()) {
            eCol = col;
            eRow = row + w.getTiles().length - 1;
        } else {
            eRow = row;
            eCol = col + w.getTiles().length - 1;
        }
        if (!inBoard(eRow, eCol))
            return false;


        if (isEmpty && !onStar(w))
            return false;

        if (!isEmpty && !crossTile(w))
            return false;

        return !changesTile(w);
    }

    public boolean dictionaryLegal(Word w) {
        DictionaryManager dictionaryManager = DictionaryManager.get();
        List<String> list = new ArrayList<>(Arrays.asList(fileNames));
        list.add(w.getWordAsString());
        return dictionaryManager.query(list.toArray(new String[0]));
    }

    private ArrayList<Word> getAllWords(Tile[][] ts) {
        ArrayList<Word> ws = new ArrayList<>();

        // Horizontal scan
        for (int i = 0; i < ts.length; i++) {
            int j = 0;
            while (j < ts[i].length) {
                ArrayList<Tile> tal = new ArrayList<>();
                int row = i, col = j;
                while (j < ts[i].length && ts[i][j] != null) {
                    tal.add(ts[i][j]);
                    j++;
                }
                if (tal.size() > 1) {
                    Tile[] tiles = new Tile[tal.size()];
                    ws.add(new Word(tal.toArray(tiles), row, col, false));
                }
                j++;
            }
        }

        // Vertical scan
        for (int j = 0; j < ts[0].length; j++) {
            int i = 0;
            while (i < ts.length) {
                ArrayList<Tile> tal = new ArrayList<>();
                int row = i, col = j;
                while (i < ts.length && ts[i][j] != null) {
                    tal.add(ts[i][j]);
                    i++;
                }
                if (tal.size() > 1) {
                    Tile[] tiles = new Tile[tal.size()];
                    ws.add(new Word(tal.toArray(tiles), row, col, true));
                }
                i++;
            }
        }

        return ws;
    }

    public ArrayList<Word> getWords(Word w) {
        Tile[][] ts = getTiles(); // a clone...
        ArrayList<Word> before = getAllWords(ts);
        // demo placement of new word
        int row = w.getRow();
        int col = w.getCol();
        for (Tile t : w.getTiles()) {
            ts[row][col] = t;
            if (w.isVertical()) row++;
            else col++;
        }
        ArrayList<Word> after = getAllWords(ts);
        after.removeAll(before); // only new words remain...
        return after;
    }

    public int getScore(Word w) {
        int row = w.getRow();
        int col = w.getCol();
        int sum = 0;
        int tripleWord = 0, doubleWord = 0;
        for (Tile t : w.getTiles()) {
            sum += t.score;
            if (bonus[row][col] == dl)
                sum += t.score;
            if (bonus[row][col] == tl)
                sum += t.score * 2;
            if (bonus[row][col] == dw)
                doubleWord++;
            if (bonus[row][col] == tw)
                tripleWord++;
            if (w.isVertical()) row++;
            else col++;
        }

        if (doubleWord > 0)
            sum *= (2 * doubleWord);
        if (tripleWord > 0)
            sum *= (3 * tripleWord);
        return sum;

    }

    public int tryPlaceWord(Word w) {

        Tile[] ts = w.getTiles();
        int row = w.getRow();
        int col = w.getCol();
        for (int i = 0; i < ts.length; i++) {
            if (ts[i] == null)
                ts[i] = tiles[row][col];
            if (w.isVertical()) row++;
            else col++;
        }

        Word test = new Word(ts, w.getRow(), w.getCol(), w.isVertical());

        int sum = 0;
        if (boardLegal(test)) {
            System.out.println("Board legal");
            ArrayList<Word> newWords = getWords(test);
            for (Word nw : newWords) {
//                if(inFile(w)){
                if (dictionaryLegal(nw)) {
                    System.out.println("Dictionary legal");
                    sum += getScore(nw);
                } else {
                    System.out.println("Dictionary illegal");
                    return 0;
                }
            }
        } else return -1;


        // the placement
        row = w.getRow();
        col = w.getCol();
        for (Tile t : w.getTiles()) {
            tiles[row][col] = t;
            if (w.isVertical()) row++;
            else col++;
        }

        if (isEmpty) {
            isEmpty = false;
            bonus[7][7] = 0;
        }
        return sum;
    }

    public void print() {
        for (Tile[] ts : tiles) {
            for (Tile t : ts) {
                if (t != null)
                    System.out.print(t.letter);
                else
                    System.out.print("_");
            }
            System.out.println();
        }
    }

    public String[][] getBoardsLetters() {
        String[][] board = new String[15][15];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                Tile t = tiles[i][j];
                if (t != null)
                    board[i][j] = t.letterToString();
                else
                    board[i][j] = "_";
            }
        }
        return board;
    }
}
