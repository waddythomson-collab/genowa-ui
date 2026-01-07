package genowa;

import genowa.generator.JpaDataAccess;

/**
 * Simple test to verify database connection to existing PostgreSQL database.
 * Run this as: java genowa.DatabaseConnectionTest
 */
public class DatabaseConnectionTest
{
    public static void main(String[] args)
    {
        System.out.println("========================================");
        System.out.println("Testing database connection...");
        System.out.println("========================================");
        
        JpaDataAccess dataAccess = new JpaDataAccess();
        
        // Try to connect using default persistence unit
        System.out.println("Attempting to connect to PostgreSQL...");
        System.out.println("  URL: jdbc:postgresql://localhost:5432/genowa_db");
        System.out.println("  User: genowa");
        
        boolean connected = dataAccess.connect("");
        
        if (connected)
        {
            System.out.println("✓ Database connection successful!");
            System.out.println("  Connection status: " + (dataAccess.isConnected() ? "CONNECTED" : "NOT CONNECTED"));
            
            // Test a simple query
            try
            {
                System.out.println("\nTesting query capabilities...");
                var tables = dataAccess.getTableNames();
                System.out.println("✓ Found " + tables.size() + " tables in database");
                
                if (!tables.isEmpty())
                {
                    System.out.println("  Sample tables:");
                    int count = Math.min(5, tables.size());
                    for (int i = 0; i < count; i++)
                    {
                        System.out.println("    - " + tables.get(i));
                    }
                    if (tables.size() > count)
                    {
                        System.out.println("    ... and " + (tables.size() - count) + " more");
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("⚠ Could not query tables: " + e.getMessage());
                e.printStackTrace();
            }
            
            dataAccess.disconnect();
            System.out.println("\n✓ Database connection test completed successfully!");
        }
        else
        {
            System.out.println("✗ Database connection failed!");
            System.out.println("  Error: " + dataAccess.getLastError());
            System.out.println("\nTroubleshooting:");
            System.out.println("  1. Check if PostgreSQL is running:");
            System.out.println("     psql -U genowa -d genowa_db");
            System.out.println("  2. Verify database exists:");
            System.out.println("     CREATE DATABASE genowa_db;");
            System.out.println("  3. Check persistence.xml configuration");
            System.out.println("  4. Verify user 'genowa' has access to database");
        }
        
        System.out.println("========================================");
    }
}

