package com.example.dsa2_ca2.model;

import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.loader.CSVLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ExhibitTest {

    private Graph<Room> graph;
    private Room room18; // 18,"Peter Paul Rubens (1577–1640)","Baroque",316,109
    private Exhibit exhibit; // 18,"Drunken Silenus supported by Satyrs","Anthony van Dyck"

    @BeforeEach
    void setup() throws IOException {
        CSVLoader loader = new CSVLoader();

        graph = loader.loadGraph(
                "src/main/resources/com/example/dsa2_ca2/rooms.csv",
                "src/main/resources/com/example/dsa2_ca2/exhibits.csv",
                "src/main/resources/com/example/dsa2_ca2/edges.csv"
        );

        room18 = graph.getVertex(18).getData();
        MyList<Exhibit> exhibits = room18.getExhibits();
        exhibit = exhibits.get(0);
    }

    @Test
    void getTitle() {
        assertEquals("Drunken Silenus supported by Satyrs", exhibit.getTitle());
    }

    @Test
    void getArtist() {
        assertEquals("Anthony van Dyck", exhibit.getArtist());
    }

    @Test
    void setTitle() {
        exhibit.setTitle("New Title");
        assertEquals("New Title", exhibit.getTitle());
    }

    @Test
    void setArtist() {
        exhibit.setArtist("New Artist");
        assertEquals("New Artist", exhibit.getArtist());
    }
}