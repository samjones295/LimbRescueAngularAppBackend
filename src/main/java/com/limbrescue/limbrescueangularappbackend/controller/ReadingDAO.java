package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Reading;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReadingDAO {
    private String jdbcURL;
    private String dbUser;
    private String dbPassword;
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private Connection connection;
    public ReadingDAO() throws FileNotFoundException, ClassNotFoundException {
        reader = new FileReader("application.properties");
        jdbcURL = p.getProperty("spring.datasource.url");
        dbUser = p.getProperty("spring.datasource.username");
        dbPassword = p.getProperty("spring.datasource.password");
        Class.forName("com.mysql.jdbc.Driver");
        table = p.getProperty("spring.datasource.ReadingTable");
    }
    public List<Reading> getAllReadings() throws SQLException {
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<Reading> readings = new ArrayList<>();
        while (result.next()) {
            Reading reading = new Reading(result.getInt("id"), result.getInt("patient_no"),
                    result.getDate("date_created"), result.getDouble("time"), result.getDouble("ppg_reading"),
                    result.getString("laterality"), result.getString("group_id_array"), result.getString("active_or_rest"));
            readings.add(reading);
        }
        connection.close();
        return readings;
    }
    public Reading getReading(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
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
            reading.setTime(result.getDouble("time"));
            reading.setPpg_reading(result.getDouble("ppg_reading"));
            reading.setLaterality(result.getString("laterality"));
            reading.setGroup_id_array(result.getString("group_id_array"));
            reading.setActive_or_rest(result.getString("active_or_rest"));
        }
        connection.close();
        return reading;
    }
    public void insertReading(Reading reading) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "INSERT INTO " + table + " VALUES(id = ?, patient_no = ?, date_created = ?, time = ?, " +
                "ppg_reading = ?, laterality = ?, group_id_array = ?, active_or_rest = ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getId());
        statement.setInt(2, reading.getPatient_no());
        statement.setDate(3, reading.getDate_created());
        statement.setDouble(4, reading.getTime());
        statement.setDouble(5, reading.getPpg_reading());
        statement.setString(6, reading.getLaterality());
        statement.setString(7, reading.getGroup_id_array());
        statement.setString(8, reading.getActive_or_rest());
        statement.executeQuery();
        connection.close();
    }
    public void updateReading(Reading reading, int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "UPDATE " + table + " SET patient_no = ?, date_created = ?, time = ?, ppg_reading = ?, laterality = ?, " +
                "group_id_array = ?, active_or_rest = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getPatient_no());
        statement.setDate(2, reading.getDate_created());
        statement.setDouble(3, reading.getTime());
        statement.setDouble(4, reading.getPpg_reading());
        statement.setString(5, reading.getLaterality());
        statement.setString(6, reading.getGroup_id_array());
        statement.setString(7, reading.getActive_or_rest());
        statement.setInt(8, reading.getId());
        statement.executeQuery();
        connection.close();
    }
    public void deleteReading(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
