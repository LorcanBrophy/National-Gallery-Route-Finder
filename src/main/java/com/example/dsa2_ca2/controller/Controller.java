package com.example.dsa2_ca2.controller;

import com.example.dsa2_ca2.loader.CSVLoader;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;
import com.example.dsa2_ca2.traversal.BFS;
import com.example.dsa2_ca2.traversal.PixelBFS;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML private Canvas mapCanvas;
    @FXML private HBox imageContainer;
    private GraphicsContext graphicsContext;
    private Image backgroundImage;

    PixelBFS.Point startPoint = null;
    PixelBFS.Point endPoint = null;

    private Graph<Room> graph;

    private EventHandler<MouseEvent> pointSelectionHandler;

    @FXML public void initialize() throws IOException {
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

    @FXML private void loadBackgroundImage() {
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

    @FXML private void choosePoints() {
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





    @FXML public void onBFS() {
        int startID = 21;
        int endID = 25;

        MyList<Room> path = BFS.traverse(graph, startID, endID);

        System.out.println("Distance from room " + startID + " to room " + endID + " = " + path.size());
    }

    @FXML public void onPixelBFS() {
        try {
            BufferedImage bwImage = ImageIO.read(
                    new File("src/main/resources/com/example/dsa2_ca2/map(BW).png")
            );

            PixelBFS.Point start = new PixelBFS.Point(startPoint.x(), startPoint.y());
            PixelBFS.Point end = new PixelBFS.Point(endPoint.x(), endPoint.y());

            MyList<PixelBFS.Point> path = PixelBFS.traverse(bwImage, start, end);

            System.out.println("Pixel path length: " + path.size());

            redrawMap();
            drawPath(path);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void redrawMap() {
        graphicsContext.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        graphicsContext.drawImage(backgroundImage, 0, 0);
    }

    private void drawPath(MyList<PixelBFS.Point> path) {
        PixelBFS.Point start = path.get(0);
        PixelBFS.Point end = path.get(path.size() - 1);

        // draw start point
        graphicsContext.setFill(Color.RED);
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
}
