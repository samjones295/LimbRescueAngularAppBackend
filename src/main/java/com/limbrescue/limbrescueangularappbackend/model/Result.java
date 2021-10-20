package com.limbrescue.limbrescueangularappbackend.model;

public class Result {
    private int id;
    private int group_id;
    private String algorithm;
    private int ran_by;
    private String status;
    private String comments;
    public Result() {

    }
    public Result(int id, int group_id, String algorithm, int ran_by, String status, String comments) {
        this.id = id;
        this.group_id = group_id;
        this.algorithm = algorithm;
        this.ran_by = ran_by;
        this.status = status;
        this.comments = comments;
    }
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
}
