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



    public void initializeDatabase() {
        try (Connection connection = getConnection()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS wynaut_rank_up_player_data (
                    player_id VARCHAR(36) PRIMARY KEY,
                    elo INTEGER DEFAULT 1000
                );
                
            """;
            connection.createStatement().execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
