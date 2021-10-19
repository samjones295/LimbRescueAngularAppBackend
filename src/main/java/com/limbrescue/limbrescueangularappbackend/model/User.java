package com.limbrescue.limbrescueangularappbackend.model;

import java.sql.Date;
import java.sql.Timestamp;

public class User {
    private int id;
    private String email;
    private String username;
    private String password;
    private Date date_created;
    private Timestamp last_updated;
    //Constructors
    public User() {

    }
    public User(int id, String email, String username, String password, Date date_created, Timestamp last_updated) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.date_created = date_created;
        this.last_updated = last_updated;
    }
    //Getters
    public int getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Date getDate_created() {
        return date_created;
    }
    public Timestamp getLast_updated() {
        return last_updated;
    }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
    public void setLast_updated(Timestamp last_updated) {
        this.last_updated = last_updated;
    }
}
