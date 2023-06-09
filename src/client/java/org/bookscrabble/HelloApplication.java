package org.bookscrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import viewmodel.ViewModel;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("board1.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        BoardController mwc = fxmlLoader.getController();
        ViewModel vm = new ViewModel();
        mwc.setViewModel(vm);
        vm.addObserver(mwc);
    }

    public static void main(String[] args) {
        launch();
    }
}