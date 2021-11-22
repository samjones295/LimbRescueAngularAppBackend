package com.limbrescue.limbrescueangularappbackend.model;

public class Result {
    //Fields
    private int id;
    private int group_id;
    private String algorithm;
    private int ran_by;
    private String status;
    private String comments;
    private int train_accuracy;
    private int test_accuracy;
    //Constructors
    public Result() {

    }
    public Result(int id, int group_id, String algorithm, int ran_by, String status, String comments, int train_accuracy, int test_accuracy) {
        this.id = id;
        this.group_id = group_id;
        this.algorithm = algorithm;
        this.ran_by = ran_by;
        this.status = status;
        this.comments = comments;
        this.train_accuracy = train_accuracy;
        this.test_accuracy = test_accuracy;
    }
    //Getters
    public int getId() {
        return id;
    }
    public int getGroup_id() {
        return group_id;
    }
    public String getAlgorithm() {
        return algorithm;
    }
    public int getRan_by() {
        return ran_by;
    }
    public String getStatus() {
        return status;
    }
    public String getComments() {
        return comments;
    }
    public int getTrain_accuracy() { return train_accuracy; }
    public int getTest_accuracy() { return test_accuracy; }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    public void setRan_by(int ran_by) {
        this.ran_by = ran_by;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }
    public void setTrain_accuracy(int train_accuracy) { this.train_accuracy = train_accuracy; }
    public void setTest_accuracy(int test_accuracy) { this.test_accuracy = test_accuracy; }
    //ToString
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "group_id: " + group_id + ", " +
                "algorithm: " + algorithm + ", " +
                "ran_by: " + ran_by + ", " +
                "status: " + status + ", " +
                "comments: " + comments + ", " +
                "}";
    }
}
