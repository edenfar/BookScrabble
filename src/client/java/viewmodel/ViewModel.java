package viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    public boolean isHost = false;

    public ViewModel() {
        playerName = new SimpleStringProperty();
        gameName = new SimpleStringProperty();
        playersArray = new StringProperty[0];
    }

    public void setModel(Model m) {
        this.m = m;
        m.addObserver(this);
    }

    public StringProperty[] getPlayersArray() {
        String[] stringPlayers = m.getPlayersArray();
        this.playersArray = new StringProperty[stringPlayers.length];
        for (int i = 0; i < stringPlayers.length; i++) {
            this.playersArray[i] = new SimpleStringProperty(stringPlayers[i]);
        }
        return this.playersArray;
    }

    public boolean getIsHost() {
        return this.isHost;
    }

    public void createGame(String[] fileNames) {
        this.isHost = true;
        HostModel hostModel = new HostModel();
        this.setModel(hostModel);
        // TODO: Request server details from client
        hostModel.connect("localhost", 6123);
        hostModel.createGame(playerName.getValue(), fileNames);
    }

    public void startGame() {
        if (this.m instanceof HostModel) {
            ((HostModel) m).startGame();
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
        // Trigger view to change screen
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == m) {
            String type = (String) arg;

            if (Objects.equals(type, "GameName"))
                this.onNewGame(m.getGameName());
            if (Objects.equals(type, "Players")) {
                this.setChanged();
                this.notifyObservers("Players");
            }
            if (Objects.equals(type, "GameStarted")) {
                this.setChanged();
                this.notifyObservers("GameStarted");
            }

        }

    }


}
