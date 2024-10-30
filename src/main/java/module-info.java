module com.example.bibliotech {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;
    requires java.mail;

    opens com.example.bibliotech to javafx.fxml;
    exports com.example.bibliotech.presentation;
    opens com.example.bibliotech.presentation to javafx.fxml;
    exports com.example.bibliotech.config;
    exports com.example.bibliotech.presentation.control to javafx.fxml;
    opens com.example.bibliotech.presentation.control  to javafx.fxml;
    opens com.example.bibliotech.config to javafx.fxml;
    exports com.example.bibliotech.presentation.components to javafx.fxml;
    opens com.example.bibliotech.presentation.components to javafx.fxml;

}