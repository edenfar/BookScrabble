package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.GuestModel;
import model.HostModel;
import model.Model;


import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    Model m;
    public StringProperty playerName, gameName;
    public StringProperty[] playersArray;

    public StringProperty playerTiles;
    public StringProperty currPlayerName;
    public boolean isHost = false;
    public boolean isGameStarted = false;

    public ViewModel() {
        playerName = new SimpleStringProperty();
        gameName = new SimpleStringProperty();
        playersArray = new StringProperty[0];
        currPlayerName = new SimpleStringProperty();
    }

    public void setModel(Model m) {
        this.m = m;
        m.addObserver(this);
    }

    public StringProperty[] getPlayersArray() {
        return this.playersArray;
    }

    public boolean getIsHost() {
        return this.isHost;
    }

    public void createGame(String[] fileNames) {
        this.isHost = true;
        this.currPlayerName.setValue(playerName.getValue());
        HostModel hostModel = new HostModel();
        this.setModel(hostModel);
        // TODO: Request server details from client
        hostModel.connect("localhost", 6123);
        hostModel.createGame(playerName.getValue(), fileNames);
    }

    public void startGame() {
        if (this.m instanceof HostModel) {
            ((HostModel) m).startGame();
            isGameStarted = true;
        }else{
            throw new RuntimeException("Cannot start game as guest");
        }
    }

    public void connectToGame() {
        this.isHost = false;
        GuestModel guestModel = new GuestModel();
        this.setModel(guestModel);
        guestModel.connect("localhost", 6123);
        guestModel.join(playerName.getValue(), gameName.getValue());
    }

    public void onNewGame(String gameName) {
        this.gameName.setValue(gameName);
        System.out.println(this.gameName.getValue());
    }

    public void playTurn(String word, int r, int c, boolean vertical) {
        m.playTurn(word, r, c, vertical);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == m) {
            String type = (String) arg;

            if (Objects.equals(type, "GameName"))
                this.onNewGame(m.getGameName());
            if (Objects.equals(type, "Players")) {
                String[] stringPlayers = m.getPlayersArray();
                this.playersArray = new StringProperty[stringPlayers.length];
                for (int i = 0; i < stringPlayers.length; i++) {
                    this.playersArray[i] = new SimpleStringProperty(stringPlayers[i]);
                }
                this.setChanged();
                this.notifyObservers("Players");
            }
            if (Objects.equals(type, "GameStarted")) {
                this.setChanged();
                this.notifyObservers("GameStarted");
            }
            if (Objects.equals(type, "CurrPlayer")) {
                this.currPlayerName = new SimpleStringProperty(m.getCurrPlayerName());
                this.setChanged();
                this.notifyObservers("CurrentPlayer");
            }
            if (Objects.equals(type, "PlayerTiles")) {
                this.playerTiles = new SimpleStringProperty(m.getPlayerTiles());
                this.setChanged();
                this.notifyObservers("PlayerTiles");
            }

        }

    }
}
