package com.example.dsa2_ca2.model;

public class Exhibit {

    // fields
    private String title;
    private String artist;

    // constructor
    public Exhibit(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    // getters
    public String getTitle() {
        return title;
    }
    public String getArtist() {
        return artist;
    }

    // setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
}
