package com.limbrescue.limbrescueangularappbackend.model;

public class ReadingData {
    //Fields
    private int id;
    private int reading_id;
    private double time;
    private double ppg_reading;
    //Constructors
    public ReadingData() {

    }
    public ReadingData(int id, int reading_id, double time, double ppg_reading) {
        this.id = id;
        this.reading_id = reading_id;
        this.time = time;
        this.ppg_reading = ppg_reading;
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
    public double getPpg_reading() {
        return ppg_reading;
    }
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
    public void setPpg_reading(double ppg_reading) {
        this.ppg_reading = ppg_reading;
    }
}
