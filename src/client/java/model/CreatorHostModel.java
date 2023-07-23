package model;

public class CreatorHostModel extends HostModel {
    public void createGame(String playerName, String[] fileNames) {
        this.sendMessage(String.join(",", "host", playerName, String.join(",", fileNames)));
    }
}
