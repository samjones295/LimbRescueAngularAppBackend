package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Patient;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("")
public class PatientDAO {
    //All attributes read from the properties file.
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public PatientDAO() {
        //Determine what file to read
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find the file");
        }
        try {
            p.load(reader);
        } catch (IOException e) {
            System.out.println("Cannot load file");
        }
        table = p.getProperty("spring.datasource.PatientTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the patients table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the group readings table.
     * @throws SQLException
     */
    @GetMapping("/allpatients")
    @ResponseBody
    public List<Patient> getAllPatients() throws SQLException {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<Patient> readings = new ArrayList<>();
        while (result.next()) {
            Patient data = new Patient(result.getInt("id"), result.getString("patient_no"), result.getString("status"));
            readings.add(data);
        }
        connection.close();
        return readings;
    }

    /**
     * Retrieves a single patient based on the ID.
     *
     * @param id
     * @return
     * @throws SQLException
     */
    @GetMapping("/singlepatient/{id}")
    @ResponseBody
    public Patient getPatient(@PathVariable("id")  int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        Patient patient = null;
        if (result.next()) {
            patient = new Patient();
            patient.setId(id);
            patient.setPatient_no(result.getString("patient_no"));
            patient.setStatus(result.getString("status"));
        }
        connection.close();
        return patient;
    }

    /**
     * Inserts a patient to the table.
     *
     * @param patient
     *              The patient to be inserted.
     * @throws SQLException
     */
    @PostMapping(path = "/patient")
    @ResponseBody
    public void insertPatient(@RequestBody Patient patient) throws SQLException{
        Connection connection = dbConnection.getConnection();
        if (getPatient(patient.getId()) != null) {
            updatePatient(patient, patient.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " VALUES(id = ?, patient_no = ?, status = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, patient.getId());
            statement.setString(2, patient.getPatient_no());
            statement.setString(3, patient.getStatus());
            statement.executeQuery();
        }
        connection.close();
    }

    /**
     *
     * @param patient
     *              The variable values of the columns.
     * @param id
     *              The group reading ID to be updated.
     * @return
     *              The updated patient.
     * @throws SQLException
     */
    @PutMapping(path="/patient/{id}")
    @ResponseBody
    public Patient updatePatient(@RequestBody Patient patient, @PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET patient_no = ?, status = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, patient.getPatient_no());
        statement.setString(2, patient.getStatus());
        statement.setInt(3, id);
        ResultSet result = statement.executeQuery();
        patient.setPatient_no(result.getString("patient_no"));
        patient.setStatus(result.getString("status"));
        connection.close();
        return patient;
    }

    /**
     * Deletes a patient based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     * @throws SQLException
     */
    @DeleteMapping("/patient/{id}")
    @ResponseBody
    public void deletePatient(@PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
