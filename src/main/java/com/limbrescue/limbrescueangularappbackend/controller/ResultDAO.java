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
        String sql = "SELECT * FROM " + table;
        List<Result> results = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Result res = new Result(result.getInt("id"), result.getInt("group_id"), result.getString("algorithm"),
                        result.getInt("ran_by"), result.getString("status"), result.getString("comments"),
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
        String sql = "INSERT INTO " + table + " (id, group_id, algorithm, ran_by, status, comments, train_accuracy, test_accuracy) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, res.getId());
            statement.setInt(2, res.getGroup_id());
            statement.setString(3, res.getAlgorithm());
            statement.setInt(4, res.getRan_by());
            statement.setString(5, res.getStatus());
            statement.setString(6, res.getComments());
            statement.setInt(7, res.getTrain_accuracy());
            statement.setInt(8, res.getTest_accuracy());
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
        //Runs the machine learning and stores the results.
        MachineLearning ml = null;
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
        List<String> output = ml.run();
        for (String out : output) {
            if (out.contains("train / test accuracy:")) {
                String accuracy = out.substring(out.indexOf("train / test accuracy:") + 23);
                String[] nums = accuracy.split(" / ");
                System.out.println(Arrays.toString(nums));
                res.setTrain_accuracy((int) (Double.parseDouble(nums[0]) * 100));
                res.setTest_accuracy((int) (Double.parseDouble(nums[1]) * 100));
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
        String sql = "UPDATE " + table + " SET group_id = ?, algorithm = ?, ran_by = ?, status = ?, comments = ?," +
                "train_accuracy = ?, test_accuracy = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, res.getGroup_id());
            statement.setString(2, res.getAlgorithm());
            statement.setInt(3, res.getRan_by());
            statement.setString(4, res.getStatus());
            statement.setString(5, res.getComments());
            statement.setInt(6, res.getTrain_accuracy());
            statement.setInt(7, res.getTest_accuracy());
            statement.setInt(8, id);
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

//    /**
//     * Exports the results to a .txt file.
//     * @param id
//     *          The id to export the results.
//     */
//    @GetMapping(path = "/report/{id}")
//    @ResponseBody
//    public void exportResultsToFile(@PathVariable("id") int id) {
//        Result res = getResult(id);
//        try {
//            FileWriter writer = null;
//            //Output depends on which algorithm is being run.
//            switch(res.getAlgorithm()) {
//                case "Support Vector Machine":
//                    writer = new FileWriter(p.getProperty("spring.datasource.SVM"));
//                    writer.write("{\n");
//                    writer.write("\t" + resultList.get(id).get(29).substring(resultList.get(id).get(29).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(32).substring(resultList.get(id).get(32).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(33) + "\n");
//                    writer.write("\t" + resultList.get(id).get(34) + "\n");
//                    writer.write("\t" + resultList.get(id).get(35) + "\n");
//                    writer.write("\t" + resultList.get(id).get(36) + "\n");
//                    writer.write("\t" + resultList.get(id).get(37).substring(resultList.get(id).get(37).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(38) + "\n");
//                    writer.write("\t" + resultList.get(id).get(39) + "\n");
//                    writer.write("\t" + resultList.get(id).get(40) + "\n");
//                    writer.write("\t" + resultList.get(id).get(41) + "\n");
//                    writer.write("}\n");
//                    break;
//                case "Random Forest":
//                    writer = new FileWriter(p.getProperty("spring.datasource.RF"));
//                    writer.write("{\n");
//                    writer.write("\t" + resultList.get(id).get(32).substring(resultList.get(id).get(32).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(35).substring(resultList.get(id).get(35).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(36) + "\n");
//                    writer.write("\t" + resultList.get(id).get(37) + "\n");
//                    writer.write("\t" + resultList.get(id).get(38) + "\n");
//                    writer.write("\t" + resultList.get(id).get(39) + "\n");
//                    writer.write("\t" + resultList.get(id).get(40).substring(resultList.get(id).get(40).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(41) + "\n");
//                    writer.write("\t" + resultList.get(id).get(42) + "\n");
//                    writer.write("\t" + resultList.get(id).get(43) + "\n");
//                    writer.write("\t" + resultList.get(id).get(44) + "\n");
//                    writer.write("}\n");
//                    break;
//                case "Naive Bayes":
//                    writer = new FileWriter(p.getProperty("spring.datasource.NB"));
//                    writer.write("{\n");
//                    writer.write("\t" + resultList.get(id).get(16).substring(resultList.get(id).get(16).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(19).substring(resultList.get(id).get(19).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(20) + "\n");
//                    writer.write("\t" + resultList.get(id).get(21) + "\n");
//                    writer.write("\t" + resultList.get(id).get(22) + "\n");
//                    writer.write("\t" + resultList.get(id).get(23) + "\n");
//                    writer.write("\t" + resultList.get(id).get(24).substring(resultList.get(id).get(24).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(25) + "\n");
//                    writer.write("\t" + resultList.get(id).get(26) + "\n");
//                    writer.write("\t" + resultList.get(id).get(27) + "\n");
//                    writer.write("\t" + resultList.get(id).get(28) + "\n");
//                    writer.write("}\n");
//                    break;
//                case "Multi Layer Perceptron":
//                    writer = new FileWriter(p.getProperty("spring.datasource.MLP"));
//                    writer.write("{\n");
//                    writer.write("\t" + resultList.get(id).get(37).substring(resultList.get(id).get(37).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(40).substring(resultList.get(id).get(40).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(41) + "\n");
//                    writer.write("\t" + resultList.get(id).get(42) + "\n");
//                    writer.write("\t" + resultList.get(id).get(43) + "\n");
//                    writer.write("\t" + resultList.get(id).get(44) + "\n");
//                    writer.write("\t" + resultList.get(id).get(45).substring(resultList.get(id).get(45).indexOf("0m") + 3) + "\n");
//                    writer.write("\t" + resultList.get(id).get(46) + "\n");
//                    writer.write("\t" + resultList.get(id).get(47) + "\n");
//                    writer.write("\t" + resultList.get(id).get(48) + "\n");
//                    writer.write("\t" + resultList.get(id).get(49) + "\n");
//                    writer.write("}\n");
//                    break;
//                default:
//                    LOGGER.warning("Invalid Algorithm");
//                    break;
//            }
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
