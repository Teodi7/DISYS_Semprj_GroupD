module at.fhtechnikum.energygui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;


    opens at.fhtechnikum.energygui to javafx.fxml;
    exports at.fhtechnikum.energygui;
}