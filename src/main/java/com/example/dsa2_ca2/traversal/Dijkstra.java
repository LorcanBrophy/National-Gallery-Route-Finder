package com.example.dsa2_ca2.traversal;

import com.example.dsa2_ca2.graph.Edge;
import com.example.dsa2_ca2.graph.Graph;
import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;

import java.util.*;

public class Dijkstra {

    public static <T> MyList<T> traverse(Graph<T> graph, int startID, int endID) {
        MyList<T> result = new MyArrayList<>();

        Vertex<T> start = graph.getVertex(startID);
        Vertex<T> end = graph.getVertex(endID);
        if (start == null || end == null) return result;

        MyList<Vertex<T>> vertices = graph.getAllVertices();
        int size = vertices.size();

        // map vertex -> id, so I can access cost/parent with id
        Map<Vertex<T>, Integer> map = new HashMap<>();
        for (int i = 0; i < size; i++) map.put(vertices.get(i), i);


        // set all dijkstra weights to infinite except for source
        double[] cost = new double[size];
        int[] parent = new int[size];

        Arrays.fill(cost, Double.MAX_VALUE);
        Arrays.fill(parent, -1);

        cost[map.get(start)] = 0;

        // make a min heap which removes/returns the smallest element
        PriorityQueue<double[]> heap = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        heap.offer(new double[]{map.get(start), 0});

        while (!heap.isEmpty()) {

            double[] current = heap.poll(); // current = [vertexID, weight]
            if (current[1] > cost[(int) current[0]]) continue;

            Vertex<T> currentVertex = vertices.get((int) current[0]);
            if (currentVertex == end) break;

            for (Edge<T> edge : currentVertex.getEdges()) {
                Vertex<T> neighbour = edge.getDestination();
                int neighbourID = map.get(neighbour);

                double newCost = current[1] + edge.getWeight();
                if (newCost < cost[neighbourID]) {
                    cost[neighbourID] = newCost;
                    parent[neighbourID] = (int) current[0];
                    heap.offer(new double[]{neighbourID, newCost});
                }
            }
        }

        LinkedList<T> path = new LinkedList<>();
        int curr = map.get(end);
        while (curr != -1) {
            path.addFirst(vertices.get(curr).getData());
            curr = parent[curr];
        }

        result.addAll(path);

        return result;
    }
}
