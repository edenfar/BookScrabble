package org.bookscrabble;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import viewmodel.ViewModel;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class HelloController implements Observer {
    @FXML
    private TextField playerName, gameName;
    private ViewModel vm;
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

        dialog.showAndWait().ifPresent((GuestData guestData) -> vm.connectToGame());
    }
    @FXML
    private void rectangleClicked(ActionEvent event) {
        Rectangle rectangle = (Rectangle) event.getSource();
        int row = GridPane.getRowIndex(rectangle);
        int column = GridPane.getColumnIndex(rectangle);
        System.out.println("Clicked Rectangle at row: " + row + ", column: " + column);
    }
    public void createNewGame(ActionEvent actionEvent) {
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

        dialog.showAndWait().ifPresent((HostData guestData) -> vm.createGame(fileNames.getText().split(",")));
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}