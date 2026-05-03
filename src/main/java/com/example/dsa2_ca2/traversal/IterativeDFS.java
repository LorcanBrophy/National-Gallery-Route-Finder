package com.example.dsa2_ca2.traversal;

import com.example.dsa2_ca2.graph.Edge;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;
import javafx.util.Pair;

import java.util.*;

public class IterativeDFS {

    public static <T> MyList<MyList<T>> traverse(Graph<T> graph, int startID, int endID, int numPermutations) {

        // stores all valid paths
        MyList<MyList<T>> allPaths = new MyArrayList<>();

        // initial node and target node
        Vertex<T> start = graph.getVertex(startID);
        Vertex<T> end = graph.getVertex(endID);
        if (start == null || end == null) return allPaths;

        // contains only start vertex
        MyList<Vertex<T>> initialPath = new MyArrayList<>();
        initialPath.add(start);

        // stack keeps track of DFS state
        // Pair data structure stores 2 related objects
        // stores [current vertex, currentPath]
        Stack<Pair<Vertex<T>, MyList<Vertex<T>>>> stack = new Stack<>();
        stack.push(new Pair<>(start, initialPath));

        while (!stack.isEmpty() && allPaths.size() < numPermutations) {

            // get current DFS state
            // .pop() remove and returns the top element in the stack
            // stack = [A, B, C]
            // current = stack.pop()
            // stack = [A, B]
            // current = C
            Pair<Vertex<T>, MyList<Vertex<T>>> pair = stack.pop();
            Vertex<T> current = pair.getKey();
            MyList<Vertex<T>> path = pair.getValue();

            // store complete path if dest reached
            if (current == end) {
                MyList<T> result = new MyArrayList<>();
                for (Vertex<T> vertex : path) result.add(vertex.getData());
                allPaths.add(result);
                continue;
            }

            if (allPaths.size() >= numPermutations) break;

            // iterate through the unvisited neighbours of current node
            for (Edge<T> edge : current.getEdges()) {
                Vertex<T> neighbour = edge.getDestination();

                if (!path.contains(neighbour)) {

                    // create unmutable path
                    MyList<Vertex<T>> newPath = new MyArrayList<>();
                    newPath.addAll(path);
                    newPath.add(neighbour);

                    // push new DFS state onto stack
                    stack.push(new Pair<>(neighbour, newPath));
                }
            }
        }

        return allPaths;
    }
}
