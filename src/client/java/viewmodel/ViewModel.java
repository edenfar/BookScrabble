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

    public ViewModel() {
        playerName = new SimpleStringProperty();
        gameName = new SimpleStringProperty();
    }

    public void setModel(Model m) {
        this.m = m;
        m.addObserver(this);
    }

    public void createGame(String[] fileNames) {
        HostModel hostModel = new HostModel();
        this.setModel(hostModel);
        // TODO: Request server details from client
        hostModel.connect("localhost", 6123);
        hostModel.createGame(playerName.getValue(), fileNames);
    }

    public void connectToGame() {
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
        }
    }
}
