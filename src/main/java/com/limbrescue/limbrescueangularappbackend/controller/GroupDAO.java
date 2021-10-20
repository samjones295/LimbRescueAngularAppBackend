package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Group;
import com.limbrescue.limbrescueangularappbackend.model.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GroupDAO {
    private String jdbcURL;
    private String dbUser;
    private String dbPassword;
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private Connection connection;
    public GroupDAO() throws FileNotFoundException, ClassNotFoundException {
        reader = new FileReader("application.properties");
        jdbcURL = p.getProperty("spring.datasource.url");
        dbUser = p.getProperty("spring.datasource.username");
        dbPassword = p.getProperty("spring.datasource.password");
        Class.forName("com.mysql.jdbc.Driver");
        table = p.getProperty("spring.datasource.GroupTable");
    }
    public List<Group> getAll() throws SQLException {
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<Group> groups = new ArrayList<>();
        while (result.next()) {
            Group group = new Group(result.getInt("id"), result.getString("name"), result.getDate("date_created"));
            groups.add(group);
        }
        connection.close();
        return groups;
    }
    public void insertGroup(Group group) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "INSERT INTO " + table + " VALUES(id = ?, name = ?, date_created = ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, group.getId());
        statement.setString(2, group.getName());
        statement.setDate(3, group.getDate_created());
        ResultSet result = statement.executeQuery();
        connection.close();
    }
    public void updateGroup(Group group, int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "UPDATE " + table + " SET name = ?, date_created = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, group.getName());
        statement.setDate(2, group.getDate_created());
        statement.setInt(3, group.getId());
        ResultSet result = statement.executeQuery();
        connection.close();
    }
    public void deleteGroup(int id) throws SQLException{
        connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        connection.close();
    }
}
