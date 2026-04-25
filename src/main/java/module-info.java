module com.example.dsa2_ca2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    exports com.example.dsa2_ca2.graph;
    opens com.example.dsa2_ca2.graph to javafx.fxml;

    exports com.example.dsa2_ca2.controller;
    opens com.example.dsa2_ca2.controller to javafx.fxml;

    exports com.example.dsa2_ca2.main;
    opens com.example.dsa2_ca2.main to javafx.fxml;
}