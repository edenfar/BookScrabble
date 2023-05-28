module org.bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.bookscrabble to javafx.fxml;
    exports org.bookscrabble;
}