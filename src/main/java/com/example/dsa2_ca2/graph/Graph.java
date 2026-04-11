package com.example.dsa2_ca2.graph;

import java.util.HashMap;
import java.util.Map;

public class Graph<T> {

    // fields
    private Map<Integer, Vertex<T>> vertices;

    // constructor
    public Graph(Map<Integer, Vertex<T>> vertices) {
        vertices = new HashMap<>();
    }

    // getters

    public Map<Integer, Vertex<T>> getVertices() {
        return vertices;
    }


    // methods

    // adds vertex to graph
    public void addVertex(int key, T data) {
        vertices.put(key, new Vertex<>(data));
    }

    // returns vertex from key
    public Vertex<T> getVertex(int id) {
        return vertices.get(id);
    }

    // adds directed edge to vertex
    public void addDirectedEdge(int from, int to, double weight) {
        Vertex<T> source = vertices.get(from);
        Vertex<T> terminus = vertices.get(to);

        source.connectDirected(terminus, weight);
    }

    // adds undirected edge to vertex
    public void addUndirectedEdge(int from, int to, double weight) {
        Vertex<T> source = vertices.get(from);
        Vertex<T> terminus = vertices.get(to);

        source.connectUndirected(terminus, weight);
    }


}
