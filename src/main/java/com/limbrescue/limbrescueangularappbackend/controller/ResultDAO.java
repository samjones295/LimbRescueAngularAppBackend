package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.ml.*;
import com.limbrescue.limbrescueangularappbackend.model.Result;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.*;
import java.util.*;

@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("")
public class ResultDAO {
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
     * Stores the ML results.
     */
    private Map<Integer, List<String>> resultList;
    /**
     * Logger
     */
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ResultDAO.class.getName());
    /**
     * Constructor
     */
    public ResultDAO()  {
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
        table = p.getProperty("spring.datasource.ResultTable");
        dbConnection = new DBConnection();
        //Initializes the result list map.
        resultList = new HashMap<>();
    }

    /**
     * Retrieves all the elements of the results table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the results table.
     */
    @GetMapping("/results")
    @ResponseBody
    public List<Result> getAllResults() {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table; //The SELECT Query
        List<Result> results = new ArrayList<>(); //The array list to store the tuples.
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            //Iterates over the result set and adds into the array list after executing query.
            while (result.next()) {
                Result res = new Result(result.getInt("id"), result.getString("group_name"), result.getDate("date_ran"), result.getString("algorithm"),
                        result.getInt("train_accuracy"), result.getInt("test_accuracy"));
                results.add(res);
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
        return results;
    }

    /**
     * Retrieves a single result based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the results table.
     */
    @GetMapping("/result/{id}")
    @ResponseBody
    public Result getResult(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?"; //The SELECT Query
        Result res = null; //Uses a NULL value if ID is not found.
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            //If found, set return object to be the value of the tuple.
            if (result.next()) {
                res = new Result();
                res.setId(id);
                res.setGroup_name(result.getString("group_name"));
                res.setDate_ran(result.getDate("date_ran"));
                res.setAlgorithm(result.getString("algorithm"));
                res.setTrain_accuracy(result.getInt("train_accuracy"));
                res.setTest_accuracy(result.getInt("test_accuracy"));
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
        return res;
    }

    /**
     * Inserts a result to the table.
     *
     * @param res
     *          The result to be inserted.
     */
    public void insertResult(Result res) {
        Connection connection = dbConnection.getConnection();
        //Updates the ID if necessary to avoid duplicates.
        int id = res.getId();
        while (getResult(id) != null) {
            id++;
            res.setId(id);
        }
        //SQL Insert Statement
        String sql = "INSERT INTO " + table + " (id, group_name, date_ran, algorithm, train_accuracy, test_accuracy) VALUES(?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, res.getId());
            statement.setString(2, res.getGroup_name());
            statement.setDate(3, res.getDate_ran());
            statement.setString(4, res.getAlgorithm());
            statement.setInt(5, res.getTrain_accuracy());
            statement.setInt(6, res.getTest_accuracy());
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
     * Runs a machine learning algorithm before insertion.
     *
     * @param res
     *              the result tuple to be inserted.
     */
    @PostMapping(path = "/result")
    @ResponseBody
    public void runMLAlgorithm(@RequestBody Result res) {
        //Updates the ID if necessary to avoid duplicates.
        int id = res.getId();
        while (getResult(id) != null) {
            id++;
            res.setId(id);
        }
        exportReadingDataToCSV(res);
        //Runs the machine learning and stores the results.
        MachineLearning ml = null;
        //Tha algorithm to run depends on the algorithm stored.
        switch(res.getAlgorithm()) {
            case "Support Vector Machine":
                ml = new SupportVectorMachine();
                break;
            case "Random Forest":
                ml = new RandomForest();
                break;
            case "Naive Bayes":
                ml = new NaiveBayes();
                break;
            case "Multi Layer Perceptron":
                ml = new MultiLayerPerceptron();
                break;
            default:
                LOGGER.warning("Invalid Algorithm");
                break;
        }
        //The output of the ML algorithm.
        List<String> output = ml.run();
        for (String out : output) {
            //Locates the line that contains train and test accuracy.
            if (out.contains("train / test accuracy:")) {
                String accuracy = out.substring(out.indexOf("train / test accuracy:") + 23);
                String[] nums = accuracy.split(" / ");
                res.setTrain_accuracy((int) (Double.parseDouble(nums[0]) * 100)); //Train Accuracy
                res.setTest_accuracy((int) (Double.parseDouble(nums[1]) * 100)); //Test Accuracy
                break;
            }
        }
        resultList.put(res.getId(), ml.run());
        //Insert the result to the SQL.
        insertResult(res);
    }
    /**
     * Updates a result based on the ID.
     *
     * @param res
     *          The variable values of the columns.
     * @param id
     *          The result ID to be updated.
     */
    @PutMapping(path="/result/{id}")
    @ResponseBody
    public void updateResult(@RequestBody Result res, @PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        //SQL Update Statement
        String sql = "UPDATE " + table + " SET group_name = ?, date_ran = ?, algorithm = ?, " +
                "train_accuracy = ?, test_accuracy = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, res.getGroup_name());
            statement.setDate(2, res.getDate_ran());
            statement.setString(3, res.getAlgorithm());
            statement.setInt(4, res.getTrain_accuracy());
            statement.setInt(5, res.getTest_accuracy());
            statement.setInt(6, id);
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
     * Deletes a result based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
    @DeleteMapping("/result/{id}")
    @ResponseBody
    public void deleteResult(@PathVariable("id") int id) {
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

    /**
     * Export the reading data to a .csv
     *
     * @param res
     *          The result tuple
     */
    public void exportReadingDataToCSV(Result res) {
        Connection connection = dbConnection.getConnection();
        String outputFile = "";
        switch(res.getAlgorithm()) {
            case "Support Vector Machine":
                outputFile = p.getProperty("spring.datasource.outputFile") + "/svm/rawdata/files/" + res.getGroup_name() + ".csv";
                break;
            case "Random Forest":
                outputFile = p.getProperty("spring.datasource.outputFile") + "/rf/rawdata/files/" + res.getGroup_name() + ".csv";
                break;
            case "Naive Bayes":
                outputFile = p.getProperty("spring.datasource.outputFile") + "/nb/rawdata/files/" + res.getGroup_name() + ".csv";
                break;
            case "Multi Layer Perceptron":
                outputFile = p.getProperty("spring.datasource.outputFile") + "/mlp/rawdata/files/" + res.getGroup_name() + ".csv";
                break;
        }
        String sql = "(SELECT 'Limb', 'Time', 'Value') UNION " +
                "(SELECT laterality, time, ppg_reading FROM " + p.getProperty("spring.datasource.ReadingDataTable") +
                " WHERE reading_id LIKE (SELECT reading_ids FROM `group` WHERE name = ?))" +
                " INTO OUTFILE '" + outputFile + "' FIELDS ENCLOSED BY '\"' TERMINATED BY ',' ESCAPED BY '\"' LINES TERMINATED BY '\n'";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, res.getGroup_name());
            statement.executeQuery();
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
