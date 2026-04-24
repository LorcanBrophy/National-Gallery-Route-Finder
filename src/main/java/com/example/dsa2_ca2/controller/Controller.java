package com.example.dsa2_ca2.controller;

import com.example.dsa2_ca2.loader.CSVLoader;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;
import com.example.dsa2_ca2.traversal.BFS;
import com.example.dsa2_ca2.traversal.PixelBFS;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
    public ImageView galleryMap;
    public HBox imageContainer;
    private Image image;

    @FXML
    private Canvas canvas;


    private Graph<Room> graph;
    private GraphicsContext graphicsContext;
    @FXML
    public void initialize() throws IOException {
        buildGraph();

        File file = new File("src/main/resources/com/example/dsa2_ca2/map.png");
        image = new Image(file.toURI().toString());

        galleryMap.setImage(image);
        imageContainer.getChildren().setAll(galleryMap);

        canvas = new Canvas(image.getWidth(), image.getHeight());
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(image, 0, 0);

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
    public void onBFS() {
        int startID = 21;
        int endID = 25;

        MyList<Room> path = BFS.traverse(graph, startID, endID);

        System.out.println("Distance from room " + startID + " to room " + endID + " = " + path.size());
    }

    PixelBFS.Point startPoint;
    PixelBFS.Point endPoint;

    @FXML
    public void choosePoints() {
        canvas.setOnMouseClicked(mouseEvent -> {
            int x = (int) mouseEvent.getX();
            int y = (int) mouseEvent.getY();

            System.out.println("Clicked: " + x + "," + y);

            if (startPoint == null) {
                startPoint = new PixelBFS.Point(x, y);
            } else {
                endPoint = new PixelBFS.Point(x, y);
            }


        });
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

            imageContainer.getChildren().setAll(canvas);

            graphicsContext.setStroke(Color.RED);
            graphicsContext.setLineWidth(2.0);

            for (int i = 0; i < path.size() - 1; i++) {
                PixelBFS.Point a = path.get(i);
                PixelBFS.Point b = path.get(i + 1);

                graphicsContext.strokeLine(a.x(), a.y(), b.x(), b.y());
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
