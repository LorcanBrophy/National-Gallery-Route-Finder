package com.example.dsa2_ca2.graph;

import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;

public class Vertex<T> {

    // fields
    private final T data;
    private final MyList<Edge<T>> edges;

    // constructor
    public Vertex(T data) {
        this.data = data;
        this.edges = new MyArrayList<>();
    }

    // getters
    public T getData() {
        return data;
    }
    public MyList<Edge<T>> getEdges() {
        return edges;
    }

    // methods
    public void connectDirected(Vertex<T> destNode, double weight) {
        edges.add(new Edge<>(destNode, weight));
    }
    public void connectUndirected(Vertex<T> destNode, double weight) {
        edges.add(new Edge<>(destNode, weight));
        destNode.edges.add(new Edge<>(this, weight));
    }


}
