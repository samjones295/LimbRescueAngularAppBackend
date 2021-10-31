package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Result;
import com.limbrescue.limbrescueangularappbackend.model.User;
import org.springframework.web.bind.annotation.*;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("/api/v1")
public class UserDAO {
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public UserDAO()  {
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find the file");
        }
        table = p.getProperty("spring.datasource.UserTable");
        dbConnection = new DBConnection();
    }
    @GetMapping("/allusers")
    public List<User> getAllUsers() throws SQLException {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
        List<User> users = new ArrayList<>();
        while (result.next()) {
            User user = new User(result.getInt("id"), result.getString("email"),
                    result.getString("username"), result.getString("password"),
                    result.getDate("date_created"), result.getDate("last_updated"));
            users.add(user);
        }
        connection.close();
        return users;
    }
    public User getUser(int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        User user = null;
        if (result.next()) {
            user = new User();
            user.setId(id);
            user.setEmail(result.getString("email"));
            user.setUsername(result.getString("username"));
            user.setPassword(result.getString("password"));
            user.setDate_created(result.getDate("date_created"));
            user.setLast_updated(result.getDate("last_updated"));
        }
        connection.close();
        return user;
    }
    @PostMapping(path = "/user")
    public void insertUser(User user) throws SQLException{
        Connection connection = dbConnection.getConnection();
        if (getUser(user.getId()) != null) {
            updateUser(user, user.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " VALUES(id = ?, email = ?, username = ?, password = ?, date_created = ?, last_updated = ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, user.getId());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setDate(5, user.getDate_created());
            statement.setDate(6, user.getLast_updated());
            statement.executeQuery();
        }
        connection.close();
    }
    public User updateUser(User user, int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET email = ?, username = ?, password = ?, date_created = ?, last_updated = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPassword());
        statement.setDate(4, user.getDate_created());
        statement.setDate(5, user.getLast_updated());
        statement.setInt(6, id);
        ResultSet result = statement.executeQuery();
        user.setEmail(result.getString("email"));
        user.setUsername(result.getString("username"));
        user.setPassword(result.getString("password"));
        user.setDate_created(result.getDate("date_created"));
        user.setLast_updated(result.getDate("last_updated"));
        connection.close();
        return user;
    }
    public void deleteUser(int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
    public User checkLogin(String username, String password) throws SQLException {
        //Class.forName("com.mysql.jdbc.Driver");
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM users WHERE username = ? and password = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet result = statement.executeQuery();
        User user = null;
        if (result.next()) {
            user = new User();
            user.setEmail(result.getString("email"));
            user.setUsername(username);
            user.setPassword(password);
            user.setDate_created(result.getDate("date_created"));
            user.setLast_updated(result.getDate("last_updated"));
        }
        connection.close();
        return user;
    }
}
