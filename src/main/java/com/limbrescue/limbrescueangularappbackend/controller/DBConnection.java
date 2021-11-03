package com.limbrescue.limbrescueangularappbackend.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private String jdbcURL;
    private String dbUser;
    private String dbPassword;
    private static final Properties p = new Properties();
    private FileReader reader;
    private Connection connection;
    public DBConnection() {
        try {
            reader = new FileReader("src/main/resources/application.properties");
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find the file");
        }

        jdbcURL = p.getProperty("spring.datasource.url");
        dbUser = p.getProperty("spring.datasource.username");
        dbPassword = p.getProperty("spring.datasource.password");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {

        }

    }
    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);
        } catch (SQLException e) {

        }
        return connection;
    }
}
