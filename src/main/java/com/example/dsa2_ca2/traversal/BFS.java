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

        // final correct path to be returned
        MyList<T> correctPath = new MyArrayList<>();

        // initial node and target node
        Vertex<T> start = graph.getVertex(startID);
        Vertex<T> end = graph.getVertex(endID);
        if (start == null || end == null) return correctPath;

        // tracks visited nodes, can do if (!visited.contains(current)) to see if node is visited
        Set<Vertex<T>> visited = new HashSet<>();
        visited.add(start);

        // queue to process neighbours
        Queue<Vertex<T>> queue = new LinkedList<>();
        queue.add(start);

        // stores the parents, used to reconstruct path later
        // key = child, value = parent
        Map<Vertex<T>, Vertex<T>> parent = new HashMap<>();
        parent.put(start, null);

        // flag to know if end has been found
        boolean found = false;

        while (!queue.isEmpty()) {

            // .poll() remove and returns the first element in the queue
            // queue = [A, B, C]
            // current = queue.poll()
            // queue = [B, C]
            // current = A
            Vertex<T> current = queue.poll();

            if (current == end) {
                found = true;
                break;
            }

            // iterate through the unvisited neighbours of current node
            for (Edge<T> edge : current.getEdges()) {
                Vertex<T> neighbour = edge.getDestination();

                // if the node is unvisited, add it to the queue
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                    parent.put(neighbour, current);
                }
            }
        }

        if (!found) return correctPath;

        // now that the target was found, the path can be reconstructed, starting from the end
        MyList<Vertex<T>> reversePath = new MyArrayList<>();

        // parent = [null:A, A:B, B:C, D:E]
        // iterate until current = null, at the start, and add current to reverseList
        Vertex<T> current = end;
        while (current != null) {
            reversePath.add(current);
            current = parent.get(current);
        }

        // reversePath = [E, D, C, B, A]
        // now need reverse it
        for (int i = reversePath.size() - 1; i >= 0; i--) {
            correctPath.add(reversePath.get(i).getData());
        }

        return correctPath;
    }
}
