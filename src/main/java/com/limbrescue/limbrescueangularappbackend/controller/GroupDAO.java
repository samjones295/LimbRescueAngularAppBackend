package com.limbrescue.limbrescueangularappbackend.controller;

import com.limbrescue.limbrescueangularappbackend.model.Group;
import org.springframework.web.bind.annotation.*;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

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
            e.printStackTrace();
        }
        try {
            p.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        table = p.getProperty("spring.datasource.GroupTable");
        dbConnection = new DBConnection();
    }

    /**
     * Retrieves all the elements of the group table and stores it in an array list.
     *
     * @return
     *          An arraylist containing the group table.
     */
    @GetMapping("/groups")
    @ResponseBody
    public List<Group> getAllGroups() {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM `" + table + "`";
        List<Group> groups = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                Group group = new Group(result.getInt("id"), result.getString("name"),
                        result.getString("reading_ids"));
                groups.add(group);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    /**
     * Retrieves a single group based on the ID.
     *
     * @param id
     *          The ID to be retrieved
     * @return
     *          A pointer to a tuple in the group table.
     */
//    @GetMapping("/group/{id}")
//    @ResponseBody
    public Group getGroup(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM `" + table + "` WHERE id = ?";
        Group group = null;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                group = new Group();
                group.setId(id);
                group.setName(result.getString("name"));
                group.setReading_ids(result.getString("reading_ids"));
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
        return group;
    }

    /**
     * Retrieves a group with the corresponding name.
     *
     * @param name
     *          The name to search for.
     * @return
     *          The group with the name.
     */
    @GetMapping("/group/{name}")
    @ResponseBody
    public Group getGroupByName(@PathVariable("name") String name) {
        Connection connection = dbConnection.getConnection();
        String sql = "SELECT * FROM `" + table + "` WHERE name = ?";
        Group group = null;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                group = new Group();
                group.setId(result.getInt("id"));
                group.setName(name);
                group.setReading_ids(result.getString("reading_ids"));
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
        return group;
    }
    /**
     * Inserts a group to the table.
     *
     * @param group
     *              The group to be inserted.
     */
    @PostMapping(path = "/group")
    @ResponseBody
    public void insertGroup(@RequestBody Group group) {
        Connection connection = dbConnection.getConnection();
        int id = group.getId();
        while (getGroup(id) != null) {
            id++;
            group.setId(id);
        }
        if (getGroupByName(group.getName()) != null) {
            updateGroupByName(group, group.getName());
        }
        try {
            String sql = "INSERT INTO `" + table + "` (id, name, reading_ids) VALUES(?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, group.getId());
            statement.setString(2, group.getName());
            System.out.println(sortIDs(group.getReading_ids()));
            statement.setString(3, sortIDs(group.getReading_ids()));
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
     * Updates a group based on the ID.
     *
     * @param group
     *          The variable values of the columns.
     * @param id
     *          The group ID to be updated.
     */
//    @PutMapping(path="/group/{id}")
//    @ResponseBody
    public void updateGroup(@RequestBody Group group, @PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE `" + table + "` SET name = ?, reading_ids = ? WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, group.getName());
            statement.setString(2, group.getReading_ids());
            statement.setInt(3, id);
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
     * Updates a group based on the ID.
     *
     * @param group
     *          The variable values of the columns.
     * @param name
     *          The group name to be updated.
     */
    @PutMapping(path="/group/{name}")
    @ResponseBody
    public void updateGroupByName(@RequestBody Group group, @PathVariable("name") String name) {
        Connection connection = dbConnection.getConnection();
        String sql = "UPDATE `" + table + "` SET reading_ids = ? WHERE name = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, group.getReading_ids());
            statement.setString(2, name);
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
     * Deletes a Group based on the ID.
     *
     * @param id
     *          The ID to be deleted.
     */
//    @DeleteMapping("/group/{id}")
//    @ResponseBody
    public void deleteGroup(@PathVariable("id") int id) {
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM `" + table + "` WHERE id = ?";
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
    @DeleteMapping("/group/{name}")
    @ResponseBody
    public void deleteGroupByName(@PathVariable("name") String name) {
        Connection connection = dbConnection.getConnection();
        String sql = "DELETE FROM `" + table + "` WHERE name = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
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
     *
     * @param ids
     *              The list of ids to be sorted
     * @return
     *              The sorted list of IDs.
     */
    private String sortIDs(String ids) {
        String[] nums = ids.split(",");
        Arrays.sort(nums, new Comparator<String>() {
            public int compare(String a, String b) {
                return Integer.parseInt(a) - Integer.parseInt(b);
            }
        });
        Set<String> set = new HashSet<>();
        for (String num : nums) {
            set.add(num);
        }
        String result = "";
        for (String s : set) {
            result = result + s + ",";
        }
        return result.substring(0, result.length() - 1);
    }
}
