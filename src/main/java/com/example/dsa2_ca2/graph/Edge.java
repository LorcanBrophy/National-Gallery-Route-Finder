package com.example.dsa2_ca2.graph;

public class Edge<T> {

    // fields
    public Vertex<T> destination;
    public double weight;

    // constructor
    public Edge(Vertex<T> destination, double weight) {
        this.destination = destination;
        this.weight = weight;
    }

    // getters
    public Vertex<T> getdestination() {
        return destination;
    }
    public double getWeight() {
        return weight;
    }
}
