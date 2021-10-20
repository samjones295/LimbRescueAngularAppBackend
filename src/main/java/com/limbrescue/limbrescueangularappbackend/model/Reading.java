package com.limbrescue.limbrescueangularappbackend.model;

import java.sql.Date;

public class Reading {
    private int id;
    private int patient_no;
    private Date date_created;
    private double time;
    private double ppg_reading;
    private String laterality;
    private String group_id_array;
    private String active_or_rest;
    public Reading() {

    }
    public Reading(int id, int patient_no, Date date_created, double time, double ppg_reading, String laterality, String group_id_array, String active_or_rest) {
        this.id = id;
        this.patient_no = patient_no;
        this.date_created = date_created;
        this.time = time;
        this.ppg_reading = ppg_reading;
        this.laterality = laterality;
        this.group_id_array = group_id_array;
        this.active_or_rest = active_or_rest;
    }
    public int getId() {
        return id;
    }
    public int getPatient_no() {
        return patient_no;
    }
    public Date getDate_created() {
        return date_created;
    }
    public double getTime() {
        return time;
    }
    public double getPpg_reading() {
        return ppg_reading;
    }
    public String getLaterality() {
        return laterality;
    }
    public String getGroup_id_array() {
        return group_id_array;
    }
    public String getActive_or_rest() {
        return active_or_rest;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setPatient_no(int patient_no) {
        this.patient_no = patient_no;
    }
    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }
    public void setTime(double time) {
        this.time = time;
    }
    public void setPpg_reading(double ppg_reading) {
        this.ppg_reading = ppg_reading;
    }
    public void setLaterality(String laterality) {
        this.laterality = laterality;
    }
    public void setGroup_id_array(String group_id_array) {
        this.group_id_array = group_id_array;
    }
    public void setActive_or_rest(String active_or_rest) {
        this.active_or_rest = active_or_rest;
    }
}
