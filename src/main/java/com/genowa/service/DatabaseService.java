package com.genowa.service;

import java.sql.*;

public class DatabaseService
{
    private static final String URL = "jdbc:mysql://localhost:3306/genowa";
    private static final String USER = "genowa";
    private static final String PASSWORD = "genowa123";

    private static DatabaseService instance;
    private Connection connection;

    private DatabaseService()
    {
        connect();
    }

    public static synchronized DatabaseService getInstance()
    {
        if (instance == null)
        {
            instance = new DatabaseService();
        }
        return instance;
    }

    private void connect()
    {
        try
        {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully");
        }
        catch (SQLException e)
        {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Connection getConnection()
    {
        try
        {
            if (connection == null || connection.isClosed())
            {
                connect();
            }
        }
        catch (SQLException e)
        {
            connect();
        }
        return connection;
    }

    public boolean validateLogin(String username, String password)
    {
        // Simple validation - in production, use proper password hashing
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql))
        {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            // If users table doesn't exist, allow admin/admin
            return "admin".equals(username) && "admin".equals(password);
        }
    }

    public String getUserRole(String username)
    {
        String sql = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql))
        {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                return rs.getString("role");
            }
        }
        catch (SQLException e)
        {
            // Default role
        }
        return "Administrator";
    }

    public boolean testConnection()
    {
        try
        {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        }
        catch (SQLException e)
        {
            return false;
        }
    }
}
