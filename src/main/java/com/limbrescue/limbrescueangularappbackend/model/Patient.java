package com.limbrescue.limbrescueangularappbackend.model;

public class Patient {
    private int id;
    private String patient_no;
    private String status;
    public Patient() {

    }
    public Patient(int id, String patient_no, String status) {
        this.id = id;
        this.patient_no = patient_no;
        this.status = status;
    }
    public int getId() {
        return id;
    }
    public String getPatient_no() {
        return patient_no;
    }
    public String getStatus() {
        return status;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setPatient_no(String patient_no) {
        this.patient_no = patient_no;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
