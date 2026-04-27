package com.example.dsa2_ca2.traversal;

import com.example.dsa2_ca2.graph.Edge;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;
import com.example.dsa2_ca2.model.Room;

import java.util.*;

public class DFS {

    public static <T> MyList<MyList<T>> traverse(Graph<T> graph, int startID, int endID, int numPermutations, Set<T> avoidedRooms, Set<T> waypoints) {

        MyList<MyList<T>> allPaths = new MyArrayList<>();

        Vertex<T> start = graph.getVertex(startID);
        Vertex<T> end = graph.getVertex(endID);
        if (start == null || end == null) return allPaths;


        Set<Vertex<T>> visited = new HashSet<>();
        MyList<Vertex<T>> currentPath = new MyArrayList<>();
        visited.add(start);
        currentPath.add(start);

        recursiveDFS(start, end, visited, currentPath, allPaths, numPermutations, avoidedRooms, waypoints);

        return allPaths;
    }

    private static <T> void recursiveDFS(Vertex<T> current, Vertex<T> end, Set<Vertex<T>> visited,
                                         MyList<Vertex<T>> currentPath, MyList<MyList<T>> allPaths, int numPermutations,
                                         Set<T> avoidedRooms, Set<T> waypoints) {

        // enough permutations found
        if (allPaths.size() >= numPermutations) return;

        if (current == end && allWaypointsVisited(currentPath, waypoints)) {
            MyList<T> path = new MyArrayList<>();
            for (Vertex<T> vertex : currentPath) {
                path.add(vertex.getData());
            }
            allPaths.add(path);
            return;
        }

        for (Edge<T> edge : current.getEdges()) {
            Vertex<T> neighbour = edge.getDestination();

            if (!visited.contains(neighbour) && !avoidedRooms.contains(neighbour.getData())) {

                // move to neighbour
                visited.add(neighbour);
                currentPath.add(neighbour);

                // recursion
                recursiveDFS(neighbour, end, visited, currentPath, allPaths, numPermutations, avoidedRooms, waypoints);

                // backtrack
                currentPath.remove(currentPath.size() - 1);
                visited.remove(neighbour);

                // enough permutations found
                if (allPaths.size() >= numPermutations) return;
            }
        }

    }

    private static <T> boolean allWaypointsVisited(MyList<Vertex<T>> path, Set<T> waypoints) {
        if (waypoints.isEmpty() || waypoints == null) return true;

        Set<T> foundWaypoints = new HashSet<>();
        for (int i = 0; i < path.size(); i++) {
            T data = path.get(i).getData();
            if (waypoints.contains(data)) foundWaypoints.add(data);
        }
        return foundWaypoints.containsAll(waypoints);
    }
}
