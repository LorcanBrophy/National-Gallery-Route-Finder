package com.example.dsa2_ca2.benchmark;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.utils.CSVLoader;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;
import com.example.dsa2_ca2.traversal.BFS;
import com.example.dsa2_ca2.traversal.DFS;
import com.example.dsa2_ca2.traversal.Dijkstra;
import org.openjdk.jmh.annotations.*;



@Measurement(iterations = 1, time = 1)
@Warmup(iterations = 2, time = 1)
@Fork(value = 1)

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)

public class MyBenchmark {
    private Graph<Room> graph;
    private int startID;
    private int endID;

    private final MyList<Integer> waypoints = new MyArrayList<>();
    private final Set<Room> avoid = new HashSet<>();
    private final Set<Room> waypoints2 = new HashSet<>();

    @Setup(Level.Invocation)
    public void setup() throws IOException {
        CSVLoader loader = new CSVLoader();

        // init graph
        graph = loader.loadGraph(
                "src/main/resources/com/example/dsa2_ca2/rooms.csv",
                "src/main/resources/com/example/dsa2_ca2/exhibits.csv",
                "src/main/resources/com/example/dsa2_ca2/edges.csv"
        );

        startID = (int) (40 * Math.random());
        endID = (int) (40 * Math.random());
        }


    @Benchmark
    public void runBFS() {
        BFS.traverse(graph, startID, endID);
    }

    @Benchmark
    public void runDFS() {
        DFS.traverse(graph, startID, endID, 10, avoid, waypoints2);
    }

    @Benchmark
    public void runDijkstra() {
        Dijkstra.traverse(graph, startID, endID, avoid);
    }

    @Benchmark
    public void runDijkstraInteresting() {
        Dijkstra.traverseWaypoints(graph, startID, endID, avoid, waypoints);
    }


}
