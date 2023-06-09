package org.bookscrabble;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import viewmodel.ViewModel;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class HelloController extends Observable implements Observer {

    @FXML
    public Button startButton;
    @FXML
    private TextField playerName, gameName;
    @FXML
    private Label gameNameLabel;
    @FXML
    public VBox stringContainer;

    private ViewModel vm;


    private Stage stage;
    private Scene scene, scene2;
    final int VBOX_HEIGHT = 8;
    final int DIALOG_WIDTH = 300;


    public void setViewModel(ViewModel vm) {
        this.vm = vm;
    }

    public void bindPlayerName() {
        this.vm.playerName.bind(playerName.textProperty());
    }

    public void bindGameName() {
        this.vm.gameName.bind(gameName.textProperty());
    }

    public record GuestData(String playerName, String gameName) {
    }

    public record HostData(String playerName, String fileNames) {
    }

    public void joinExistingGame(ActionEvent actionEvent) {
        //Get main stage
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Dialog<GuestData> dialog = new Dialog<>();
        dialog.setTitle("Enter Game & Your Name");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinWidth(DIALOG_WIDTH);
        playerName = new TextField();
        this.bindPlayerName();
        gameName = new TextField();
        this.bindGameName();
        dialogPane.setContent(new VBox(VBOX_HEIGHT, new Label("Your Name:"), playerName, new Label("Game:"), gameName));
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new GuestData(playerName.getText(), gameName.getText());
            }
            return null;
        });


        dialog.showAndWait().ifPresent((GuestData guestData) -> {
            vm.connectToGame();
        });


    }
    @FXML
    private void rectangleClicked(ActionEvent event) {
        Rectangle rectangle = (Rectangle) event.getSource();
        int row = GridPane.getRowIndex(rectangle);
        int column = GridPane.getColumnIndex(rectangle);
        System.out.println("Clicked Rectangle at row: " + row + ", column: " + column);
    }
    public void createNewGame(ActionEvent actionEvent) {
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Dialog<HostData> dialog = new Dialog<>();
        dialog.setTitle("Enter Your Name & File Names");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinWidth(DIALOG_WIDTH);
        playerName = new TextField();
        this.bindPlayerName();
        TextField fileNames = new TextField();
        dialogPane.setContent(new VBox(VBOX_HEIGHT, new Label("Your Name:"), playerName, new Label("File Names:"), fileNames));
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new HostData(playerName.getText(), fileNames.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent((HostData guestData) -> {
            vm.createGame(fileNames.getText().split(","));
        });
    }

    private Button getStartButton() {
        return this.startButton;
    }

    public void displayStrings(String[] strings) {
        stringContainer.getChildren().clear();
        for (String str : strings) {
            Label label = new Label(str);
            stringContainer.getChildren().add(label);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //Change screen To screen with players list and start button
        //Check o = viewmodel
        //arg use to send infos (board change , bag changed ..)

        // after creating new game, screen with name  of the game, names of players start
        //everytime new player connect, it will send message to every player to let them now then we will catch that and update the screen


        if (o == vm) {
            String type = (String) arg;
            if ((Objects.equals(type, "Players")) && (!vm.isGameStarted)) {
                Platform.runLater(() -> {
                    FXMLLoader loader;

                    // Create a new window with pre-game-screen.fxml if it doesn't exist yet
                    loader = new FXMLLoader(getClass().getResource("pre-game-screen.fxml"));
                    Parent root;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // Get the controller instance from the FXMLLoader
                    HelloController controller = loader.getController();

                    // Display the game name on the window
                    String name_s = vm.gameName.getValue() + " ";
                    controller.gameNameLabel.setText(name_s);

                    scene = new Scene(root);
                    stage.setTitle("Pre-Game");
                    stage.setScene(scene);


                    StringProperty[] playerArray = vm.getPlayersArray();
                    String[] stringArray = new String[playerArray.length];
                    for (int i = 0; i < playerArray.length; i++) {
                        String playerNameWithScore = playerArray[i].get();
                        String[] parts = playerNameWithScore.split(":");
                        String playerName = parts[0];
                        stringArray[i] = playerName;
                    }
                    controller.displayStrings(stringArray);

                    boolean showStartButton = vm.getIsHost();
                    controller.getStartButton().setVisible(showStartButton);

                    controller.startButton.setOnAction(event -> {
                        vm.startGame();
                    });
                    stage.show();
                });
            }
            if (Objects.equals(type, "GameStarted")) {
                Platform.runLater(() -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("board1.fxml"));
                    Parent root;

                    BoardController mwc = new BoardController(vm);
                    fxmlLoader.setController(mwc);

                    try {
                        root = fxmlLoader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mwc.setBoardAndDisplay();

                    scene2 = new Scene(root,700, 700);
                    stage.setTitle("Board");
                    stage.setScene(scene2);

                    stage.show();

                });
            }
        }

    }
}

