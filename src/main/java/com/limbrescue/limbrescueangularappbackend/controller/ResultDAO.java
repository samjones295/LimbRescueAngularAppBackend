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
                Result res = new Result(result.getInt("id"), result.getInt("group_id"), result.getDate("date_ran"), result.getString("algorithm"),
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
                res.setGroup_id(result.getInt("group_id"));
                res.setAlgorithm(result.getString("algorithm"));
                res.setDate_ran(result.getDate("date_ran"));
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
        String sql = "INSERT INTO " + table + " (id, group_id, date_ran, algorithm, train_accuracy, test_accuracy) VALUES(?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, res.getId());
            statement.setInt(2, res.getGroup_id());
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
        String sql = "UPDATE " + table + " SET group_id = ?, algorithm = ?, date_ran = ?, " +
                "train_accuracy = ?, test_accuracy = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, res.getGroup_id());
            statement.setString(2, res.getAlgorithm());
            statement.setInt(3, res.getTrain_accuracy());
            statement.setInt(4, res.getTest_accuracy());
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

//    /**
//     * Updates the comments of a result.
//     *
//     * @param id
//     *          The id to be updated.
//     * @param comment
//     *          The updated comment.
//     */
//    @PutMapping("/resultcomment/{id}")
//    @ResponseBody
//    public void updateComments(@PathVariable("id") int id, @RequestParam String comment) {
//        Connection connection = dbConnection.getConnection();
//        //SQL Update Statement to update comments
//        String sql = "UPDATE " + table + " SET comments = ? WHERE id = ?";
//        try {
//            PreparedStatement statement = connection.prepareStatement(sql);
//            statement.setString(1, comment);
//            statement.setInt(2, id);
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
