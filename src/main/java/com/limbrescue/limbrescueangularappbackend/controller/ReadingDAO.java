package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Reading;
import org.springframework.web.bind.annotation.*;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("")
public class ReadingDAO {
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
    public ReadingDAO() {
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
        table = p.getProperty("spring.datasource.ReadingTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the readings table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the readings table.
     */
    @GetMapping("/readings")
    @ResponseBody
    public List<Reading> getAllReadings() {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        List<Reading> readings = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Reading reading = new Reading(result.getInt("id"), result.getString("patient_no"),
                        result.getDate("date_created"), result.getString("laterality"), /*result.getString("active_or_rest"),*/ result.getString("comments"));
                readings.add(reading);
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
     * Retrieves all the elements of the readings table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the readings table.
     */
    @GetMapping(value ="/readings", params="patient_no")
    @ResponseBody
    public List<Reading> getAllReadingsOfPatient(@RequestParam("patient_no") String patient_no) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table  + " WHERE patient_no = ?"; //The SELECT query.
        List<Reading> readings = new ArrayList<>(); //The array list to store the tuples.
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patient_no);
            ResultSet result = statement.executeQuery();
            //Iterates over the result set and adds into the array list after executing query.
            while (result.next()) {
                Reading reading = new Reading(result.getInt("id"), result.getString("patient_no"),
                        result.getDate("date_created"), result.getString("laterality"), result.getString("comments"));
                readings.add(reading);
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
     * Retrieves a single reading based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the readings table.
     */
    @GetMapping("/reading/{id}")
    @ResponseBody
    public Reading getReading(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        Reading reading = null; //Uses a NULL value if ID is not found.
        String sql = "SELECT * FROM " + table + " WHERE id = ?"; //The SELECT Query
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            //If found, set return object to be the value of the tuple.
            if (result.next()) {
                reading = new Reading();
                reading.setId(id);
                reading.setPatient_no(result.getString("patient_no"));
                reading.setDate_created(result.getDate("date_created"));
                reading.setLaterality(result.getString("laterality"));
                reading.setComments(result.getString("comments"));
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
     * Retrieves a single reading based on the patient_no.
     *
     * @param patient_no
     *          The patient_no to be retrieved
     * @return
     *          A pointer to a tuple in the readings table.
     */
    @GetMapping("/reading")
    @ResponseBody
    public Reading getReadingOfPatient(@RequestParam("patient_no") String patient_no) {
        Connection connection = dbConnection.getConnection();
        Reading reading = null;
        String sql = "SELECT * FROM " + table + " WHERE patient_no = ?"; //The SELECT Query
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patient_no);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                reading = new Reading();
                reading.setId(result.getInt("id"));
                reading.setPatient_no(patient_no);
                reading.setDate_created(result.getDate("date_created"));
                reading.setLaterality(result.getString("laterality"));
                reading.setComments(result.getString("comments"));
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
     * Inserts a reading to the table.
     *
     * @param reading
     *              The reading to be inserted.
     */
    @PostMapping(path = "/reading")
    @ResponseBody
    public void insertReading(@RequestBody Reading reading) {
        Connection connection = dbConnection.getConnection();
        //Updates the ID if necessary to avoid duplicates.
        int id = reading.getId();
        while (getReading(id) != null) {
            id++;
            reading.setId(id);
        }
        //SQL Insert Statement
        String sql = "INSERT INTO " + table + " (id, patient_no, date_created, laterality, comments) VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, reading.getId());
            statement.setString(2, reading.getPatient_no());
            statement.setDate(3, reading.getDate_created());
            statement.setString(4, reading.getLaterality());
            statement.setString(5, reading.getComments());
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
    @GetMapping("/timestamp")
    @ResponseBody
    public String getCurrentDateAndTime() {
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = df.format(new Date());
        System.out.println(gmtTime);
        return gmtTime;
    }

    /**
     * Updates a reading based on the ID.
     *
     * @param reading
     *          The variable values of the columns.
     * @param id
     *          The reading ID to be updated.
     */
    @PutMapping(path="/reading/{id}")
    @ResponseBody
    public void updateReading(@RequestBody Reading reading, @PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        //SQL Update Statement
        String sql = "UPDATE " + table + " SET patient_no = ?, date_created = ?, laterality = ?, comments= ? " +
                " WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, reading.getPatient_no());
            statement.setDate(2, reading.getDate_created());
            statement.setString(3, reading.getLaterality());
            statement.setString(4, reading.getComments());
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
     * Updates the comments of a patient.
     *
     * @param id
     *          The id to be updated.
     * @param comment
     *          The updated comment.
     */
    @PutMapping("/readingcomment/{id}")
    @ResponseBody
    public void updateComments(@PathVariable("id") int id, @RequestParam String comment) {
        Connection connection = dbConnection.getConnection();
        //SQL Update Statement to update comments
        String sql = "UPDATE " + table + " SET comments = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, comment);
            statement.setInt(2, id);
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
     * Deletes a reading based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
    @DeleteMapping("/reading/{id}")
    @ResponseBody
    public void deleteReading(@PathVariable("id") int id) {
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
