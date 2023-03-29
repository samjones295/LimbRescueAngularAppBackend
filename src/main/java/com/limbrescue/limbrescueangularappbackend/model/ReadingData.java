package com.limbrescue.limbrescueangularappbackend.model;

public class ReadingData {
    // Fields
    private int id;
    private int reading_id;
    private double record_time;
    private String ppg_val;
    private String laterality;
    private int derivative;

    // Constructors
    public ReadingData() {

    }

    public ReadingData(int id, int reading_id, double record_time, String ppg_val, String laterality, int derivative) {
        this.id = id;
        this.reading_id = reading_id;
        this.record_time = record_time;
        this.ppg_val = ppg_val;
        this.laterality = laterality;
        this.derivative = derivative;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getReading_id() {
        return reading_id;
    }

    public double getRecord_time() {
        return record_time;
    }

    public String getPpg_val() {
        return ppg_val;
    }

    public String getLaterality() {
        return laterality;
    }

    public int getDerivative() {
        return derivative;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setReading_id(int reading_id) {
        this.reading_id = reading_id;
    }

    public void setRecord_time(double record_time) {
        this.record_time = record_time;
    }

    public void setPpg_val(String ppg_val) {
        this.ppg_val = ppg_val;
    }

    public void setLaterality(String laterality) {
        this.laterality = laterality;
    }

    public void setDerivative(int derivative) {
        this.derivative = derivative;
    }

    // ToString
    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "reading_id: " + reading_id + ", " +
                "record_time: " + record_time + ", " +
                "ppg_val: " + ppg_val + ", " +
                "laterality: " + laterality + ", " +
                "derivative: " + derivative +
                "}";
    }
}
