package com.example.dsa2_ca2.model;

public class Room {

    // fields
    private int id;
    private String name;
    private String period;
    private int x;
    private int y;

    private MyList<Exhibit> exhibits;

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
    public MyList<Exhibit> getExhibits() {
        return exhibits;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
    public void setExhibits(MyList<Exhibit> exhibits) {
        this.exhibits = exhibits;
    }
}
