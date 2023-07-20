module org.bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.sql;


    opens org.bookscrabble to javafx.fxml;
    opens server to org.hibernate.orm.core;
    exports org.bookscrabble;
    exports server;
}