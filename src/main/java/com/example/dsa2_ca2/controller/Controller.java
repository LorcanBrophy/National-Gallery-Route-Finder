package com.example.dsa2_ca2.controller;

import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.traversal.*;
import com.example.dsa2_ca2.utils.CSVLoader;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.utils.DialogUtils;
import com.example.dsa2_ca2.model.Exhibit;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;
import com.example.dsa2_ca2.utils.DrawingUtils;
import com.example.dsa2_ca2.utils.Utils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Controller {
    @FXML private Canvas mapCanvas;
    @FXML private VBox imageContainer;
    private GraphicsContext graphicsContext;
    private Image backgroundImage;

    private PixelBFS.Point startPoint = null;
    private PixelBFS.Point endPoint = null;

    private Graph<Room> graph;

    private EventHandler<MouseEvent> pointSelectionHandler;

    private DrawingUtils drawingUtils;
    private DialogUtils dialogUtils;
    private Utils utils;


    // setup

    @FXML
    public void initialize() throws IOException {
        buildGraph();
        loadBackgroundImage();
        setupEventHandlers();
        
        drawingUtils = new DrawingUtils(mapCanvas, backgroundImage);
        dialogUtils = new DialogUtils(graph);
        utils = new Utils(imageContainer);


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
        File file = new File("src/main/resources/com/example/dsa2_ca2/mapImages/map.png");
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

    public void choosePoints() {
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

    public void displayPathImages(MyList<Room> path) {

        HBox pathBox = drawingUtils.buildPathView(path);

        ScrollPane scrollPane = new ScrollPane(pathBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Stage stage = new Stage();

        Scene scene = new Scene(scrollPane, 800, 250);
        stage.setScene(scene);

        stage.show();
    }

    // update csv

    @FXML
    public void onAddRoom() {
        Room room = dialogUtils.createRoomDialog();
        if (room == null) return;

        graph.addVertex(room.getId(), room);
    }

    @FXML
    public void onAddExhibit() {
        Object[] exhibitFields = dialogUtils.createExhibitDialog();
        int roomID = (int) exhibitFields[0];
        String title = (String) exhibitFields[1];
        String artist = (String) exhibitFields[2];

        Room room = graph.getVertex(roomID).getData();
        if (room == null) return;

        Exhibit exhibit = new Exhibit(title, artist);

        room.getExhibits().add(exhibit);
    }

    @FXML
    public void onAddEdge() {
        Object[] edgeFields = dialogUtils.createEdgeDialog();
        if (edgeFields == null) return;

        int srcID = (int) edgeFields[0];
        int destID = (int) edgeFields[1];
        double weight = (double) edgeFields[2];

        Vertex<Room> source = graph.getVertex(srcID);
        Vertex<Room> dest = graph.getVertex(destID);
        if (source == null || dest == null) return;


        source.connectUndirected(dest, weight);
    }


    // BFS

    @FXML
    public void onBFS() {
        utils.removePathLabel();

        int startID = dialogUtils.chooseStartRoomDialog();
        if (startID == -1) return;
        int endID = dialogUtils.chooseEndRoomDialog();
        if (endID == -1) return;

        MyList<Room> path = BFS.traverse(graph, startID, endID);

        drawingUtils.drawPath(path);
        displayPathImages(path);
    }

    @FXML
    public void onPixelBFS() {
        try {

            utils.removePathLabel();

            BufferedImage bwImage = ImageIO.read(
                    new File("src/main/resources/com/example/dsa2_ca2/mapImages/map(BW).png")
            );

            if (startPoint == null || endPoint == null) return;

            PixelBFS.Point start = new PixelBFS.Point(startPoint.x(), startPoint.y());
            PixelBFS.Point end = new PixelBFS.Point(endPoint.x(), endPoint.y());

            MyList<PixelBFS.Point> path = PixelBFS.traverse(bwImage, start, end);

            utils.setPathLabel("Length of path: " + path.size());

            drawingUtils.redrawMap();
            drawingUtils.drawBFSPath(path);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // DFS

    @FXML
    public void onDFS() {
        utils.removePathLabel();

        int startID = dialogUtils.chooseStartRoomDialog();
        if (startID == -1) return;
        int endID = dialogUtils.chooseEndRoomDialog();
        if (endID == -1) return;

        int numPermutations = dialogUtils.numberDialog("Total Permutations", "Enter Number of Permutations:");

        Set<Room> avoid = new HashSet<>();
        boolean wantsToAvoid = dialogUtils.showConfirmationDialog("Avoid Rooms", "Do you want to exclude any rooms from the route?");
        if (wantsToAvoid) avoid = dialogUtils.showRoomAvoidanceDialog();

        Set<Room> waypoint = new HashSet<>();
        boolean wantsToVisit = dialogUtils.showConfirmationDialog("Waypoint Rooms", "Do you want to include any rooms in the route?");
        if (wantsToVisit) {
            waypoint = dialogUtils.showWaypointDialog();
            MyList<MyList<Room>> pathWaypoint = RecursiveDFS.traverse(graph, startID, endID, numPermutations,  avoid, waypoint);
            if (pathWaypoint.isEmpty()) return;
            drawingUtils.animateDFSRecursion(pathWaypoint, 0);
            return;
        }

        MyList<MyList<Room>> allPaths = RecursiveDFS.traverse(graph, startID, endID, numPermutations, avoid, waypoint);
        MyList<MyList<Room>> allPaths2 = IterativeDFS.traverse(graph, startID, endID, numPermutations);

        drawingUtils.animateDFSRecursion(allPaths, 0);
        utils.viewRoutes(allPaths);
    }


    // dijkstra

    @FXML
    private void onDijkstra() {
        utils.removePathLabel();

        int startID = dialogUtils.chooseStartRoomDialog();
        if (startID == -1) return;
        int endID = dialogUtils.chooseEndRoomDialog();
        if (endID == -1) return;

        Set<Room> avoid = new HashSet<>();
        boolean wantsToAvoid = dialogUtils.showConfirmationDialog("Avoid Rooms", "Do you want to exclude any rooms from the route?");
        if (wantsToAvoid) avoid = dialogUtils.showRoomAvoidanceDialog();

        Set<Room> waypoint;
        boolean wantsToVisit = dialogUtils.showConfirmationDialog("Waypoint Rooms", "Do you want to include any rooms in the route?");
        if (wantsToVisit) {
            waypoint = dialogUtils.showWaypointDialog();
            MyList<Room> pathWaypoint = Dijkstra.traverseWaypoints(graph, startID, endID, avoid, utils.extractIDs(waypoint));
            if (pathWaypoint.isEmpty()) return;
            drawingUtils.drawPath(pathWaypoint);
            displayPathImages(pathWaypoint);
            return;
        }

        MyList<Room> path = Dijkstra.traverse(graph, startID, endID, avoid);
        if (path.isEmpty()) return;

        drawingUtils.drawPath(path);
        displayPathImages(path);
    }

    @FXML
    private void onDijkstraInteresting() {
        utils.removePathLabel();

        int startID = dialogUtils.chooseStartRoomDialog();
        if (startID == -1) return;
        int endID = dialogUtils.chooseEndRoomDialog();
        if (endID == -1) return;

        Set<Room> avoid = new HashSet<>();
        boolean wantsToAvoid = dialogUtils.showConfirmationDialog("Avoid Rooms", "Do you want to exclude any rooms from the route?");
        if (wantsToAvoid) avoid = dialogUtils.showRoomAvoidanceDialog();

        Set<Room> waypoints = new HashSet<>();
        boolean wantsToVisit = dialogUtils.showConfirmationDialog("Waypoint Rooms", "Do you want to include any rooms in the route?");
        if (wantsToVisit) waypoints = dialogUtils.showWaypointDialog();

        boolean wantsArtists = dialogUtils.showConfirmationDialog("Favorite Artists", "Do you want to include rooms with specific artists?");
        if (wantsArtists) {
            Set<String> selectedArtists = dialogUtils.showArtistsDialog();
            Map<String, MyList<Integer>> artistRoomMap = dialogUtils.getArtistRoomMap();

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
            MyList<Room> pathWaypoint = Dijkstra.traverseWaypoints(graph, startID, endID, avoid, utils.extractIDs(waypoints));
            if (pathWaypoint.isEmpty()) return;
            drawingUtils.drawPath(pathWaypoint);
            displayPathImages(pathWaypoint);
            return;
        }

        MyList<Room> path = Dijkstra.traverse(graph, startID, endID, avoid);
        if (path.isEmpty()) return;

        drawingUtils.drawPath(path);
        displayPathImages(path);

    }

}
