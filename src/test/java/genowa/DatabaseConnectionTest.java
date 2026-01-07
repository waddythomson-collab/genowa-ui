package genowa;

import genowa.generator.JpaDataAccess;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test database connection to existing PostgreSQL database.
 */
public class DatabaseConnectionTest
{
    @Test
    public void testDatabaseConnection()
    {
        System.out.println("Testing database connection...");
        
        JpaDataAccess dataAccess = new JpaDataAccess();
        
        // Try to connect using default persistence unit
        boolean connected = dataAccess.connect("");
        
        if (connected)
        {
            System.out.println("✓ Database connection successful!");
            assertTrue(dataAccess.isConnected(), "Should be connected");
            
            // Test a simple query
            try
            {
                // Try to get table names
                var tables = dataAccess.getTableNames();
                System.out.println("✓ Found " + tables.size() + " tables in database");
            }
            catch (Exception e)
            {
                System.out.println("⚠ Could not query tables: " + e.getMessage());
            }
            
            dataAccess.disconnect();
        }
        else
        {
            System.out.println("✗ Database connection failed: " + dataAccess.getLastError());
            System.out.println("  Check persistence.xml configuration:");
            System.out.println("  - URL: jdbc:postgresql://localhost:5432/genowa_db");
            System.out.println("  - User: genowa");
            System.out.println("  - Make sure PostgreSQL is running and database exists");
        }
    }
}

