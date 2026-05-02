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

        // list of path permutations
        MyList<MyList<T>> allPaths = new MyArrayList<>();

        // initial node and target node
        Vertex<T> start = graph.getVertex(startID);
        Vertex<T> end = graph.getVertex(endID);
        if (start == null || end == null) return allPaths;

        // tracks visited nodes, can do if (!visited.contains(current)) to see if node is visited
        Set<Vertex<T>> visited = new HashSet<>();
        visited.add(start);

        // used to track the current path, will be added to allPaths once end is found for each permutation
        MyList<Vertex<T>> currentPath = new MyArrayList<>();
        currentPath.add(start);

        // calling the function that populates allPaths
        recursiveDFS(start, end, visited, currentPath, allPaths, numPermutations, avoidedRooms, waypoints);

        return allPaths;
    }

    private static <T> void recursiveDFS(Vertex<T> current, Vertex<T> end, Set<Vertex<T>> visited,
                                         MyList<Vertex<T>> currentPath, MyList<MyList<T>> allPaths, int numPermutations,
                                         Set<T> avoidedRooms, Set<T> waypoints) {

        // enough permutations found
        if (allPaths.size() >= numPermutations) return;

        // checks for path being complete
        if (current == end && allWaypointsVisited(currentPath, waypoints)) {

            // currentPath is mutable and will change for the next path, hence make a new list
            MyList<T> path = new MyArrayList<>();

            // copy all vertices from currentPath to the new path
            for (Vertex<T> vertex : currentPath) path.add(vertex.getData());

            allPaths.add(path);
            return;
        }

        // v similar to the BFS logic
        // iterate through the unvisited neighbours of current node
        for (Edge<T> edge : current.getEdges()) {
            Vertex<T> neighbour = edge.getDestination();

            if (!visited.contains(neighbour) && !avoidedRooms.contains(neighbour.getData())) {
                visited.add(neighbour);
                currentPath.add(neighbour);

                // explore the other neighbours
                recursiveDFS(neighbour, end, visited, currentPath, allPaths, numPermutations, avoidedRooms, waypoints);

                // backtrack, remove the node from path and visited
                currentPath.remove(currentPath.size() - 1);
                visited.remove(neighbour);

                // enough permutations found
                if (allPaths.size() >= numPermutations) return;
            }
        }

    }

    // helper method that checks wheter all waypoints are visited on current path
    private static <T> boolean allWaypointsVisited(MyList<Vertex<T>> path, Set<T> waypoints) {
        if (waypoints.isEmpty() || waypoints == null) return true;

        // creates temp list to check if all waypoints are in path
        Set<T> foundWaypoints = new HashSet<>();
        for (int i = 0; i < path.size(); i++) {
            T data = path.get(i).getData();
            if (waypoints.contains(data)) foundWaypoints.add(data);
        }

        // if they are the same, return true
        return foundWaypoints.containsAll(waypoints);
    }
}
