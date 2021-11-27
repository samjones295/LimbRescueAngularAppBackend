package com.limbrescue.limbrescueangularappbackend.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Group {
    //Fields
    private int id;
    private String name;
    private String reading_ids;
    //Constructors
    public Group() {

    }
    public Group(int id, String name, String reading_ids) {
        this.id = id;
        this.name = name;
        this.reading_ids = reading_ids;
    }
    //Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getReading_ids(){ return reading_ids; }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setReading_ids(String reading_ids) { this.reading_ids = reading_ids; }
    //ToString
    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "name: " + name + ", " +
                "reading_ids: " + reading_ids + ", " +
                "}";
    }
}
