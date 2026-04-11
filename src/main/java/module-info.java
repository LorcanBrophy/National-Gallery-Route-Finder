module com.example.dsa2_ca2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dsa2_ca2 to javafx.fxml;
    exports com.example.dsa2_ca2;
    exports com.example.dsa2_ca2.controller;
    opens com.example.dsa2_ca2.controller to javafx.fxml;
}