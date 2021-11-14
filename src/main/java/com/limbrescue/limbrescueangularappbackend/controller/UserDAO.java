package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Result;
import com.limbrescue.limbrescueangularappbackend.model.User;

import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@CrossOrigin(origins="http://localhost:8081")
@RestController
@RequestMapping("")
public class UserDAO {
    //All attributes read from the properties file.
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    public UserDAO()  {
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
        table = p.getProperty("spring.datasource.UserTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the users table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the users table.
     */
    @GetMapping("/users")
    @ResponseBody
    public List<User> getAllUsers() {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table;
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                User user = new User(result.getInt("id"), result.getString("email"),
                        result.getString("username"), result.getString("password"),
                        result.getDate("date_created"), result.getDate("last_updated"));
                users.add(user);
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
        return users;
    }

    /**
     * Retrieves a single user based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the results table.
     */
    @GetMapping("/user/{id}")
    @ResponseBody
    public User getUser(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                user = new User();
                user.setId(id);
                user.setEmail(result.getString("email"));
                user.setUsername(result.getString("username"));
                user.setPassword(result.getString("password"));
                user.setDate_created(result.getDate("date_created"));
                user.setLast_updated(result.getDate("last_updated"));
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
        return user;
    }

    /**
     * Inserts a user to the table.
     *
     * @param user
     *          The user to be inserted.
     */
    @PostMapping(path = "/user")
    @ResponseBody
    public void insertUser(@RequestBody User user) {
        Connection connection = dbConnection.getConnection();
        if (getUser(user.getId()) != null) {
            updateUser(user, user.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " (id, email, username, password, date_created, last_updated) VALUES(?, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, user.getId());
                statement.setString(2, user.getEmail());
                statement.setString(3, user.getUsername());
                statement.setString(4, user.getPassword());
                statement.setDate(5, user.getDate_created());
                statement.setDate(6, user.getLast_updated());
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
     * Updates a user based on the ID.
     *
     * @param user
     *          The variable values of the columns.
     * @param id
     *          The user ID to be updated.
     */
    @PutMapping(path="/user/{id}")
    @ResponseBody
    public void updateUser(@RequestBody User user, @PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET email = ?, username = ?, password = ?, date_created = ?, last_updated = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setDate(4, user.getDate_created());
            statement.setDate(5, user.getLast_updated());
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
     * Deletes a user based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
    @DeleteMapping("/user/{id}")
    @ResponseBody
    public void deleteUser(@PathVariable("id") int id) {
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
     * Check to see if credentials are valid
     *
     * @param username
     *                  The username.
     * @param password
     *                  The password.
     * @return
     *                  A pointer to the tuple.
     */
    @GetMapping("/logincheck/{username}/{password}")
    @ResponseBody
    public User checkLogin(@PathVariable("username") String username, @PathVariable("password") String password) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM users WHERE username = ? and password = ?";
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                user = new User();
                user.setEmail(result.getString("email"));
                user.setUsername(username);
                user.setPassword(password);
                user.setDate_created(result.getDate("date_created"));
                user.setLast_updated(result.getDate("last_updated"));
            } else {
                LOGGER.info("Invalid credentials");
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
        return user;
    }
}
