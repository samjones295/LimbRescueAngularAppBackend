package com.limbrescue.limbrescueangularappbackend.controller;


import com.limbrescue.limbrescueangularappbackend.model.GroupReading;
import org.springframework.security.core.parameters.P;
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
    public GroupReadingDAO() {
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
        table = p.getProperty("spring.datasource.GroupTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the group readings table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the group readings table.
     */
    @GetMapping("/groupreadings")
    @ResponseBody
    public List<GroupReading> getAllGroupReadings() {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        List<GroupReading> readings = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                GroupReading reading = new GroupReading(result.getInt("id"), result.getInt("group_id"),
                        result.getInt("reading_id"));
                readings.add(reading);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readings;
    }
    @GetMapping("/groupreadings/{group_id}")
    @ResponseBody
    public List<GroupReading> getGroupReadingsByGroupID(@PathVariable("group_id") int group_id) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE group_id = ?";
        List<GroupReading> readings = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, group_id);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                GroupReading reading = new GroupReading(result.getInt("id"), result.getInt("group_id"),
                        result.getInt("reading_id"));
                readings.add(reading);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readings;
    }
    /**
     * Retrieves a single group reading based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the group readings table.
     */
    @GetMapping("/groupreading/{id}")
    @ResponseBody
    public GroupReading getGroupReading(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        GroupReading reading = null;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                reading = new GroupReading();
                reading.setId(id);
                reading.setGroup_id(result.getInt("group_id"));
                reading.setReading_id(result.getInt("reading_id"));
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
        return reading;
    }
    /**
     * Inserts a group reading to the table.
     *
     * @param reading
     *              The group reading to be inserted.
     */
    @PostMapping(path = "/groupreading")
    @ResponseBody
    public void insertGroupReading(@RequestBody GroupReading reading) {
        Connection connection = dbConnection.getConnection();
        //Updates the ID if necessary to avoid duplicates.
        int id = reading.getId();
        while (getGroupReading(id) != null) {
            id++;
            reading.setId(id);
        }
        try {
            String sql = "INSERT INTO " + table + " (id, group_id, reading_id) VALUES(?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, reading.getId());
            statement.setInt(2, reading.getGroup_id());
            statement.setInt(3, reading.getReading_id());
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
     * Updates a group reading based on the ID.
     *
     * @param reading
     *          The variable values of the columns.
     * @param id
     *          The group reading ID to be updated.
     */
    @PutMapping(path="/groupreading/{id}")
    @ResponseBody
    public void updateGroupReading(@RequestBody GroupReading reading, @PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET group_id = ?, reading_id = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, reading.getGroup_id());
            statement.setInt(2, reading.getReading_id());
            statement.setInt(3, id);
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
     * Deletes a Group reading based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
    @DeleteMapping("/groupreading/{id}")
    @ResponseBody
    public void deleteGroupReading(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
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
