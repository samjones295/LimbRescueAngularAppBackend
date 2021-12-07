package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Patient;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET, RequestMethod.POST})
@RestController
@RequestMapping("")
public class PatientDAO {
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
    public PatientDAO() {
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
        table = p.getProperty("spring.datasource.PatientTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the patients table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the group readings table.
     */
    @GetMapping("/patients")
    @ResponseBody
    public List<Patient> getAllPatients() {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table; //The SELECT Query
        List<Patient> readings = new ArrayList<>(); //The array list to store the tuples.
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            //Iterates over the result set and adds into the array list after executing query.
            while (result.next()) {
                Patient data = new Patient(result.getInt("id"), result.getString("patient_no"), result.getString("status"));
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
     * Retrieves a single patient based on the ID.
     *
     * @param id
     *          The id of the patient.
     * @return
     *          A pointer to a tuple in the patients table.
     */
    @GetMapping("/patient/{id}")
    @ResponseBody
    public Patient getPatient(@PathVariable("id")  int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?"; //The SELECT Query
        Patient patient = null; //Uses a NULL value if ID is not found.
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            //If found, set return object to be the value of the tuple.
            if (result.next()) {
                patient = new Patient();
                patient.setId(id);
                patient.setPatient_no(result.getString("patient_no"));
                patient.setStatus(result.getString("status"));
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
        return patient;
    }

    /**
     * Inserts a patient to the table.
     *
     * @param patient
     *              The patient to be inserted.
     */
    @PostMapping(path = "/patient")
    @ResponseBody
    public void insertPatient(@RequestBody Patient patient) {
        Connection connection = dbConnection.getConnection();
        //Updates the ID if necessary to avoid duplicates.
        int id = patient.getId();
        while (getPatient(id) != null) {
            id++;
            patient.setId(id);
        }
        //SQL Insert Statement
        String sql = "INSERT INTO " + table + " (id, patient_no, status) VALUES(?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, patient.getId());
            statement.setString(2, patient.getPatient_no());
            statement.setString(3, patient.getStatus());
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
     *
     * @param patient
     *              The variable values of the columns.
     * @param id
     *              The group reading ID to be updated.
     */
    @PutMapping(path="/patient/{id}")
    @ResponseBody
    public void updatePatient(@RequestBody Patient patient, @PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        //SQL Update Statement
        String sql = "UPDATE " + table + " SET patient_no = ?, status = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patient.getPatient_no());
            statement.setString(2, patient.getStatus());
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
     * Deletes a patient based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
    @DeleteMapping("/patient/{id}")
    @ResponseBody
    public void deletePatient(@PathVariable("id") int id) {
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
