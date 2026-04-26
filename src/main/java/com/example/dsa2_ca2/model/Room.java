package com.example.dsa2_ca2.model;

public class Room {

    // fields
    private final int id;
    private final String name;
    private final String period;
    private final int x;
    private final int y;

    private final MyList<Exhibit> exhibits;

    // constructor
    public Room(int id, String name, String period, int x, int y) {
        this.id = id;
        this.name = name;
        this.period = period;
        this.x = x;
        this.y = y;
        this.exhibits = new MyArrayList<>();
    }

    // getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPeriod() {
        return period;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public MyList<Exhibit> getExhibits() {
        return exhibits;
    }
}
