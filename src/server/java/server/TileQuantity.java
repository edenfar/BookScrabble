package server;

import java.io.Serializable;

public class TileQuantity implements Serializable {

    private int id;
    private Tile tile;
    private int value;
    private Tile.Bag bag;

    public TileQuantity(Tile tile, int value) {
        this.tile = tile;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Tile getTile() {
        return tile;
    }

    public void decrementValue() {
        this.value--;
    }

    public void incrementValue() {
        this.value--;
    }

    public int getId() {
        return id;
    }
}
