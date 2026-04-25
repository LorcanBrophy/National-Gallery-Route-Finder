package com.example.dsa2_ca2.graph;

import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;

import java.util.HashMap;
import java.util.Map;

public class Graph<T> {

    // fields
    private final Map<Integer, Vertex<T>> vertices;

    // constructor
    public Graph() {
        this.vertices = new HashMap<>();
    }

    // getters

    /*public Map<Integer, Vertex<T>> getVertices() {
        return vertices;
    }*/


    // methods

    // adds vertex to graph
    public void addVertex(int key, T data) {
        vertices.put(key, new Vertex<>(data));
    }

    // returns vertex from key
    public Vertex<T> getVertex(int id) {
        return vertices.get(id);
    }

    public MyList<Vertex<T>> getAllVertices() {
        MyList<Vertex<T>> list = new MyArrayList<>();
        for (Vertex<T> vertex : vertices.values()) {
            list.add(vertex);
        }
        return list;
    }

    // adds directed edge to vertex
    /*public void addDirectedEdge(int from, int to, double weight) {
        Vertex<T> source = vertices.get(from);
        Vertex<T> terminus = vertices.get(to);

        source.connectDirected(terminus, weight);
    }*/

    // adds undirected edge to vertex
    public void addUndirectedEdge(int from, int to, double weight) {
        Vertex<T> source = vertices.get(from);
        Vertex<T> terminus = vertices.get(to);

        source.connectUndirected(terminus, weight);
    }

    /*// prints graph for debugging
    public void printGraph() {
        for (Map.Entry<Integer, Vertex<T>> entry : vertices.entrySet()) {
            int id = entry.getKey();
            Vertex<T> vertex = entry.getValue();

            System.out.print("Vertex " + id + " (" + vertex.getData() + ") -> ");

            for (Edge<T> edge : vertex.getEdges()) {
                System.out.print(edge.getdestination().getData() + " (w=" + edge.getWeight() + "), ");
            }

            System.out.println();
        }
    }*/


}
