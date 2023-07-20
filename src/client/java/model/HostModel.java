package model;

public class HostModel extends Model {
    public void createGame(String playerName, String[] fileNames) {
        this.sendMessage(String.join(",", "host", playerName, String.join(",", fileNames)));
    }

    public void startGame() {
        this.sendMessage("start");
    }

    public void saveGame() {
        this.sendMessage("save");
    }
}
