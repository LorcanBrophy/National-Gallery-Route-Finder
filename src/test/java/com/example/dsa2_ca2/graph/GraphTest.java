package com.example.dsa2_ca2.graph;

import com.example.dsa2_ca2.loader.CSVLoader;
import com.example.dsa2_ca2.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private Graph<Room> graph;
    private Vertex<Room> v18;
    private Vertex<Room> v19;


    @BeforeEach
    void setup() throws IOException {

        CSVLoader loader = new CSVLoader();

        // init graph
        graph = loader.loadGraph(
                "src/main/resources/com/example/dsa2_ca2/rooms.csv",
                "src/main/resources/com/example/dsa2_ca2/exhibits.csv",
                "src/main/resources/com/example/dsa2_ca2/edges.csv"
        );

        // assign vertices
        v18 = graph.getVertex(18);
        v19 = graph.getVertex(19);
    }

    /*@Test
    void testDirectedEdge() {

        // test size
        assertEquals(2, v18.getEdges().size());
        assertEquals(2, v19.getEdges().size());

        //assertEquals("B", v18.getEdges().get(0).getdestination().getData());

        // test weight
        assertEquals(1, v18.getEdges().get(0).getWeight());

    }*/

    @Test
    void testUndirectedEdge() {

        // test size
        assertEquals(2, v18.getEdges().size()); // 18 <--> 19, 21
        assertEquals(2, v19.getEdges().size()); // 19 <--> 18, 20

        // test edges
        assertEquals(19, v18.getEdges().get(0).getdestination().getData().getId());
        assertEquals(21, v18.getEdges().get(1).getdestination().getData().getId());

        assertEquals(18, v19.getEdges().get(0).getdestination().getData().getId());

        // test weight
        assertEquals(1, v18.getEdges().get(0).getWeight());
        assertEquals(1, v19.getEdges().get(0).getWeight());
    }
}
