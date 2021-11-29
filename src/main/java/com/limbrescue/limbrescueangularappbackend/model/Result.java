package com.limbrescue.limbrescueangularappbackend.model;

import java.sql.Date;

public class Result {
    //Fields
    private int id;
    private String group_name;
    private Date date_ran;
    private String algorithm;
    private int train_accuracy;
    private int test_accuracy;
    //Constructors
    public Result() {

    }
    public Result(int id, String group_name, Date date_ran, String algorithm, int train_accuracy, int test_accuracy) {
        this.id = id;
        this.group_name = group_name;
        this.date_ran = date_ran;
        this.algorithm = algorithm;
        this.train_accuracy = train_accuracy;
        this.test_accuracy = test_accuracy;
    }
    //Getters
    public int getId() {
        return id;
    }
    public String getGroup_name() {
        return group_name;
    }
    public Date getDate_ran() { return date_ran; }
    public String getAlgorithm() {
        return algorithm;
    }
    public int getTrain_accuracy() { return train_accuracy; }
    public int getTest_accuracy() { return test_accuracy; }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
    public void setDate_ran(Date date_ran) { this.date_ran = date_ran; }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    public void setTrain_accuracy(int train_accuracy) { this.train_accuracy = train_accuracy; }
    public void setTest_accuracy(int test_accuracy) { this.test_accuracy = test_accuracy; }
    //ToString
    @Override
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "group_name: " + group_name + ", " +
                "date_ran: " + date_ran + ", " +
                "algorithm: " + algorithm + ", " +
                "train accuracy: " + train_accuracy + ", " +
                "test accuracy: " + test_accuracy +
                "}";
    }
}
