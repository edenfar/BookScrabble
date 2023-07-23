package model;

public class HostModel extends Model {
    public void startGame() {
        this.sendMessage("start");
    }

    public void saveGame() {
        this.sendMessage("save");
    }
}
