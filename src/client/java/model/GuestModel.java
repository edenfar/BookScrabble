package model;

public class GuestModel extends Model {
    public void join(String playerName, String gameName) {
        this.sendMessage(String.join(",","guest", playerName, gameName));
    }
}
