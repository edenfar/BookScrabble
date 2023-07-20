package server;

import java.io.Serializable;
import java.util.*;

public class Tile implements Serializable {

    public Integer id;
    public final char letter;
    public final int score;

    public Tile() {
        this.letter = 'A';
        this.score = 0;
    }

    Tile(char letter, int score) {
        super();
        this.letter = letter;
        this.score = score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, score);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Tile other = (Tile) obj;
        return letter == other.letter && score == other.score;
    }

    @Override
    public String toString() {
        return letter + Integer.toString(score);
    }

    public String letterToString() {
        return Character.toString(letter);
    }

    public static class Bag {
        private int id;
        private int[] maxQuantities = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        private Tile[] tiles = {
                new Tile('A', 1),
                new Tile('B', 3),
                new Tile('C', 3),
                new Tile('D', 2),
                new Tile('E', 1),
                new Tile('F', 4),
                new Tile('G', 2),
                new Tile('H', 4),
                new Tile('I', 1),
                new Tile('J', 8),
                new Tile('K', 5),
                new Tile('L', 1),
                new Tile('M', 3),
                new Tile('N', 1),
                new Tile('O', 1),
                new Tile('P', 3),
                new Tile('Q', 10),
                new Tile('R', 1),
                new Tile('S', 1),
                new Tile('T', 1),
                new Tile('U', 1),
                new Tile('V', 4),
                new Tile('W', 4),
                new Tile('X', 8),
                new Tile('Y', 4),
                new Tile('Z', 10)
        };
        private List<TileQuantity> quantities = new ArrayList<>();

        Random r;
        int size;

        public Bag() {
            for (int i = 0; i < tiles.length; i++) {
                quantities.add(new TileQuantity(tiles[i], maxQuantities[i]));
            }
            r = new Random();
            size = 98;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Bag bag)) return false;
            return bag.id == this.id && bag.size == this.size && bag.quantities.equals(this.quantities);
        }

        // draw rand tile from bag
        public Tile getRand() {
            if (size > 0) {
                int i = r.nextInt(quantities.size());
                while (quantities.get(i).getValue() == 0) i = r.nextInt(quantities.size());
                size -= 1;
                quantities.get(i).decrementValue();
                return tiles[i];
            }
            return null;
        }

        public Tile[] getRandomTiles(int count) {
            Tile[] randTiles = new Tile[count];
            for (int i = 0; i < count; i++) {
                randTiles[i] = getRand();
            }
            return randTiles;
        }

        public Tile getTile(char c) {
            if (c >= 'A' && c <= 'Z' && quantities.get(c - 'A').getValue() > 0) {
                quantities.get(c - 'A').decrementValue();
                size -= 1;
                return tiles[c - 'A'];
            }
            return null;
        }

        public int size() {
            return size;
        }

        public void put(Tile t) {
            int i = t.letter - 'A';
            if (quantities.get(i).getValue() < maxQuantities[i]) quantities.get(i).incrementValue();
        }

        public ArrayList<TileQuantity> getQuantities() {
            return new ArrayList<TileQuantity>(quantities);
        }

    }

}
