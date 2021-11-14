package com.limbrescue.limbrescueangularappbackend.controller;

import ch.qos.logback.classic.Logger;
import com.limbrescue.limbrescueangularappbackend.ml.MultiLayerPerceptron;
import com.limbrescue.limbrescueangularappbackend.ml.NaiveBayes;
import com.limbrescue.limbrescueangularappbackend.ml.RandomForest;
import com.limbrescue.limbrescueangularappbackend.ml.SupportVectorMachine;
import com.limbrescue.limbrescueangularappbackend.model.Result;
import com.limbrescue.limbrescueangularappbackend.model.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("")
public class ResultDAO {
    //All attributes read from the properties file.
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ResultDAO.class.getName());
    public ResultDAO()  {
        //Determine what file to read
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            p.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        table = p.getProperty("spring.datasource.ResultTable");
        dbConnection = new DBConnection();
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
        String sql = "SELECT * FROM " + table;
        List<Result> results = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Result res = new Result(result.getInt("id"), result.getInt("group_id"), result.getString("algorithm"),
                        result.getInt("ran_by"), result.getString("status"), result.getString("comments"));
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
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        Result res = null;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                res = new Result();
                res.setId(id);
                res.setGroup_id(result.getInt("group_id"));
                res.setAlgorithm(result.getString("algorithm"));
                res.setRan_by(result.getInt("ran_by"));
                res.setStatus(result.getString("status"));
                res.setComments(result.getString("comments"));
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
    @PostMapping(path = "/result")
    @ResponseBody
    public void insertResult(@RequestBody Result res) {
        Connection connection = dbConnection.getConnection();
        if (getResult(res.getId()) != null) {
            updateResult(res, res.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " (id, group_id, algorithm, ran_by, status, comments) VALUES(?, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, res.getId());
                statement.setInt(2, res.getGroup_id());
                statement.setString(3, res.getAlgorithm());
                statement.setInt(4, res.getRan_by());
                statement.setString(5, res.getStatus());
                statement.setString(6, res.getComments());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        String sql = "UPDATE " + table + " SET group_id = ?, algorithm = ?, ran_by = ?, status = ?, WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, res.getGroup_id());
            statement.setString(2, res.getAlgorithm());
            statement.setInt(3, res.getRan_by());
            statement.setString(4, res.getStatus());
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
     * Updates the comments of a result.
     *
     * @param id
     *          The id to be updated.
     * @param comment
     *          The updated comment.
     */
    @PutMapping("/resultcomment/{id}")
    @ResponseBody
    public void updateComments(@PathVariable("id") int id, @RequestParam String comment) {
        Connection connection = dbConnection.getConnection();
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
     * Deletes a result based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
    @DeleteMapping("/result/{id}")
    @ResponseBody
    public void deleteResult(@PathVariable("id") int id) {
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

    /**
     * Exports the results to a .csv file.
     */
    @GetMapping(path = "/viewreport/{id}")
    @ResponseBody
    public void exportResultsToFile(@PathVariable("id") int id) {
//        Connection connection = dbConnection.getConnection();
        String outputFile = p.getProperty("spring.datasource.OutputFile");
//        String sql = "(SELECT 'ID', 'Group ID', 'Algorithm', 'Ran By', 'Status', 'Comments') UNION (SELECT * FROM " + table +
//                " ) INTO OUTFILE '" + outputFile + "' FIELDS ENCLOSED BY '\"' TERMINATED BY ',' ESCAPED BY '\"' LINES TERMINATED BY '\\n'";
        //String sql = "SELECT * FROM " + table + " WHERE id = " + id;
        List<String> list = new ArrayList<>();
//        try {
//            PreparedStatement statement = connection.prepareStatement(sql);
//            ResultSet result = statement.executeQuery();
        Result res = getResult(id);
        if (res != null) {
            switch(res.getAlgorithm()) {
                case "Support Vector Machine":
                    SupportVectorMachine svm = new SupportVectorMachine();
                    list = svm.run();
                    break;
                case "Random Forest":
                    RandomForest rf = new RandomForest();
                    list = rf.run();
                    break;
                case "Naive Bayes":
                    NaiveBayes nb = new NaiveBayes();
                    list = nb.run();
                    break;
                case "Multi Layer Perceptron":
                    MultiLayerPerceptron mlp = new MultiLayerPerceptron();
                    list = mlp.run();
                    break;
                default:
                    LOGGER.warning("Invalid Algorithm");
                    break;
            }
        }

        try {
            FileWriter writer = new FileWriter(outputFile);
            for (String s : list) {
                writer.write(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
