package com.limbrescue.limbrescueangularappbackend.model;



public class ReadingData {
    //Fields
    private int id;
    private int reading_id;
    private double time;
    private String ppg_reading;
    private String laterality;
    //Constructors
    public ReadingData() {

    }
    public ReadingData(int id, int reading_id, double time, String ppg_reading, String laterality) {
        this.id = id;
        this.reading_id = reading_id;
        this.time = time;
        this.ppg_reading = ppg_reading;
        this.laterality = laterality;
    }
    //Getters
    public int getId() {
        return id;
    }
    public int getReading_id() {
        return reading_id;
    }
    public double getTime() {
        return time;
    }
    public String getPpg_reading() {
        return ppg_reading;
    }
    public String getLaterality() { return  laterality; }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setReading_id(int reading_id) {
        this.reading_id = reading_id;
    }
    public void setTime(double time) {
        this.time = time;
    }
    public void setPpg_reading(String ppg_reading) {
        this.ppg_reading = ppg_reading;
    }
    public void setLaterality(String laterality) { this.laterality = laterality; }
    //ToString
    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "patient_no: " + reading_id + ", " +
                "time: " + time + ", " +
                "ppg_reading: " + ppg_reading + ", " +
                "laterality: " + laterality +
                "}";
    }
}
