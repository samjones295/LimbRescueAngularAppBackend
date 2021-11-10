package com.limbrescue.limbrescueangularappbackend.model;

public class GroupReading {
    //Fields
    private int id;
    private int group_id;
    private int reading_id;
    //Constructors
    public GroupReading() {

    }
    public GroupReading(int id, int group_id, int reading_id) {
        this.id = id;
        this.group_id = group_id;
        this.reading_id = reading_id;
    }
    //Getters
    public int getId() {
        return id;
    }
    public int getGroup_id() {
        return group_id;
    }
    public int getReading_id() {
        return reading_id;
    }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
    public void setReading_id(int reading_id) {
        this.reading_id = reading_id;
    }
}
