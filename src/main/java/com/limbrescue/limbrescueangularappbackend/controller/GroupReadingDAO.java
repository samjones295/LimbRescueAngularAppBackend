package com.limbrescue.limbrescueangularappbackend.controller;


import com.limbrescue.limbrescueangularappbackend.model.GroupReading;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("")
public class GroupReadingDAO {
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public GroupReadingDAO() {
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find the file");
        }
        try {
            p.load(reader);
        } catch (IOException e) {
            System.out.println("Cannot load file");
        }
        table = p.getProperty("spring.datasource.GroupReadingTable");
        dbConnection = new DBConnection();
    }
    @GetMapping("/allgroupreadings")
    @ResponseBody
    public List<GroupReading> getAllGroupReadings() throws SQLException {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<GroupReading> readings = new ArrayList<>();
        while (result.next()) {
            GroupReading reading = new GroupReading(result.getInt("id"), result.getInt("group_id"),
                    result.getInt("reading_id"));
            readings.add(reading);
        }
        connection.close();
        return readings;
    }
    @GetMapping("/singlegroupreading/{id}")
    @ResponseBody
    public GroupReading getGroupReading(@PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        GroupReading reading = null;
        if (result.next()) {
            reading = new GroupReading();
            reading.setId(id);
            reading.setGroup_id(result.getInt("group_id"));
            reading.setReading_id(result.getInt("reading_id"));
        }
        connection.close();
        return reading;
    }
    @PostMapping(path = "/groupreading")
    @ResponseBody
    public void insertGroupReading(@RequestParam GroupReading reading) throws SQLException{
        Connection connection = dbConnection.getConnection();
        if (getGroupReading(reading.getId()) != null) {
            updateGroupReading(reading, reading.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " VALUES(id = ?, group_id = ?, reading_id = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, reading.getId());
            statement.setInt(2, reading.getGroup_id());
            statement.setInt(3, reading.getReading_id());
            statement.executeQuery();
        }
        connection.close();
    }
    @PutMapping(path="/groupreading/{id}")
    @ResponseBody
    public GroupReading updateGroupReading(@RequestParam GroupReading reading, @PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET group_id = ?, reading_id = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getGroup_id());
        statement.setInt(2, reading.getReading_id());
        statement.setInt(3, id);
        ResultSet result = statement.executeQuery();
        reading.setGroup_id(result.getInt("group_id"));
        reading.setReading_id(result.getInt("reading_id"));
        connection.close();
        return reading;
    }
    @DeleteMapping("/groupreading/{id}")
    @ResponseBody
    public void deleteGroupReading(@PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
