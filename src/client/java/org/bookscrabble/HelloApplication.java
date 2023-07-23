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
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 300);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        HelloController mwc = fxmlLoader.getController();
        ViewModel vm = new ViewModel();
        mwc.setViewModel(vm);
        vm.addObserver(mwc);
    }

    public static void main(String[] args) {
        launch();
    }
}