package com.limbrescue.limbrescueangularappbackend.controller;


import com.limbrescue.limbrescueangularappbackend.model.GroupReading;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GroupReadingDAO {
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public GroupReadingDAO() throws FileNotFoundException{
        reader = new FileReader("application.properties");
        table = p.getProperty("spring.datasource.GroupTable");
        dbConnection = new DBConnection();
    }
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
    public GroupReading getGroupReading(int id) throws SQLException{
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
    public void insertGroupReading(GroupReading reading) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "INSERT INTO " + table + " VALUES(id = ?, group_id = ?, reading_id = ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reading.getId());
        statement.setInt(2, reading.getGroup_id());
        statement.setInt(3, reading.getReading_id());
        statement.executeQuery();
        connection.close();
    }
    public GroupReading updateGroupReading(GroupReading reading, int id) throws SQLException{
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
    public void deleteGroupReading(int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
