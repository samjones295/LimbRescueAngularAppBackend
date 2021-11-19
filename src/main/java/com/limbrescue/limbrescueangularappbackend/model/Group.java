package com.limbrescue.limbrescueangularappbackend.model;

import java.sql.Date;
import java.util.Arrays;
import java.util.Comparator;

public class Group {
    //Fields
    private int id;
    private String name;
    private String reading_ids;
    //Constructors
    public Group() {

    }
    public Group(int id, String name, String reading_ids) {
        this.id = id;
        this.name = name;
        this.reading_ids = reading_ids;
    }
    //Getters
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getReading_ids(){ return reading_ids; }
    //Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setReading_ids(String reading_ids) { this.reading_ids = sortIDs(reading_ids);}
    //ToString
    public String toString() {
        return "{" +
                "id: " + id + ", " +
                "name: " + name + ", " +
                "reading_ids: " + reading_ids + ", " +
                "}";
    }

    /**
     *
     * @param ids
     *              The list of ids to be sorted
     * @return
     *              The sorted list of IDs.
     */
    private String sortIDs(String ids) {
        String[] nums = ids.split(", ");
        Arrays.sort(nums, new Comparator<String>() {
            public int compare(String a, String b) {
                return Integer.parseInt(a) - Integer.parseInt(b);
            }
        });
        String result = Arrays.toString(nums);
        return result.substring(1, result.length() - 1);
    }
}
