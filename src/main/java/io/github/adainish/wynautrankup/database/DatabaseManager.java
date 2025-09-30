package io.github.adainish.wynautrankup.database;

import net.impactdev.impactor.relocations.com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager
{
    public String DATABASE_URL = "";
    public Driver driver;
    public DatabaseManager()
    {
        try {
            Class.forName("net.impactdev.impactor.relocations.com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.driver = new Driver();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public void closeDatabase() {
        // shutting down the database connection
        try {
            getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
