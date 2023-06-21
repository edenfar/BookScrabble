module org.bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.bookscrabble to javafx.fxml;
    exports org.bookscrabble;
}