package com.limbrescue.limbrescueangularappbackend.model;

public class Patient {
    //Fields
    private int id;
    private String patient_no;
    private String status;
    //Constructors
    public Patient() {

    }
    public Patient(int id, String patient_no, String status) {
        this.id = id;
        this.patient_no = patient_no;
        this.status = status;
    }
    //Getters
    public int getId() {
        return id;
    }
    public String getPatient_no() {
        return patient_no;
    }
    public String getStatus() {
        return status;
    }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setPatient_no(String patient_no) {
        this.patient_no = patient_no;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    //ToString
    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "patient_no: " + patient_no + ", " +
                "status: " + status + ", " +
                "}";
    }
}
