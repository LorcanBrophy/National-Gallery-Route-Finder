package com.example.dsa2_ca2.traversal;

import com.example.dsa2_ca2.graph.Edge;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;

import java.util.*;

public class DFS {

    // returns shortest path using BFS
    // TODO add waypoint functions
    public static <T> MyList<MyList<T>> traverse(Graph<T> graph, int startID, int endID, int numPermutations) {

        MyList<MyList<T>> allPaths = new MyArrayList<>();

        Vertex<T> start = graph.getVertex(startID);
        Vertex<T> end = graph.getVertex(endID);
        if (start == null || end == null) return allPaths;


        Set<Vertex<T>> visited = new HashSet<>();
        MyList<Vertex<T>> currentPath = new MyArrayList<>();
        visited.add(start);
        currentPath.add(start);

        recursiveDFS(start, end, visited, currentPath, allPaths, numPermutations);

       /* boolean found = false;

        // actual bfs traversal
        while (!stack.isEmpty()) {
            Vertex<T> current = stack.pop(); // returns/removes first element in queue in FIFO order

            if (current == end) {
                found = true;
                break;
            }

            for (Edge<T> edge : current.getEdges()) {
                Vertex<T> neighbour = edge.getDestination();

                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    stack.push(neighbour);
                }
            }
        }

        if (!found) return allPaths;


        // now that

        LinkedList<T> path = new LinkedList<>();

        Vertex<T> step = end;
        while (step != null) {
            path.addFirst(step.getData());
            step = parent.get(step);
        }

        allPaths.addAll(path);
        return allPaths;*/

        return allPaths;
    }

    private static <T> void recursiveDFS(Vertex<T> current, Vertex<T> end, Set<Vertex<T>> visited,
                                         MyList<Vertex<T>> currentPath, MyList<MyList<T>> allPaths, int numPermutations) {

        // enough permutations found
        if (allPaths.size() >= numPermutations) return;

        if (current == end) {
            MyList<T> path = new MyArrayList<>();
            for (Vertex<T> vertex : currentPath) {
                path.add(vertex.getData());
            }
            allPaths.add(path);
            return;
        }

        for (Edge<T> edge : current.getEdges()) {
            Vertex<T> neighbour = edge.getDestination();

            if (!visited.contains(neighbour)) {

                // move to neighbour
                visited.add(neighbour);
                currentPath.add(neighbour);

                // recursion
                recursiveDFS(neighbour, end, visited, currentPath, allPaths, numPermutations);

                // backtrack
                currentPath.remove(currentPath.size() - 1);
                visited.remove(neighbour);

                // enough permutations found
                if (allPaths.size() >= numPermutations) return;
            }
        }

    }
}
