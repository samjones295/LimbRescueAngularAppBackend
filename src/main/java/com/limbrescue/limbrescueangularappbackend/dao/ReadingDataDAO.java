package com.limbrescue.limbrescueangularappbackend.dao;

import com.limbrescue.limbrescueangularappbackend.model.ReadingData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReadingDataDAO {
    private String jdbcURL;
    private String dbUser;
    private String dbPassword;
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private Connection connection;
    public ReadingDataDAO() throws FileNotFoundException, ClassNotFoundException {
        reader = new FileReader("application.properties");
        jdbcURL = p.getProperty("spring.datasource.url");
        dbUser = p.getProperty("spring.datasource.username");
        dbPassword = p.getProperty("spring.datasource.password");
        Class.forName("com.mysql.jdbc.Driver");
        table = p.getProperty("spring.datasource.ReadingTable");
    }
    public List<ReadingData> getAllReadingData() throws SQLException {
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<ReadingData> readings = new ArrayList<>();
        while (result.next()) {
            ReadingData data = new ReadingData(result.getInt("id"), result.getInt("reading_id"),
                    result.getDouble("time"), result.getDouble("ppg_reading"));
            readings.add(data);
        }
        connection.close();
        return readings;
    }
    public ReadingData getReadingData(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        ReadingData data = null;
        if (result.next()) {
            data = new ReadingData();
            data.setId(id);
            data.setReading_id(result.getInt("reading_id"));
            data.setTime(result.getDouble("time"));
            data.setPpg_reading(result.getDouble("ppg_reading"));
        }
        connection.close();
        return data;
    }
    public void insertReadingData(ReadingData data) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "INSERT INTO " + table + " VALUES(id = ?, reading_id = ?, time = ?, ppg_reading = ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, data.getId());
        statement.setInt(2, data.getReading_id());
        statement.setDouble(3, data.getTime());
        statement.setDouble(4, data.getPpg_reading());
        statement.executeQuery();
        connection.close();
    }
    public void updateReadingData(ReadingData data, int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "UPDATE " + table + " SET reading_id = ?, time = ?, ppg_reading = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, data.getReading_id());
        statement.setDouble(2, data.getTime());
        statement.setDouble(3, data.getPpg_reading());
        statement.setInt(4, data.getId());
        statement.executeQuery();
        connection.close();
    }
    public void deleteReadingData(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
