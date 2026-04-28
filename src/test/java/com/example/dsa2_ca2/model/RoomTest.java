package com.example.dsa2_ca2.model;

import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.utils.CSVLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {
    private Graph<Room> graph;
    private Room room18; // 18,"Peter Paul Rubens (1577–1640)","Baroque",316,109


    @BeforeEach
    void setup() throws IOException {
        CSVLoader loader = new CSVLoader();

        graph = loader.loadGraph(
                "src/main/resources/com/example/dsa2_ca2/rooms.csv",
                "src/main/resources/com/example/dsa2_ca2/exhibits.csv",
                "src/main/resources/com/example/dsa2_ca2/edges.csv"
        );

        room18 = graph.getVertex(18).getData();

    }

    @Test
    void getId() {
        assertEquals(18, room18.getId());
    }

    @Test
    void getName() {
        assertEquals("Peter Paul Rubens (1577–1640)", room18.getName());
    }

    @Test
    void getPeriod() {
        assertEquals("Baroque", room18.getPeriod());
    }

    @Test
    void getX() {
        assertEquals(316, room18.getX());
    }

    @Test
    void getY() {
        assertEquals(109, room18.getY());
    }

    @Test
    void getExhibits() {
        MyList<Exhibit> exhibits = room18.getExhibits();
        assertFalse(exhibits.isEmpty());
    }
}