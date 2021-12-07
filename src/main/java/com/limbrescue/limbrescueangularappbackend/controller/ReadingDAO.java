package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Reading;
import org.springframework.web.bind.annotation.*;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET, RequestMethod.POST})
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
     * Global storage for time and date
     */
    private String startTime;
    private String endTime;
    private final static long DELAY = 3000;
    private long delta;
    private int reading_id;
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
        Date defaultDate = new Date(0);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"); //Formats the date.
        formatter.setTimeZone(TimeZone.getTimeZone("gmt")); //Time zone is in UTC.
        // Initiate time setting
        startTime = formatter.format(defaultDate);
        endTime = formatter.format(defaultDate);
        delta = 0;
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
                        result.getDate("date_created").toString(), result.getString("laterality"), result.getString("comments"));
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
                        result.getDate("date_created").toString(), result.getString("laterality"), result.getString("comments"));
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
                reading.setDate_created(result.getString("date_created"));
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
                reading.setDate_created(result.getString("date_created"));
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
     * @param sql
     *          The SQL query.
     * @param id
     *          The reading ID
     * @param patient_no
     *          The patient number
     * @param date_created
     *          The date created
     * @param laterality
     *          The laterality
     * @param comments
     *          The comments
     * @return
     *          The id of the reading.
     */
    public int insertReading(String sql, int id, String patient_no, String date_created, String laterality, String comments) {
        Connection connection = dbConnection.getConnection();
        //SQL Insert Statement
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setString(2, patient_no);
            statement.setString(3, date_created);
            statement.setString(4, laterality);
            statement.setString(5, comments);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    /**
     * Parses a reading
     * @param reading
     *              The reading to be parsed
     * @return
     *          the ID of the inserted reading.
     */
    @PostMapping(path = "/table")
    @ResponseBody
    public int parseData(@RequestBody Reading reading) {
        //Auto increment the ID.
        int id = reading.getId();
        while (getReading(id) != null) {
            id++;
        }
        reading.setId(id);
        String sql = "INSERT INTO " + table + " (id, patient_no, date_created, laterality, comments) VALUES(?, ?, ?, ?, ?)";
        //Date to be parsed.
        String create = reading.getDate_created();
        //Splits the date into the components.
        String[] elements = create.split(" ");
        //Converts the month to a number.
        int month;
        switch (elements[1]) {
            case "Jan":
                month = 0;
                break;
            case "Feb":
                month = 1;
                break;
            case "Mar":
                month = 2;
                break;
            case "Apr":
                month = 3;
                break;
            case "May":
                month = 4;
                break;
            case "Jun":
                month = 5;
                break;
            case "Jul":
                month = 6;
                break;
            case "Aug":
                month = 7;
                break;
            case "Sep":
                month = 8;
                break;
            case "Oct":
                month = 9;
                break;
            case "Nov":
                month = 10;
                break;
            case "Dec":
                month = 11;
                break;
            default:
                throw new IllegalArgumentException("Invalid month");
        }
        java.sql.Date date = new java.sql.Date(Integer.parseInt(elements[5]) - 1900, month, Integer.parseInt(elements[2]));
        //Updates the date
        reading.setDate_created(date.toString());
        reading_id = reading.getId();
        return insertReading(sql, reading.getId(), reading.getPatient_no(), date.toString(), reading.getLaterality(), reading.getComments());
    }
    @GetMapping("/table")
    @ResponseBody
    public int getReading_id() {
        return reading_id;
    }
    /**
     * Retrieves the start and stop date and time for the watch.
     *
     * @param delta
     *          The standard time of watch.
     * @return
     *          The array containing the start and the stop time for the watch.
     */
    @GetMapping("/start")
    @ResponseBody
    public String getDateAndTime(@RequestParam("delta") long delta) {
        this.delta = delta;
        // Unit of delta is ms, so 30s is 30000ms.
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"); //Formats the date.
        formatter.setTimeZone(TimeZone.getTimeZone("gmt")); //Time zone is in UTC.
        java.util.Date now = new java.util.Date();
        java.util.Date startDate = new java.util.Date(now.getTime() + DELAY); //Start Date
        java.util.Date endDate = new java.util.Date(startDate.getTime() + delta); //End Date
        startTime = formatter.format(startDate); //Start time
        endTime = formatter.format(endDate); //End time
        return "{ \"start_time\": \""+startTime + "\", \"end_time\": \"" + endTime + "\", \"delta\":  \"" + delta + "\" }";
    }

    /**
     * Three-second delay.
     *
     * @return
     *          The array containing the start and the stop time for the watch.
     */
    @GetMapping("/time")
    @ResponseBody
    public String getDateAndTime() {
        return startTime + ";" + endTime + ";" + delta;
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
            statement.setString(2, reading.getDate_created());
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
