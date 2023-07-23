package viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.*;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    public String rounds;
    public String winnerName;
    public String winnerScore;
    Model m;
    public StringProperty playerName, gameName, playerScore;
    public StringProperty[] playersArray;
    public StringProperty[] scoreBoard;

    public String[][] boardData;
    public StringProperty playerTilesLetters;
    public StringProperty playerTilesScore;
    public StringProperty currPlayerName;
    public boolean isHost = false;
    public boolean isGameStarted = false;

    public boolean firstRound = true;

    public String illegal;
    public int round = 1;

    public ViewModel() {
        playerName = new SimpleStringProperty();
        gameName = new SimpleStringProperty();
        playersArray = new StringProperty[0];
        currPlayerName = new SimpleStringProperty();
        playerScore = new SimpleStringProperty();
        boardData = new String[15][15];
        rounds = "";
    }

    public void setModel(Model m) {
        this.m = m;
        m.addObserver(this);
    }

    public void setFirstRound() {
        this.firstRound = false;
    }

    public StringProperty[] getPlayersArray() {
        String[] stringPlayers = m.getPlayersArray();
        this.playersArray = new StringProperty[stringPlayers.length];
        for (int i = 0; i < stringPlayers.length; i++) {
            this.playersArray[i] = new SimpleStringProperty(stringPlayers[i]);
        }
        return this.playersArray;
    }

    public boolean getAllPlayersConnected() {
        return m.getAllPlayersConnected();
    }

    public StringProperty[] getPlayersScores() {
        String[] playersScoreArray = m.getPlayersScoreArray();
        this.scoreBoard = new StringProperty[playersScoreArray.length];
        for (int i = 0; i < playersScoreArray.length; i++) {
            this.scoreBoard[i] = new SimpleStringProperty(playersScoreArray[i]);
        }
        return this.scoreBoard;
    }

    public void createGame(String[] fileNames) {
        this.isHost = true;
        this.currPlayerName.setValue(playerName.getValue());
        CreatorHostModel creatorHostModel = new CreatorHostModel();
        this.setModel(creatorHostModel);
        // TODO: Request server details from client
        creatorHostModel.connect("localhost", 6123);
        creatorHostModel.createGame(playerName.getValue(), fileNames);
    }

    public void startGame() {
        if (isHost) {
            ((HostModel) m).startGame();
            isGameStarted = true;
        } else {
            throw new RuntimeException("Cannot start game as guest");
        }
    }

    public void saveGame() {
        if (this.m instanceof HostModel hostModel) {
            hostModel.saveGame();
        } else {
            throw new RuntimeException("Cannot save game as guest");
        }
    }

    public void connectToGame() {
        this.isHost = false;
        GuestModel guestModel = new GuestModel();
        this.setModel(guestModel);
        guestModel.connect("localhost", 6123);
        guestModel.join(playerName.getValue(), gameName.getValue());
    }

    public void loadExistingGame() {
        this.isHost = true;
        LoaderHostModel loaderHostModel = new LoaderHostModel();
        this.setModel(loaderHostModel);
        loaderHostModel.connect("localhost", 6123);
        loaderHostModel.load(playerName.getValue(), gameName.getValue());
    }

    public void onNewGame(String gameName) {
        this.gameName.setValue(gameName);
    }

    public void playTurn(String word, int r, int c, boolean vertical, String tilesToReplace) {
        m.playTurn(word, r, c, vertical, tilesToReplace);
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
            if (Objects.equals(type, "Round")) {
                this.round = m.getRound();
                this.setChanged();
                this.notifyObservers("Round");
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
                this.playerTilesLetters = new SimpleStringProperty(m.getPlayerTilesLetters());
                this.playerTilesScore = new SimpleStringProperty(m.getPlayerTilesScore());
                this.setChanged();
                this.notifyObservers("PlayerTiles");
            }
            if (Objects.equals(type, "NewTurn")) {
                this.setChanged();
                this.notifyObservers("NewTurn");
            }
            if (Objects.equals(type, "PlayerScore")) {
                this.playerScore = new SimpleStringProperty(m.getPlayerScore());
                this.setChanged();
                this.notifyObservers("PlayerScore");
            }
            if (Objects.equals(type, "Board")) {
                for (int i = 0; i < m.getBoardData().length; i++) {
                    this.boardData[i] = m.getBoardData()[i].clone();
                }
            }
            if (Objects.equals(type, "Rounds")) {
                this.rounds = m.getrounds();
                this.setChanged();
                this.notifyObservers("Rounds");
            }
            if (Objects.equals(type, "Illegal")) {
                this.illegal = m.getIllegal();
                this.setChanged();
                this.notifyObservers("Illegal");
            }
            if (Objects.equals(type, "GameEnded")) {
                String[] temp = m.getWinner().split(",");
                this.winnerName = temp[0];
                this.winnerScore = temp[1];
                this.setChanged();
                this.notifyObservers("GameEnded");
            }
            if (Objects.equals(type, "ScoreBoard")) {
                this.setChanged();
                this.notifyObservers("ScoreBoard");
            }

        }
    }
}