package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Group;
import org.springframework.web.bind.annotation.*;


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
public class GroupDAO {
    //All attributes read from the properties file.
    private String table;
    private static final Properties p = new Properties();
    private FileReader reader;
    private DBConnection dbConnection;
    public GroupDAO()  {
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
        table = p.getProperty("spring.datasource.GroupTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the group table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the group table.
     * @throws SQLException
     */
    @GetMapping("/allgroups")
    @ResponseBody
    public List<Group> getAllGroups() throws SQLException {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM `" + table + "`";
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

    /**
     * Retrieves a single group based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the group table.
     * @throws SQLException
     */
    @GetMapping("/singlegroup/{id}")
    @ResponseBody
    public Group getGroup(@PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        Group group = null;
        if (result.next()) {
            group = new Group();
            group.setId(id);
            group.setName(result.getString("name"));
            group.setDate_created(result.getDate("date_created"));
        }
        connection.close();
        return group;
    }

    /**
     * Inserts a group to the table.
     *
     * @param group
     *              The group to be inserted.
     * @throws SQLException
     */
    @PostMapping(path = "/group")
    @ResponseBody
    public void insertGroup(@RequestBody Group group) throws SQLException{
        Connection connection = dbConnection.getConnection();
        if (getGroup(group.getId()) != null) {
            updateGroup(group, group.getId());
        }
        else {
            String sql = "INSERT INTO " + table + " (id, name, date_created) VALUES(?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, group.getId());
            statement.setString(2, group.getName());
            statement.setDate(3, group.getDate_created());
            statement.executeQuery();
        }
        connection.close();
    }

    /**
     * Updates a group based on the ID.
     *
     * @param group
     *          The variable values of the columns.
     * @param id
     *          The group ID to be updated.
     * @return
     *          The updated group.
     * @throws SQLException
     */
    @PutMapping(path="/group/{id}")
    @ResponseBody
    public Group updateGroup(@RequestBody Group group, @PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE " + table + " SET name = ?, date_created = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, group.getName());
        statement.setDate(2, group.getDate_created());
        statement.setInt(3, id);
        ResultSet result = statement.executeQuery();
        group.setName(result.getString("name"));
        group.setDate_created(result.getDate("date_created"));
        connection.close();
        return group;
    }

    /**
     * Deletes a Group based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     * @throws SQLException
     */
    @DeleteMapping("/group/{id}")
    @ResponseBody
    public void deleteGroup(@PathVariable("id") int id) throws SQLException{
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeQuery();
        connection.close();
    }
}
