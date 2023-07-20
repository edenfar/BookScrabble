package org.bookscrabble;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
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
    public Button closeButton;
    @FXML
    private Text winnerName;
    @FXML
    private Text winnerScore;
    @FXML
    private TextField playerName, gameName;
    @FXML
    private Label gameNameLabel;
    @FXML
    public VBox stringContainer;
    private ViewModel vm;
    private Stage stage;
    private Scene scene, scene3;
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

        Optional<GuestData> result = dialog.showAndWait();
        if (result.isPresent()) {
            vm.connectToGame();
        } else {    //Cancel button pressed
            System.out.println("Cancel button pressed");
        }


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

        Optional<HostData> result = dialog.showAndWait();
        if (result.isPresent()) {
            vm.createGame(fileNames.getText().split(","));
        } else {    //Cancel button pressed
            System.out.println("Cancel button pressed");
        }
    }

    private Button getStartButton() {
        return this.startButton;
    }

    public void displayStrings(String[] strings) {
        stringContainer.getChildren().clear();
        for (String str : strings) {
            Label label = new Label(str);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 25)); // Set font and size
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
            if ((Objects.equals(type, "GameStarted")) || (Objects.equals(type, "NewTurn"))) {
                Platform.runLater(() -> {
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("board1.fxml"));
                    BoardController mwc = new BoardController(vm);
                    fxmlLoader.setController(mwc);
                    try {
                        Parent root = fxmlLoader.load();
                        mwc.setBoardAndDisplay();
                        Scene scene2 = new Scene(root, 700, 750);
                        stage.setTitle("Board");
                        stage.setScene(scene2);
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });


            }
            if (Objects.equals(type, "Illegal")) {
                Platform.runLater(() -> {
                    Stage popupStage = new Stage();
                    popupStage.initOwner(stage);
                    popupStage.initModality(Modality.APPLICATION_MODAL);
                    popupStage.initStyle(StageStyle.UTILITY);

                    // Create a label with the message
                    Label messageLabel = new Label(vm.illegal);

                    // Create an OK button
                    Button okButton = new Button("OK");
                    okButton.setOnAction(e -> popupStage.close());

                    // Create a layout pane and add the label and button
                    VBox root = new VBox(10);
                    root.setAlignment(Pos.CENTER);
                    root.getChildren().addAll(messageLabel, okButton);

                    // Create the scene and set it in the stage
                    Scene scene = new Scene(root, 200, 150);
                    popupStage.setScene(scene);

                    // Show the popup stage
                    popupStage.showAndWait();
                });
            }
            if (Objects.equals(type, "GameEnded")) {
                Platform.runLater(() -> {
                    stage.close();
                    winnerName = new Text();
                    winnerName.setText(vm.winnerName);
                    winnerScore = new Text();
                    winnerScore.setText(vm.winnerScore);

                    System.out.println("The winner is " + winnerName.getText() + " with score " + winnerScore.getText() + " points");
                    Stage newStage = new Stage();
                    try {

                        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("end-game-screen.fxml"));
                        HelloController controller = new HelloController();
                        fxmlLoader.setController(controller);
                        Parent root = fxmlLoader.load();
                        controller.setWinner(winnerName.getText(), winnerScore.getText());
                        scene3 = new Scene(root, 600, 500);
                        newStage.setTitle("Game Ended");
                        newStage.setScene(scene3);
                        controller.closeButton.setOnAction(e -> newStage.close());
                        newStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

    }

    private void setWinner(String name, String score) {
        winnerName.setText(name);
        winnerScore.setText(score);
    }
}

