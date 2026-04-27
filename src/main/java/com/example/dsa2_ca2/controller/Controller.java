package com.example.dsa2_ca2.controller;

import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.loader.CSVLoader;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.model.Exhibit;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;
import com.example.dsa2_ca2.traversal.BFS;
import com.example.dsa2_ca2.traversal.DFS;
import com.example.dsa2_ca2.traversal.Dijkstra;
import com.example.dsa2_ca2.traversal.PixelBFS;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class Controller {
    @FXML private Canvas mapCanvas;
    @FXML private VBox imageContainer;
    private GraphicsContext graphicsContext;
    private Image backgroundImage;

    PixelBFS.Point startPoint = null;
    PixelBFS.Point endPoint = null;

    private Graph<Room> graph;

    private EventHandler<MouseEvent> pointSelectionHandler;

    @FXML
    public void initialize() throws IOException {
        buildGraph();
        loadBackgroundImage();
        setupEventHandlers();


    }

    public void buildGraph() throws IOException {
        CSVLoader loader = new CSVLoader();

        graph = loader.loadGraph(
                "src/main/resources/com/example/dsa2_ca2/rooms.csv",
                "src/main/resources/com/example/dsa2_ca2/exhibits.csv",
                "src/main/resources/com/example/dsa2_ca2/edges.csv"
        );
    }

    @FXML
    private void loadBackgroundImage() {
        File file = new File("src/main/resources/com/example/dsa2_ca2/map.png");
        backgroundImage = new Image(file.toURI().toString());

        mapCanvas.setWidth(backgroundImage.getWidth());
        mapCanvas.setHeight(backgroundImage.getHeight());
        graphicsContext = mapCanvas.getGraphicsContext2D();

        graphicsContext.drawImage(backgroundImage, 0, 0);
    }

    private void setupEventHandlers() {
        pointSelectionHandler = mouseEvent -> {
            int x = (int) mouseEvent.getX();
            int y = (int) mouseEvent.getY();

            System.out.println("Clicked: " + x + "," + y);

            if (startPoint == null) {
                startPoint = new PixelBFS.Point(x, y);
            } else if (endPoint == null) {
                endPoint = new PixelBFS.Point(x, y);
                mapCanvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, pointSelectionHandler);
            }
        };
    }

    @FXML
    private void choosePoints() {
        startPoint = null;
        endPoint = null;

        mapCanvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, pointSelectionHandler);
        mapCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, pointSelectionHandler);

        /*mapCanvas.setOnMouseClicked(mouseEvent -> {
            int x = (int) mouseEvent.getX();
            int y = (int) mouseEvent.getY();

            System.out.println("Clicked: " + x + "," + y);

            if (startPoint == null || endPoint != null) {
                startPoint = new PixelBFS.Point(x, y);
                endPoint = null;
            } else {
                endPoint = new PixelBFS.Point(x, y);
            }


        });*/
    }


    // BFS

    @FXML
    public void onBFS() {
        int startID = chooseStartRoom();
        if (startID == -1) return;
        int endID = chooseEndRoom();

        MyList<Room> path = BFS.traverse(graph, startID, endID);

        drawPath(path);
    }

    @FXML
    public void onPixelBFS() {
        try {
            BufferedImage bwImage = ImageIO.read(
                    new File("src/main/resources/com/example/dsa2_ca2/map(BW).png")
            );

            if (startPoint == null || endPoint == null) return;

            PixelBFS.Point start = new PixelBFS.Point(startPoint.x(), startPoint.y());
            PixelBFS.Point end = new PixelBFS.Point(endPoint.x(), endPoint.y());

            MyList<PixelBFS.Point> path = PixelBFS.traverse(bwImage, start, end);

            Label header = new Label("Length of path: " + path.size());
            imageContainer.getChildren().add(header);

            redrawMap();
            drawBFSPath(path);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void redrawMap() {
        graphicsContext.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        graphicsContext.drawImage(backgroundImage, 0, 0);
    }

    private void drawBFSPath(MyList<PixelBFS.Point> path) {
        redrawMap();

        PixelBFS.Point start = path.get(0);
        PixelBFS.Point end = path.get(path.size() - 1);

        // draw start point
        graphicsContext.setFill(Color.LIME);
        graphicsContext.fillOval(start.x() - 5, start.y() - 5, 10, 10);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeOval(start.x() - 5, start.y() - 5, 10, 10);

        // draw end point
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(end.x() - 5, end.y() - 5, 10, 10);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeOval(end.x() - 5, end.y() - 5, 10, 10);

        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setLineWidth(2.0);

        Timeline timeline = new Timeline();
        double interval = 2000.0 / path.size(); // ms

        for (int i = 0; i < path.size() - 1; i++) {

            int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * interval), _ -> {
                PixelBFS.Point a = path.get(index);
                PixelBFS.Point b = path.get(index + 1);
                graphicsContext.strokeLine(a.x(), a.y(), b.x(), b.y());
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();

    }


    // DFS

    @FXML
    private ListView<String> routesListView;

    @FXML
    public void onDFS() {
        int startID = chooseStartRoom();
        if (startID == -1) return;
        int endID = chooseEndRoom();

        int numPermutations = numberDialog("Enter number of route permutations:");

        Set<Room> avoid = new HashSet<>();
        boolean wantsToAvoid = showConfirmationDialog("Avoid Rooms", "Do you want to exclude any rooms from the route?");
        if (wantsToAvoid) avoid = showRoomAvoidanceDialog();

        Set<Room> waypoint = new HashSet<>();
        boolean wantsToVisit = showConfirmationDialog("Waypoint Rooms", "Do you want to include any rooms in the route?");
        if (wantsToVisit) {
            waypoint = showWaypointDialog();
            MyList<MyList<Room>> pathWaypoint = DFS.traverse(graph, startID, endID, numPermutations,  avoid, waypoint);
            if (pathWaypoint.isEmpty()) return;
            animateDFSRecursion(pathWaypoint, 0);
            return;
        }

        MyList<MyList<Room>> allPaths = DFS.traverse(graph, startID, endID, numPermutations, avoid, null);

        System.out.println("Total number of routes = " + allPaths.size());


        animateDFSRecursion(allPaths, 0);
        viewRoutes(allPaths);
    }

    @FXML
    private int chooseStartRoom() {
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

    @FXML
    private int chooseEndRoom() {
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

    // DRAW ROUTES
    @FXML
    private void drawPath(MyList<Room> path) {
        redrawMap();

        Room start = path.get(0);
        Room end = path.get(path.size() - 1);

        // draw start point
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(start.getX() - 5, start.getY() - 5, 10, 10);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeOval(start.getX() - 5, start.getY() - 5, 10, 10);

        // draw end point
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(end.getX() - 5, end.getY() - 5, 10, 10);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeOval(end.getX() - 5, end.getY() - 5, 10, 10);

        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setLineWidth(2.0);

        Timeline timeline = new Timeline();
        double interval = 2000.0 / path.size(); // ms

        for (int i = 0; i < path.size() - 1; i++) {

            int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * interval), _ -> {
                Room a = path.get(index);
                Room b = path.get(index + 1);
                graphicsContext.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();

    }

    private void animateDFSRecursion(MyList<MyList<Room>> allPaths, int currPath) {
        redrawMap();

        // TODO maybe implement diff colour for routes
        Color[] colors = {
                Color.rgb(255, 0, 0),
                Color.rgb(0, 0, 255),
                Color.rgb(0, 255, 0),
                Color.rgb(255, 165, 0),
                Color.rgb(128, 0, 128),
                Color.rgb(255, 192, 203),
                Color.rgb(0, 255, 255),
                Color.rgb(255, 255, 0)
        };


        // base case
        if (currPath >= allPaths.size()) return;

        MyList<Room> path = allPaths.get(currPath);
        redrawMap();
        drawMarkers(path);

        Timeline timeline = new Timeline();

        double interval = 2000.0 / (path.size());

        for (int i = 0; i < path.size() - 1; i++) {
            int index = i;

            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * interval), _ -> {
                Room a = path.get(index);
                Room b = path.get(index + 1);
                graphicsContext.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        // recursion + pause
        timeline.setOnFinished(event -> {
            PauseTransition pause = new PauseTransition(Duration.millis(1000));
            pause.setOnFinished(_ -> animateDFSRecursion(allPaths, currPath + 1));
            pause.play();
        });

        timeline.play();

    }

    private void drawMarkers(MyList<Room> path) {
        if (path == null || path.isEmpty()) return;

        Room start = path.get(0);
        Room end = path.get(path.size() - 1);

        // Draw start point (green for start)
        graphicsContext.setFill(Color.LIME);
        graphicsContext.fillOval(start.getX() - 5, start.getY() - 5, 10, 10);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeOval(start.getX() - 5, start.getY() - 5, 10, 10);

        // Draw end point (red for destination)
        graphicsContext.setFill(Color.RED);
        graphicsContext.fillOval(end.getX() - 5, end.getY() - 5, 10, 10);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeOval(end.getX() - 5, end.getY() - 5, 10, 10);

        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setLineWidth(2.0);
    }

    private MyList<String> getAllRoomName() {
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

    private Set<String> getAllArtistNames() {
        Map<String, MyList<Integer>> artistRoomMap = getArtistRoomMap();

        return new LinkedHashSet<>(artistRoomMap.keySet());
    }

    private Map<String, MyList<Integer>> getArtistRoomMap() {
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

    @FXML
    private String showComboDialog(ComboBox<String> comboBox, Dialog<String> dialog) {
        dialog.getDialogPane().setContent(comboBox);

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(ok, cancel);

        dialog.setResultConverter(button ->
                button == ok ? comboBox.getValue() : null
        );

        return dialog.showAndWait().orElse(null);
    }

    @FXML
    private Integer numberDialog(String dialogTitle) {

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);
        dialog.setHeaderText("Enter Number of Permutations:");

        Spinner<Integer> spinner = new Spinner<>(1, 100, 1);
        spinner.setEditable(false);

        dialog.getDialogPane().setContent(spinner);

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, cancel);

        dialog.setResultConverter(button -> button == ok ? spinner.getValue() : null);

        return dialog.showAndWait().orElse(null);
    }

    @FXML
    private void onDijkstra() {
        int startID = chooseStartRoom();
        if (startID == -1) return;
        int endID = chooseEndRoom();

        Set<Room> avoid = new HashSet<>();
        boolean wantsToAvoid = showConfirmationDialog("Avoid Rooms", "Do you want to exclude any rooms from the route?");
        if (wantsToAvoid) avoid = showRoomAvoidanceDialog();

        Set<Room> waypoint;
        boolean wantsToVisit = showConfirmationDialog("Waypoint Rooms", "Do you want to include any rooms in the route?");
        if (wantsToVisit) {
            waypoint = showWaypointDialog();
            MyList<Room> pathWaypoint = Dijkstra.traverseWaypoints(graph, startID, endID, avoid, extractIDs(waypoint));
            if (pathWaypoint.isEmpty()) return;
            drawPath(pathWaypoint);
            return;
        }

        MyList<Room> path = Dijkstra.traverse(graph, startID, endID, avoid);
        if (path.isEmpty()) return;

        drawPath(path);
    }

    @FXML
    private void onDijkstraInteresting() {
        int startID = chooseStartRoom();
        if (startID == -1) return;
        int endID = chooseEndRoom();

        Set<Room> avoid = new HashSet<>();
        boolean wantsToAvoid = showConfirmationDialog("Avoid Rooms", "Do you want to exclude any rooms from the route?");
        if (wantsToAvoid) avoid = showRoomAvoidanceDialog();

        Set<Room> waypoints = new HashSet<>();
        boolean wantsToVisit = showConfirmationDialog("Waypoint Rooms", "Do you want to include any rooms in the route?");
        if (wantsToVisit) waypoints = showWaypointDialog();

        boolean wantsArtists = showConfirmationDialog("Favorite Artists", "Do you want to include rooms with specific artists?");
        if (wantsArtists) {
            Set<String> selectedArtists = showArtistsDialog();
            Map<String, MyList<Integer>> artistRoomMap = getArtistRoomMap();

            for (String name : selectedArtists) {
                MyList<Integer> roomIDs = artistRoomMap.get(name);
                for (int i = 0; i < roomIDs.size(); i++) {
                    int roomID = roomIDs.get(i);

                    Room room = graph.getVertex(roomID).getData();
                    if (room != null && !avoid.contains(room)) waypoints.add(room);
                }
            }
        }

        if (!waypoints.isEmpty()) {
            MyList<Room> pathWaypoint = Dijkstra.traverseWaypoints(graph, startID, endID, avoid, extractIDs(waypoints));
            if (pathWaypoint.isEmpty()) return;
            drawPath(pathWaypoint);
            return;
        }

        MyList<Room> path = Dijkstra.traverse(graph, startID, endID, avoid);
        if (path.isEmpty()) return;

        drawPath(path);

    }


    @FXML
    private Set<String> showArtistsDialog() {
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

    @FXML
    private MyList<Integer> extractIDs(Set<Room> rooms) {
        MyList<Integer> result = new MyArrayList<>();

        for (Room room : rooms) result.add(room.getId());

        return result;
    }

    private Set<Room> showRoomAvoidanceDialog() {
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

    private Set<Room> showWaypointDialog() {
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

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    @FXML
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

    private String printRoutes(MyList<MyList<Room>> allPaths) {
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
