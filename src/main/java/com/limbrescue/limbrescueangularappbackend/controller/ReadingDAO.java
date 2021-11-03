package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Reading;
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
@RequestMapping("/api/v1")
public class ReadingDAO {
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public ReadingDAO() {
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
        table = p.getProperty("spring.datasource.ReadingTable");
        dbConnection = new DBConnection();
    }
    @GetMapping("/allreadings")
    @ResponseBody
    public List<Reading> getAllReadings() throws SQLException {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<Reading> readings = new ArrayList<>();
        while (result.next()) {
            Reading reading = new Reading(result.getInt("id"), result.getInt("patient_no"),
                    result.getDate("date_created"), result.getString("active_or_rest"), result.getString("comments"));
            readings.add(reading);
        }
        connection.close();
        return readings;
    }
    @GetMapping("/singlereading/{id}")
    @ResponseBody
    public Reading getReading(@PathVariable int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        Reading reading = null;
        if (result.next()) {
            reading = new Reading();
            reading.setId(id);
            reading.setPatient_no(result.getInt("patient_no"));
            reading.setDate_created(result.getDate("date_created"));
            reading.setActive_or_rest(result.getString("active_or_rest"));
            reading.setComments(result.getString("comments"));
        }
        connection.close();
        return reading;
    }
    @PostMapping(path = "/reading")
    @ResponseBody
    public void insertReading(@RequestParam Reading reading) throws SQLException{
        Connection connection = dbConnection.getConnection();
        if (getReading(reading.getId()) != null) {
            updateReading(reading, reading.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " VALUES(id = ?, patient_no = ?, date_created = ?, active_or_rest = ?, comments = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, reading.getId());
            statement.setInt(2, reading.getPatient_no());
            statement.setDate(3, reading.getDate_created());
            statement.setString(4, reading.getActive_or_rest());
            statement.setString(5, reading.getComments());
            statement.executeQuery();
        }
        connection.close();
    }
    @PutMapping(path="/reading/{id}")
    @ResponseBody
    public Reading updateReading(@RequestParam  Reading reading, @PathVariable int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET patient_no = ?, date_created = ?, " +
                "active_or_rest = ?, comments = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getPatient_no());
        statement.setDate(2, reading.getDate_created());
        statement.setString(3, reading.getActive_or_rest());
        statement.setString(4, reading.getComments());
        statement.setInt(5, id);
        ResultSet result = statement.executeQuery();
        reading.setPatient_no(result.getInt("patient_no"));
        reading.setDate_created(result.getDate("date_created"));
        reading.setActive_or_rest(result.getString("active_or_rest"));
        connection.close();
        return reading;
    }
    @PutMapping("/readingcomment/{id}")
    @ResponseBody
    public Reading updateComments(@RequestParam Reading reading, @PathVariable int id, @RequestParam String comment) throws SQLException {
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET comments = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, comment);
        statement.setInt(2, id);
        ResultSet result = statement.executeQuery();
        reading.setComments(result.getString("comments"));
        connection.close();
        return reading;
    }
    @DeleteMapping("/reading/{id}")
    @ResponseBody
    public void deleteReading(int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
