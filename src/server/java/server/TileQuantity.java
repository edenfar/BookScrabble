package server;

import java.io.Serializable;

public class TileQuantity implements Serializable {

    private int id;
    private Tile tile;
    private int value;

    public TileQuantity(Tile tile, int value) {
        this.tile = tile;
        this.value = value;
    }

    public TileQuantity() {
        this.tile = null;
        this.value = 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TileQuantity tileQuantity)) return false;
        return tileQuantity.id == this.id && tileQuantity.tile.equals(this.tile) && tileQuantity.value == this.value;
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
