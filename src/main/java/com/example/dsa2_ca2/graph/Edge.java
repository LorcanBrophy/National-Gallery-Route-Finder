package com.example.dsa2_ca2.graph;

public class Edge<T> {

    // fields
    public Vertex<T> target;
    public double weight;

    // constructor
    public Edge(Vertex<T> target, double weight) {
        this.target = target;
        this.weight = weight;
    }

    // getters

    public Vertex<T> getTarget() {
        return target;
    }
    public double getWeight() {
        return weight;
    }
}
