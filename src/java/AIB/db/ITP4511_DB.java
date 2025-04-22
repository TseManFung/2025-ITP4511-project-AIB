/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AIB.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author ku123
 */
public class ITP4511_DB {
    // Database connection parameters
    private   String DB_URL ;
    private   String DB_USER ;
    private   String DB_PASSWORD; 
    
    public ITP4511_DB(String url, String user, String password){
        this.DB_URL = url;
        this.DB_USER = user;
        this.DB_PASSWORD = password;
    }
   
    /**
     * Gets a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Closes a database connection
     * @param conn Connection to close
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Log error but don't throw as this is typically in finally blocks
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection (optional)
     */
    public void testConnection() {
        Connection conn = null;
        try {
            conn = this.getConnection();
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        } finally {
            this.closeConnection(conn);
        }
    }

    public void createDB() throws SQLException, IOException {
        Connection conn = null;
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();

            String aibDbSql = readFile("AIBDB.sql");
            String testDataSql = readFile("testData.sql");

            stmt.execute(aibDbSql);
            stmt.execute(testDataSql);

            stmt.close();
        } finally {
            closeConnection(conn);
        }
    }

    private String readFile(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}