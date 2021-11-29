package com.limbrescue.limbrescueangularappbackend.model;

import java.sql.Date;

public class Reading {
    //Fields
    private int id;
    private String patient_no;
    private Date date_created;
    private String laterality;
    private String comments;
    //Constructors
    public Reading() {

    }
    public Reading(int id, String patient_no, Date date_created, String laterality, String comments) {
        this.id = id;
        this.patient_no = patient_no;
        this.date_created = date_created;
        this.laterality = laterality;
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
    public String getLaterality() { return laterality; }
    public String getComments() {
        return comments;
    }

    //Setters

    public void setId(int id) {
        this.id = id;
    }
    public void setPatient_no(String patient_no) {
        this.patient_no = patient_no;
    }
    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
    public void setLaterality(String laterality) { this.laterality = laterality; }
    public void setComments(String comments) {
        this.comments = comments;
    }
    //ToString
    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "patient_no: " + patient_no + ", " +
                "date_created: " + date_created + ", " +
                "laterality: " + laterality + ", " +
                "comments: " + comments + ", " +
                "}";
    }
}
