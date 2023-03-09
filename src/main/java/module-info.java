module azul {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.desktop;
    requires com.google.common;

    opens communication;
    exports communication;

    opens game;
    exports game;

    opens GUIs;
    exports GUIs;

    exports GUIs.Controllers;
    opens GUIs.Controllers;
}