package com.limbrescue.limbrescueangularappbackend.model;

import java.sql.Date;

public class Reading {
    //Fields
    private int id;
    private String patient_no;
    private Date date_created;
    //private String active_or_rest;
    private String comments;
    //Constructors
    public Reading() {

    }
    public Reading(int id, String patient_no, Date date_created, /*String active_or_rest,*/ String comments) {
        this.id = id;
        this.patient_no = patient_no;
        this.date_created = date_created;
        //this.active_or_rest = active_or_rest;
        this.comments = comments;
    }
    //Getters
    public int getId() {
        return id;
    }
    public String getPatient_no() {
        return patient_no;
    }
    public Date getDate_created() {
        return date_created;
    }
//    public String getActive_or_rest() {
//        return active_or_rest;
//    }
    //Setters
    public String getComments() {
        return comments;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setPatient_no(String patient_no) {
        this.patient_no = patient_no;
    }
    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
//    public void setActive_or_rest(String active_or_rest) {
//        this.active_or_rest = active_or_rest;
//    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    //ToString
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "patient_no: " + patient_no + ", " +
                "date_created: " + date_created + ", " +
                "comments: " + comments + ", " +
                "}";
    }
}
