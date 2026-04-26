package com.example.dsa2_ca2.traversal;

import com.example.dsa2_ca2.graph.Edge;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;

import java.util.*;

public class BFS {

    // returns shortest path using BFS
    public static <T> MyList<T> traverse(Graph<T> graph, int startID, int endID) {
        MyList<T> result = new MyArrayList<>();

        Vertex<T> start = graph.getVertex(startID);
        Vertex<T> end = graph.getVertex(endID);
        if (start == null || end == null) return result;


        Set<Vertex<T>> visited = new HashSet<>(); // could maybe implement a boolean[]
        Queue<Vertex<T>> queue = new LinkedList<>();
        Map<Vertex<T>, Vertex<T>> parent = new HashMap<>();

        visited.add(start);
        queue.add(start);
        parent.put(start, null);

        boolean found = false;

        // actual bfs traversal
        while (!queue.isEmpty()) {
            Vertex<T> current = queue.poll(); // returns/removes first element in queue in FIFO order

            if (current == end) {
                found = true;
                break;
            }

            for (Edge<T> edge : current.getEdges()) {
                Vertex<T> neighbour = edge.getDestination();

                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                    parent.put(neighbour, current);
                }
            }
        }

        if (!found) return result;


        // now that

        LinkedList<T> path = new LinkedList<>();

        Vertex<T> step = end;
        while (step != null) {
            path.addFirst(step.getData());
            step = parent.get(step);
        }

        result.addAll(path);
        return result;
    }
}
