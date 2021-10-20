package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Result;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ResultDAO {
    private String jdbcURL;
    private String dbUser;
    private String dbPassword;
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private Connection connection;
    public ResultDAO() throws FileNotFoundException, ClassNotFoundException {
        reader = new FileReader("application.properties");
        jdbcURL = p.getProperty("spring.datasource.url");
        dbUser = p.getProperty("spring.datasource.username");
        dbPassword = p.getProperty("spring.datasource.password");
        Class.forName("com.mysql.jdbc.Driver");
        table = p.getProperty("spring.datasource.ResultTable");
    }
    public List<Result> getAllResults() throws SQLException {
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<Result> results = new ArrayList<>();
        while (result.next()) {
            Result res = new Result(result.getInt("id"), result.getInt("group_id"), result.getString("algorithm"),
                    result.getInt("ran_by"), result.getString("status"), result.getString("comments"));
            results.add(res);
        }
        connection.close();
        return results;
    }
    public Result getResult(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        Result res = null;
        if (result.next()) {
            res = new Result();
            res.setId(id);
            res.setGroup_id(result.getInt("group_id"));
            res.setAlgorithm(result.getString("algorithm"));
            res.setRan_by(result.getInt("ran_by"));
            res.setStatus(result.getString("status"));
            res.setComments(result.getString("comments"));
        }
        connection.close();
        return res;
    }
    public void insertResult(Result res) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "INSERT INTO " + table + " VALUES(id = ?, group_id = ?, algorithm = ?, ran_by = ? status = ?, comments = ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, res.getId());
        statement.setInt(2, res.getGroup_id());
        statement.setString(3, res.getAlgorithm());
        statement.setInt(4, res.getRan_by());
        statement.setString(5, res.getStatus());
        statement.setString(6, res.getComments());
        ResultSet result = statement.executeQuery();
        connection.close();
    }
    public void updateResult(Result res, int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "UPDATE " + table + " SET group_id = ?, algorithm = ?, ran_by = ?, status = ?, comments = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, res.getGroup_id());
        statement.setString(2, res.getAlgorithm());
        statement.setInt(3, res.getRan_by());
        statement.setString(4, res.getStatus());
        statement.setString(5, res.getComments());
        statement.setInt(6, res.getId());
        ResultSet result = statement.executeQuery();
        connection.close();
    }
    public void deleteResult(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        connection.close();
    }
    public void exportResultsToCSV() throws SQLException{
        //List<Result> results = getAllResults();
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String outputFile = p.getProperty("spring.datasource.OutputFile");
        String sql = "(SELECT 'ID', 'Group ID', 'Algorithm', 'Ran By', 'Status', 'Comments') UNION (SELECT * FROM " + table +
                " ) INTO OUTFILE '" + outputFile + "' FIELDS ENCLOSED BY '\"' TERMINATED BY ',' ESCAPED BY '\"' LINES TERMINATED BY '\n";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        connection.close();
    }
}
