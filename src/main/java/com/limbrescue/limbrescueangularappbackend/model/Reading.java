package com.limbrescue.limbrescueangularappbackend.model;

import java.sql.Date;

public class Reading {
    //Fields
    private int id;
    private String patient_num;
    private String date_created;
    private String laterality;
    private String notes;
    //Constructors
    public Reading() {

    }
    public Reading(int id, String patient_num, String date_created, String laterality, String notes) {
        this.id = id;
        this.patient_num = patient_num;
        this.date_created = date_created;
        this.laterality = laterality;
        this.notes = notes;
    }
    //Getters
    public int getId() {
        return id;
    }
    public String getPatient_num() {
        return patient_num;
    }
    public String getDate_created() {
        return date_created;
    }
    public String getLaterality() { return laterality; }
    public String getnotes() {
        return notes;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setPatient_num(String patient_num) {
        this.patient_num = patient_num;
    }
    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
    public void setLaterality(String laterality) { this.laterality = laterality; }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    //ToString
    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "patient_num: " + patient_num + ", " +
                "date_created: " + date_created + ", " +
                "laterality: " + laterality + ", " +
                "notes: " + notes + ", " +
                "}";
    }
}
