package io.github.adainish.wynautrankup.database;

import io.github.adainish.wynautrankup.WynautRankUp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MatchResultRecorder
{
    public void recordMatch(UUID winnerId, UUID loserId, int winnerEloChange, int loserEloChange) {
        //make sure table exists, if not create it
        try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
            String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS wynaut_rank_up_match_results (
                        id SERIAL PRIMARY KEY,
                        winner_id UUID NOT NULL,
                        loser_id UUID NOT NULL,
                        winner_elo_change INT NOT NULL,
                        loser_elo_change INT NOT NULL
                    );
                """;
            PreparedStatement createTableStatement = connection.prepareStatement(createTableSQL);
            createTableStatement.executeUpdate();
            String insertMatchSQL = """
                INSERT INTO wynaut_rank_up_match_results (winner_id, loser_id, winner_elo_change, loser_elo_change)
                VALUES (?, ?, ?, ?);
            """;
            PreparedStatement statement = connection.prepareStatement(insertMatchSQL);
            statement.setString(1, winnerId.toString());
            statement.setString(2, loserId.toString());
            statement.setInt(3, winnerEloChange);
            statement.setInt(4, loserEloChange);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
