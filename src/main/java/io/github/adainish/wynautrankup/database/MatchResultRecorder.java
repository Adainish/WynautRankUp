/*
 * Program: WynautRankup - Add a competitive ranked system to Cobblemon
 * Copyright (C) <2025> <Nicole "Adenydd" Catherine Stuut>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * See the `LICENSE` file in the project root or <https://www.gnu.org/licenses/>.
 */
package io.github.adainish.wynautrankup.database;

import io.github.adainish.wynautrankup.WynautRankUp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MatchResultRecorder {

    public void recordMatch(UUID winnerId, UUID loserId, int winnerEloChange, int loserEloChange) {
        String insertMatchSQL = """
                    INSERT INTO wynaut_rank_up_match_results (winner_id, loser_id, winner_elo_change, loser_elo_change)
                    VALUES (?, ?, ?, ?);
                """;
        try (Connection connection = WynautRankUp.instance.databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertMatchSQL)) {
            statement.setString(1, winnerId.toString());
            statement.setString(2, loserId.toString());
            statement.setInt(3, winnerEloChange);
            statement.setInt(4, loserEloChange);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int countRecentMatches(UUID player1, UUID player2, int days) {
        String sql = """
                SELECT COUNT(*) FROM wynaut_rank_up_match_results
                WHERE ((winner_id = ? AND loser_id = ?) OR (winner_id = ? AND loser_id = ?))
                AND created_at >= NOW() - INTERVAL ? DAY
            """;
        try (Connection connection = WynautRankUp.instance.databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player1.toString());
            statement.setString(2, player2.toString());
            statement.setString(3, player2.toString());
            statement.setString(4, player1.toString());
            statement.setInt(5, days);
            var rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
