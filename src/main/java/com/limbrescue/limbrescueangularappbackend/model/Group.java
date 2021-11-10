package com.limbrescue.limbrescueangularappbackend.model;

import java.sql.Date;

public class Group {
    //Fields
    private int id;
    private String name;
    private Date date_created;
    //Constructors
    public Group() {

    }
    public Group(int id, String name, Date date_created) {
        this.id = id;
        this.name = name;
        this.date_created = date_created;
    }
    //Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Date getDate_created() {
        return date_created;
    }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
}
