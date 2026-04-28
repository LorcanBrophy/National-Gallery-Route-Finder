package com.example.dsa2_ca2.utils;

import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.model.Exhibit;
import com.example.dsa2_ca2.model.Room;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVLoader {

    public Graph<Room> loadGraph(String roomsFile, String exhibitsFile, String edgesFile) throws IOException {

        Graph<Room> graph = new Graph<>();

        loadRooms(graph, roomsFile);
        loadExhibits(graph, exhibitsFile);
        loadEdges(graph, edgesFile);

        return graph;
    }

    private void loadRooms(Graph<Room> graph, String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            // skip first line: id,name,period
            String line = br.readLine();

            while ((line = br.readLine()) != null) {

                // split csv:
                // 21,"Anthony van Dyck (1599–1641)","Baroque"
                // -> [21, "Anthony van Dyck (1599–1641)", "Baroque"]
                String[] fields = line.split(",", 5);

                // assign each field for Room constructor
                int id = Integer.parseInt(fields[0].trim());
                String name = fields[1].replace("\"", "").trim(); // \" removes any " characters i.e ""Hello Hassan"" -> "Hello Hassan"
                String period = fields[2].replace("\"", "").trim();
                int x = Integer.parseInt(fields[3].trim());
                int y = Integer.parseInt(fields[4].trim());

                // create the room object and add to graph
                Room room = new Room(id, name, period, x, y);
                graph.addVertex(id, room);
            }
        }
    }

    private void loadExhibits(Graph<Room> graph, String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine();

            while ((line = br.readLine()) != null) {

                String[] fields = line.split(",", 3);

                int roomID = Integer.parseInt(fields[0].trim());
                String title = fields[1].replace("\"", "").trim();
                String artist = fields[2].replace("\"", "").trim();

                // create room which the exhibit belongs to
                Room room = graph.getVertex(roomID).getData();

                room.getExhibits().add(new Exhibit(title, artist));
            }
        }
    }

    private void loadEdges(Graph<Room> graph, String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line = br.readLine();

            while ((line = br.readLine()) != null) {

                String[] fields = line.split(",", 3);

                int src = Integer.parseInt(fields[0].trim());
                int dest = Integer.parseInt(fields[1].trim());
                double weight = Double.parseDouble(fields[2].trim());

                graph.addUndirectedEdge(src, dest, weight);
            }
        }
    }
}
