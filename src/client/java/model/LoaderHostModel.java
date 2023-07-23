package model;

public class LoaderHostModel extends HostModel {
    public void load(String playerName, String gameName) {
        this.sendMessage(String.join(",","load", playerName, gameName));
    }
}
