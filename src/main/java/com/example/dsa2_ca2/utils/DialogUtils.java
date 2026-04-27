package com.example.dsa2_ca2.utils;

import com.example.dsa2_ca2.graph.Edge;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.model.Exhibit;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class DialogUtils {

    public  Graph<Room> graph;

    public DialogUtils(Graph<Room> graph) {
        this.graph = graph;
    }

    public Map<String, MyList<Integer>> getArtistRoomMap() {
        Map<String, MyList<Integer>> artistRoomMap = new HashMap<>();

        List<Room> rooms = new ArrayList<>();
        for (Vertex<Room> vertex : graph.getAllVertices()) {
            rooms.add(vertex.getData());
        }

        for (Room room : rooms) {
            MyList<Exhibit> exhibits = room.getExhibits();

            for (int i = 0; i < exhibits.size(); i++) {
                String artist = exhibits.get(i).getArtist();

                if (!artistRoomMap.containsKey(artist)) artistRoomMap.put(artist, new MyArrayList<>());

                // removes duplicates (essentially .contains() )
                MyList<Integer> roomIDs = artistRoomMap.get(artist);
                boolean alreadyExists = false;
                for (int j = 0; j < roomIDs.size(); j++) {
                    if (roomIDs.get(j) == room.getId()) {
                        alreadyExists = true;
                        break;
                    }
                }
                if (!alreadyExists) roomIDs.add(room.getId());

            }
        }

        return artistRoomMap;
    }

    public MyList<String> getAllRoomName() {
        MyList<String> roomNames = new MyArrayList<>();

        List<Room> rooms = new ArrayList<>();
        for (Vertex<Room> vertex : graph.getAllVertices()) {
            rooms.add(vertex.getData());
        }
        rooms.sort(Comparator.comparingInt(Room::getId));

        for (Room room : rooms) {
            String displayNumber;

            if (room.getId() == 151) {
                displayNumber = "15a";
            } else if (room.getId() == 171) {
                displayNumber = "17a";
            } else {
                displayNumber = String.valueOf(room.getId());
            }

            roomNames.add(displayNumber + " - " + room.getName());
        }

        return roomNames;
    }

    public Set<String> getAllArtistNames() {
        Map<String, MyList<Integer>> artistRoomMap = getArtistRoomMap();

        return new LinkedHashSet<>(artistRoomMap.keySet());
    }


    // reusable dialogs

    public String showComboDialog(ComboBox<String> comboBox, Dialog<String> dialog) {
        dialog.getDialogPane().setContent(comboBox);

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(ok, cancel);

        dialog.setResultConverter(button ->
                button == ok ? comboBox.getValue() : null
        );

        return dialog.showAndWait().orElse(null);
    }

    public Integer numberDialog(String dialogTitle, String headerText) {

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);
        dialog.setHeaderText(headerText);

        Spinner<Integer> spinner = new Spinner<>(1, 100, 1);
        spinner.setEditable(false);

        dialog.getDialogPane().setContent(spinner);

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);

        dialog.setResultConverter(button -> button == ok ? spinner.getValue() : null);

        return dialog.showAndWait().orElse(null);
    }

    public String textInputDialog(String title, String header) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setGraphic(null);

        return dialog.showAndWait().orElse("").trim();
    }

    public Integer integerInputDialog(String dialogTitle, String headerText) {
        String result = textInputDialog(dialogTitle, headerText);
        if (result == null) return null;

        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double doubleInputDialog(String dialogTitle, String headerText) {
        String result = textInputDialog(dialogTitle, headerText);
        if (result == null) return null;

        try {
            return Double.parseDouble(result);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    // route info dialogs

    public int chooseStartRoomDialog() {
        MyList<String> roomNames = getAllRoomName();

        ComboBox<String> comboBox = new ComboBox<>();

        for (String name : roomNames) comboBox.getItems().add(name);


        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Choose Start Room");
        dialog.setHeaderText("Select the starting room:");

        String result = showComboDialog(comboBox, dialog);
        if (result == null) return -1;

        int startID;

        String[] parts = result.split(" - ");
        startID = Integer.parseInt(parts[0]);

        return startID;

    }

    public int chooseEndRoomDialog() {
        MyList<String> roomNames = getAllRoomName();

        ComboBox<String> comboBox = new ComboBox<>();

        for (String name : roomNames) comboBox.getItems().add(name);

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Choose End Room");
        dialog.setHeaderText("Select the ending room:");

        String result = showComboDialog(comboBox, dialog);

        int endID;

        if (result != null) {
            String[] parts = result.split(" - ");
            endID = Integer.parseInt(parts[0]);

            return endID;
        }

        return -1;
    }

    public Set<Room> showRoomAvoidanceDialog() {
        Dialog<Set<Room>> dialog = new Dialog<>();
        dialog.setTitle("Select Rooms to Avoid");
        dialog.setHeaderText("Check rooms you want to exclude from the route (ctrl + click):");

        ListView<Room> listView = new ListView<>();
        listView.setPrefSize(600, 300);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        MyList<Vertex<Room>> allVerts = graph.getAllVertices();
        for (int i = 0; i < allVerts.size(); i++) {
            Room room = allVerts.get(i).getData();
            listView.getItems().add(room);
        }

        listView.setCellFactory(_ -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);

                setText((empty || room == null) ? null : room.getId() + " - " + room.getName());
            }
        });

        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new HashSet<>(listView.getSelectionModel().getSelectedItems());
            }
            return null;
        });

        Optional<Set<Room>> result = dialog.showAndWait();

        return result.orElse(new HashSet<>());
    }

    public Set<Room> showWaypointDialog() {
        Dialog<Set<Room>> dialog = new Dialog<>();
        dialog.setTitle("Select Rooms to Visit");
        dialog.setHeaderText("Check rooms you want to include from the route (ctrl + click):");

        ListView<Room> listView = new ListView<>();
        listView.setPrefSize(600, 300);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        MyList<Vertex<Room>> allVerts = graph.getAllVertices();
        for (int i = 0; i < allVerts.size(); i++) {
            Room room = allVerts.get(i).getData();
            listView.getItems().add(room);
        }

        listView.setCellFactory(_ -> new ListCell<Room>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);

                setText((empty || room == null) ? null : room.getId() + " - " + room.getName());
            }
        });

        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new HashSet<>(listView.getSelectionModel().getSelectedItems());
            }
            return null;
        });

        Optional<Set<Room>> result = dialog.showAndWait();

        return result.orElse(new HashSet<>());
    }

    public Set<String> showArtistsDialog() {
        Dialog<Set<String>> dialog = new Dialog<>();
        dialog.setTitle("Select Favorite Artists");
        dialog.setHeaderText("Select artists you want to include in the route (ctrl + click):");

        ListView<String> listView = new ListView<>();
        listView.setPrefSize(600, 300);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        Map<String, MyList<Integer>> artistRoomMap = getArtistRoomMap();
        for (String artist : artistRoomMap.keySet()) {
            MyList<Integer> roomIds = artistRoomMap.get(artist);
            StringBuilder roomIdsStr = new StringBuilder();
            for (int i = 0; i < roomIds.size(); i++) {
                if (i > 0) roomIdsStr.append(", ");
                roomIdsStr.append(roomIds.get(i));
            }
            String display = roomIdsStr + " - " + artist;
            listView.getItems().add(display);
        }


        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Set<String> selectedDisplays = new HashSet<>(listView.getSelectionModel().getSelectedItems());
                Set<String> selectedArtists = new HashSet<>();

                for (String display : selectedDisplays) {
                    String artist = display.substring(display.indexOf(" - ") + 3);
                    selectedArtists.add(artist);
                }
                return selectedArtists;
            }
            return null;
        });

        Optional<Set<String>> result = dialog.showAndWait();

        return result.orElse(new HashSet<>());
    }



    // updaye CSV dialogs
    public Room createRoomDialog() {
        Integer id = integerInputDialog("Room ID", "Enter new room ID (above 66)");
        if (id == null || id < 66) return null;

        String name = textInputDialog("Room Name", "Enter Room Name");
        if (name == null || name.isEmpty()) return null;

        String period = textInputDialog("Period", "Enter Room Period");
        if (period == null || period.isEmpty()) return null;

        Integer x = integerInputDialog("X Coordinate", "Enter Room X Coord (0 - 863)");
        if (x == null) return null;

        Integer y = integerInputDialog("Y Coordinate", "Enter Room Y Coord (0 - 543)");
        if (y == null) return null;

        return new Room(id, name, period, x, y);
    }

    public Object[] createExhibitDialog() {

        Integer roomID = integerInputDialog("Room ID", "Enter room ID to add exhibit");
        if (roomID == null) return null;
        Vertex<Room> roomVertex = graph.getVertex(roomID);
        if (roomVertex == null) return null;

        String title = textInputDialog("Exhibit Title", "Enter exhibit title");
        if (title == null || title.isEmpty()) return null;

        String artist = textInputDialog("Artist Name", "Enter artist name");
        if (artist == null || artist.isEmpty()) return null;

        return new Object[]{roomID, title, artist};
    }

    public Object[] createEdgeDialog() {
        Integer sourceId = integerInputDialog("Source Room ID", "Enter source room ID");
        if (sourceId == null) return null;
        Vertex<Room> sourceVertex = graph.getVertex(sourceId);
        if (sourceVertex == null) return null;


        Integer destId = integerInputDialog("Destination Room ID", "Enter destination room ID");
        if (destId == null) return null;
        Vertex<Room> destVertex = graph.getVertex(destId);
        if (destVertex == null) return null;

        if (sourceId.equals(destId)) return null;

        Double weight = doubleInputDialog("Weight", "Enter connection weight");
        if (weight == null) return null;

        return new Object[]{sourceId, destId, weight};
    }

}
