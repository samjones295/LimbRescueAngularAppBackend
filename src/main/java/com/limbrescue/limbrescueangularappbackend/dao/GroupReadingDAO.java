package com.limbrescue.limbrescueangularappbackend.dao;


import com.limbrescue.limbrescueangularappbackend.model.GroupReading;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GroupReadingDAO {
    private String jdbcURL;
    private String dbUser;
    private String dbPassword;
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private Connection connection;
    public GroupReadingDAO() throws FileNotFoundException, ClassNotFoundException {
        reader = new FileReader("application.properties");
        jdbcURL = p.getProperty("spring.datasource.url");
        dbUser = p.getProperty("spring.datasource.username");
        dbPassword = p.getProperty("spring.datasource.password");
        Class.forName("com.mysql.jdbc.Driver");
        table = p.getProperty("spring.datasource.GroupReadingTable");
    }
    public List<GroupReading> getAllGroupReadings() throws SQLException {
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
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
    public GroupReading getGroupReading(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
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
    public void insertGroupReading(GroupReading reading) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "INSERT INTO " + table + " VALUES(id = ?, group_id = ?, reading_id = ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getId());
        statement.setInt(2, reading.getGroup_id());
        statement.setInt(3, reading.getReading_id());
        statement.executeQuery();
        connection.close();
    }
    public void updateGroupReading(GroupReading reading, int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "UPDATE " + table + " SET group_id = ?, reading_id = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getGroup_id());
        statement.setInt(2, reading.getReading_id());
        statement.setInt(3, reading.getId());
        statement.executeQuery();
        connection.close();
    }
    public void deleteGroupReading(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
