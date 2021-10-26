package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Reading;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReadingDAO {
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public ReadingDAO() throws FileNotFoundException{
        reader = new FileReader("application.properties");
        table = p.getProperty("spring.datasource.GroupTable");
        dbConnection = new DBConnection();
    }
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
    public Reading getReading(int id) throws SQLException{
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
    public void insertReading(Reading reading) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "INSERT INTO " + table + " VALUES(id = ?, patient_no = ?, date_created = ?, active_or_rest = ?, comments = ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getId());
        statement.setInt(2, reading.getPatient_no());
        statement.setDate(3, reading.getDate_created());
        statement.setString(4, reading.getActive_or_rest());
        statement.setString(5, reading.getComments());
        statement.executeQuery();
        connection.close();
    }
    public Reading updateReading(Reading reading, int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET patient_no = ?, date_created = ?, " +
                "active_or_rest = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getPatient_no());
        statement.setDate(2, reading.getDate_created());
        statement.setString(3, reading.getActive_or_rest());
        statement.setInt(4, id);
        ResultSet result = statement.executeQuery();
        reading.setPatient_no(result.getInt("patient_no"));
        reading.setDate_created(result.getDate("date_created"));
        reading.setActive_or_rest(result.getString("active_or_rest"));
        connection.close();
        return reading;
    }
    public Reading updateComments(Reading reading, int id, String comment) throws SQLException {
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
    public void deleteReading(int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
