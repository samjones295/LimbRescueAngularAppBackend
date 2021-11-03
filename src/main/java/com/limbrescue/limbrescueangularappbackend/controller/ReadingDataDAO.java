package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.ReadingData;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("/api/v1")
public class ReadingDataDAO {
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public ReadingDataDAO() {
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find the file");
        }
        table = p.getProperty("spring.datasource.ReadingDataTable");
        dbConnection = new DBConnection();
    }
    @GetMapping("/allreadingdata")
    public List<ReadingData> getAllReadingData() throws SQLException {
        Connection connection = dbConnection.getConnection();
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
    @GetMapping("/singlereadingdata")
    public ReadingData getReadingData(int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
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
    @PostMapping(path = "/readingdata")
    public void insertReadingData(ReadingData data) throws SQLException{
        Connection connection = dbConnection.getConnection();
        if (getReadingData(data.getId()) != null) {
            updateReadingData(data, data.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " VALUES(id = ?, reading_id = ?, time = ?, ppg_reading = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, data.getId());
            statement.setInt(2, data.getReading_id());
            statement.setDouble(3, data.getTime());
            statement.setDouble(4, data.getPpg_reading());
            statement.executeQuery();
        }
        connection.close();
    }
    public ReadingData updateReadingData(ReadingData data, int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET reading_id = ?, time = ?, ppg_reading = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, data.getReading_id());
        statement.setDouble(2, data.getTime());
        statement.setDouble(3, data.getPpg_reading());
        statement.setInt(4, id);
        ResultSet result = statement.executeQuery();
        data.setReading_id(result.getInt("reading_id"));
        data.setTime(result.getDouble("time"));
        data.setPpg_reading(result.getDouble("ppg_reading"));
        connection.close();
        return data;
    }
    @DeleteMapping("/readingdata")
    public void deleteReadingData(int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
