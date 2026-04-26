package com.example.dsa2_ca2.controller;

import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.loader.CSVLoader;
import com.example.dsa2_ca2.graph.Graph;
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
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Controller {
    @FXML private Canvas mapCanvas;
    @FXML private HBox imageContainer;
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

            PixelBFS.Point start = new PixelBFS.Point(startPoint.x(), startPoint.y());
            PixelBFS.Point end = new PixelBFS.Point(endPoint.x(), endPoint.y());

            MyList<PixelBFS.Point> path = PixelBFS.traverse(bwImage, start, end);

            System.out.println("Pixel path length: " + path.size());

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

        MyList<MyList<Room>> allPaths = DFS.traverse(graph, startID, endID, numPermutations);

        System.out.println("Total number of routes = " + allPaths.size());


        animateDFSRecursion(allPaths, 0);
        viewRoutes(allPaths);
    }

    @FXML
    private int chooseStartRoom() {
        MyList<String> roomNames = getAllRoomName();

        ComboBox<String> comboBox = new ComboBox<>();

        for (String name : roomNames) {
            comboBox.getItems().add(name);
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Choose Start Room");
        dialog.setHeaderText("Select the starting room:");

        String result = showComboDialog(comboBox, dialog);
        if (result == null) return -1;

        int startID;

        String[] parts = result.split(" - ");
        startID = Integer.parseInt(parts[0]);


        System.out.println("Start room selected: " + result);
        System.out.println("Start ID: " + startID);

        return startID;

    }

    @FXML
    private int chooseEndRoom() {
        MyList<String> roomNames = getAllRoomName();

        ComboBox<String> comboBox = new ComboBox<>();

        for (String name : roomNames) {
            comboBox.getItems().add(name);
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Choose End Room");
        dialog.setHeaderText("Select the ending room:");

        String result = showComboDialog(comboBox, dialog);

        int endID;

        if (result != null) {
            String[] parts = result.split(" - ");
            endID = Integer.parseInt(parts[0]);


            System.out.println("End room selected: " + result);
            System.out.println("End ID: " + endID);

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
        //redrawMap();

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

        MyList<Room> path = Dijkstra.traverse(graph, startID, endID);
        if (path.isEmpty()) return;

        drawPath(path);

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
