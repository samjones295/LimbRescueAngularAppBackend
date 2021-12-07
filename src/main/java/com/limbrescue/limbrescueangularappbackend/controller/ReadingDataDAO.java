package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.ReadingData;
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
public class ReadingDataDAO {
    /**
     * The name of the table.
     */
    private String table;
    /**
     * The properties file.
     */
    private static final Properties p = new Properties();
    /**
     * The file reader.
     */
    private FileReader reader;
    /**
     * The Database Connection.
     */
    private DBConnection dbConnection;

    /**
     * Constructor
     */
    public ReadingDataDAO() {
        //Determine what file to read
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Loads the reader.
        try {
            p.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Reads the table from the properties file.
        table = p.getProperty("spring.datasource.ReadingDataTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the reading data table and stores it in an array list based on reading ID and
     * laterality.
     *
     * @param reading_id
     *                  The reading ID.
     * @param laterality
     *                  The laterality.
     * @return
     *          An arraylist containing the reading data table.
     */
    @GetMapping(value="/data", params={"reading_id","laterality"})
    @ResponseBody
    public List<ReadingData> getAllReadingDataOfReadingId(@RequestParam("reading_id") int reading_id, @RequestParam("laterality") String laterality) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE reading_id=? AND laterality=?"; //The SELECT Query
        List<ReadingData> readings = new ArrayList<>(); //The array list to store the tuples.
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, reading_id);
            statement.setString(2, laterality);
            ResultSet result = statement.executeQuery();
            //Iterates over the result set and adds into the array list after executing query.
            while (result.next()) {
                ReadingData data = new ReadingData(result.getInt("id"), result.getInt("reading_id"),
                        result.getDouble("time"), result.getString("value"), result.getString("laterality"));
                readings.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return readings;
    }

    /**
     * Retrieves all the elements of the reading data table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the reading data table.
     */
    @GetMapping("/data")
    @ResponseBody
    public List<ReadingData> getAllReadingData() {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table; //The SELECT Query
        List<ReadingData> readings = new ArrayList<>(); //The array list to store the tuples.
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            //Iterates over the result set and adds into the array list after executing query.
            while (result.next()) {
                ReadingData data = new ReadingData(result.getInt("id"), result.getInt("reading_id"),
                        result.getDouble("time"), result.getString("ppg_reading"), result.getString("laterality"));
                readings.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return readings;
    }

    /**
     * Retrieves a single reading data based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the reading data table.
     */
    @GetMapping("/data/{id}")
    @ResponseBody
    public ReadingData getReadingData(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?"; //The SELECT Query
        ReadingData data = null; //Uses a NULL value if ID is not found.
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            //If found, set return object to be the value of the tuple.
            if (result.next()) {
                data = new ReadingData();
                data.setId(id);
                data.setReading_id(result.getInt("reading_id"));
                data.setTime(result.getDouble("time"));
                data.setPpg_reading(result.getString("ppg_reading"));
                data.setLaterality(result.getString("laterality"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * Inserts a reading data into the table.
     * @param sql
     *          The SQL query
     * @param reading_id
     *          The reading ID
     * @param time
     *          The time
     * @param value
     *          The reading
     * @param laterality
     *          The laterality
     */
    public void insertReadingData(String sql, int reading_id, double time, String value, String laterality) {
        Connection connection = dbConnection.getConnection();
        //SQL Insert Statement
        //String sql = "INSERT INTO " + table + " (id, reading_id, time, ppg_reading, laterality) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, 0);
            statement.setInt(2, reading_id);
            statement.setDouble(3, time);
            statement.setString(4, value);
            statement.setString(5, laterality);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a reading data
     * @param data
     *              The data to be parsed.
     */
    @PostMapping(path = "/data")
    @ResponseBody
    public void parseData(@RequestBody ReadingData data) {
        String sql = "INSERT INTO " + table + " (id, reading_id, time, ppg_reading, laterality) VALUES(?, ?, ?, ?, ?)";
        String ppg_reading = data.getPpg_reading();
        //Get the time.
        int timeIndex = ppg_reading.indexOf("time:");
        //Get the value.
        int valueIndex = ppg_reading.indexOf("value:");
        //Converts time to double.
        double time = Double.parseDouble(ppg_reading.substring(timeIndex + 6, ppg_reading.indexOf(",", timeIndex)));
        //The value part of the PPG reading.
        String value = ppg_reading.substring(valueIndex + 7, ppg_reading.length() - 1);
        //Update the time and ppg reading attributes.
        data.setTime(time);
        data.setPpg_reading(value);
        insertReadingData(sql, data.getReading_id(), time, value, data.getLaterality());
    }
    /**
     * Updates a reading data based on the ID.
     *
     * @param data
     *          The variable values of the columns.
     * @param id
     *          The reading data ID to be updated.
     */
    @PutMapping(path="/data/{id}")
    @ResponseBody
    public void updateReadingData(@RequestBody ReadingData data, @PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        //SQL Update Statement
        String sql = "UPDATE " + table + " SET reading_id = ?, time = ?, ppg_reading = ?, laterality = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, data.getReading_id());
            statement.setDouble(2, data.getTime());
            statement.setString(3, data.getPpg_reading());
            statement.setString(4, data.getLaterality());
            statement.setInt(5, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Deletes a reading data based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
    @DeleteMapping("/data/{id}")
    @ResponseBody
    public void deleteReadingData(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        //SQL Delete Statement
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
