package com.example.dsa2_ca2.utils;

import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.Set;

public class Utils {

    private final Label pathLengthLabel;

    public Utils(VBox imageContainer) {
        this.pathLengthLabel = new Label("");
        imageContainer.getChildren().add(pathLengthLabel);

    }

    public void setPathLabel(String text) {
        pathLengthLabel.setText(text);
        pathLengthLabel.setVisible(true);
    }

    public void removePathLabel() {
        pathLengthLabel.setText("");
        pathLengthLabel.setVisible(false);
    }

    public MyList<Integer> extractIDs(Set<Room> rooms) {
        MyList<Integer> result = new MyArrayList<>();

        for (Room room : rooms) result.add(room.getId());

        return result;
    }

    public void viewRoutes(MyList<MyList<Room>> allPaths) {

        // create a textArea and fill it with the routes
        TextArea textArea = new TextArea(printRoutes(allPaths));
        textArea.setWrapText(false);

        // create an alert to display the textArea
        Alert resultWindow = new Alert(Alert.AlertType.INFORMATION);
        resultWindow.setTitle("All Permutations");
        resultWindow.setHeaderText("Disply of all DFS routes");
        resultWindow.setGraphic(null);

        // adjust size
        resultWindow.getDialogPane().setMinWidth(500);
        resultWindow.getDialogPane().setMinHeight(200);

        // set the textArea as the content of the alert
        resultWindow.getDialogPane().setContent(textArea);

        // show the alert and wait for the user to close it
        resultWindow.showAndWait();
    }

    public String printRoutes(MyList<MyList<Room>> allPaths) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < allPaths.size(); i++) {
            MyList<Room> path = allPaths.get(i);
            result.append("Route ").append(i + 1).append(" (length: ").append(path.size()).append(" rooms): ");

            for (int j = 0; j < path.size(); j++) {
                result.append(path.get(j).getId());
                if (j < path.size() - 1) result.append(" → ");
            }
            result.append("\n");
        }

        return result.toString();
    }
}
