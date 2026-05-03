package com.example.dsa2_ca2.utils;

import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;
import com.example.dsa2_ca2.traversal.PixelBFS;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Objects;

public class DrawingUtils {

    private final Canvas mapCanvas;
    private final GraphicsContext graphicsContext;
    private final Image backgroundImage;

    public DrawingUtils(Canvas mapCanvas, Image backgroundImage) {
        this.mapCanvas = mapCanvas;
        this.graphicsContext = mapCanvas.getGraphicsContext2D();
        this.backgroundImage = backgroundImage;
    }

    public void redrawMap() {
        graphicsContext.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        graphicsContext.drawImage(backgroundImage, 0, 0);
    }

    // used in BFS, DIJKSTRA
    public void drawPath(MyList<Room> path) {
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
    
    
    // draw Pixel BFS
    public void drawBFSPath(MyList<PixelBFS.Point> path) {
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

    // draws DFS
    public void animateDFSRecursion(MyList<MyList<Room>> allPaths, int currPath) {
        redrawMap();

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


    public HBox buildPathView(MyList<Room> path) {

        HBox box = new HBox();
        box.setSpacing(15);

        for (Room room : path) {

            ImageView imageView = new ImageView(getRoomImage(room));
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(false);

            // Label title = new Label(room.getId() + " - " + room.getName());
            Label title = new Label("Room " + room.getId());

            VBox item = new VBox(imageView, title);
            item.setSpacing(5);

            box.getChildren().add(item);
        }

        return box;
    }

    public static Image getRoomImage(Room room) {

        String path = "/com/example/dsa2_ca2/roomImages/room" + room.getId() + ".png";

        return new Image(Objects.requireNonNull(DrawingUtils.class.getResourceAsStream(path)));
    }

}
