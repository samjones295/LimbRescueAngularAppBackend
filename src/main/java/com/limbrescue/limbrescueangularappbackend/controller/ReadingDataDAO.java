package com.limbrescue.limbrescueangularappbackend.controller;


import com.limbrescue.limbrescueangularappbackend.model.ReadingData;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.*;

import java.net.URISyntaxException;
import java.nio.file.Files;
import com.opencsv.CSVWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET, RequestMethod.POST})
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
     *
     *
     * @param reading_id
     *                  the reading_id
     * @param laterality
     *                  the laterality
     * @return a csv file for reading_Data
     *
     */
    @GetMapping(value="/output", params={"reading_id","laterality"})
    @ResponseBody
    public ResponseEntity<Object> getAllReadingDataOfReadingId_toJSON(@RequestParam("reading_id") int reading_id, @RequestParam("laterality") String laterality, HttpServletResponse res) throws IOException, URISyntaxException {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE reading_id=? AND laterality=?"; //The SELECT Query
        List<ReadingData> readings = new ArrayList<>(); //The array list to store the tuples.
        JSONArray output_list = new JSONArray();
        try {

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, reading_id);
            statement.setString(2, laterality);
            ResultSet result = statement.executeQuery();
            //Iterates over the result set and adds into the array list after executing query.
            while (result.next()) {
                JSONObject output = new JSONObject();
                output.put("time",Double.toString(result.getDouble("time")));
                output.put("ppg",result.getString("ppg_reading"));
                output.put("laterality",result.getString("laterality"));
                output_list.put(output);

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
        return new ResponseEntity<>(output_list.toList(), HttpStatus.OK);



//        FileWriter output = new FileWriter("outputfile.csv");
//        CSVWriter writer= new CSVWriter(output);
//        String[]header = {"time","ppg_reading","reading_id","laterality"};
//        writer.writeNext(header);
//        for (ReadingData data:readings)
//        {
//            String[] data_read = {Integer.toString(data.getReading_id()),Double.toString(data.getTime()),data.getPpg_reading(),data.getLaterality()};
//            writer.writeNext(data_read);
//        }
//        writer.close();;

//        File file_download =new File("outputfile.csv");












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
     * Inserts all reading data into the table sequentially over a single connection.
     * @param reading_id
     *          The reading ID
     * @param time
     *          The List of all reading times
     * @param value
     *          The List reading of all reading values
     * @param laterality
     *          The laterality
     */
public void insertReadingData(int reading_id, List<Double> time, List<String> value, String laterality) {
    Connection connection = dbConnection.getConnection();
    int id = 8759;

    String selectHighestID = "SELECT id FROM reading_data ORDER BY id DESC LIMIT 1";
    PreparedStatement selection;
    ResultSet result;
    try {
        selection = connection.prepareStatement(selectHighestID);
        result = selection.executeQuery();
        while (result.next()) { // While the result has options, should only run once.
            try {
                id = result.getInt("id");
                id++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } catch (SQLException e1) {
        e1.printStackTrace();
    }
   

    //SQL Insert Statement
    String sql = "INSERT INTO " + table + " (id, reading_id, time, ppg_reading, laterality) VALUES(?, ?, ?, ?, ?)";

    try {
        for (int i = 0; i < time.size(); i++) {
            PreparedStatement statement = connection.prepareStatement(sql); // Object that holds SQL query.

            statement.setInt(1, id + i);
            statement.setInt(2, reading_id);
            statement.setDouble(3, time.get(i));
            statement.setString(4, value.get(i));
            statement.setString(5, laterality);
            statement.executeUpdate();  // Executes the SQL query.
        }
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
     *              The data to be parsed and sent to the database.
     */
    @PostMapping(path = "/data")
    @ResponseBody
    public void parseData(@RequestBody String s) {
        JSONObject data = new JSONObject(s);
        int reading_id = data.getInt("reading_id");
        String laterality = data.getString("laterality");

        if (laterality.equals("LEFT_ARM_BILATERAL")) {
            laterality = "LEFT_ARM";
        } else if (laterality.equals("RIGHT_ARM_BILATERAL")){
            laterality = "RIGHT_ARM";
        }

        JSONArray ppg_reading = data.getJSONArray("ppg_reading");
        JSONObject readings = ppg_reading.getJSONObject(0);
        JSONArray reading_data = readings.getJSONArray("readings");

        //Update the time and ppg reading attributes.
        List<Double> time = new ArrayList<Double>();
        List<String> value = new ArrayList<String>(); // Assuming that String is declared for a reason, so preserving this for now.
        int length = reading_data.length();
        
        for(int i = 0; i < length; i++){
            time.add(reading_data.getJSONObject(i).getDouble("time"));
            value.add(reading_data.getJSONObject(i).getDouble("value")+"");
        }

        // Send the data to be sent to the database.
        insertReadingData(reading_id, time, value, laterality);
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
