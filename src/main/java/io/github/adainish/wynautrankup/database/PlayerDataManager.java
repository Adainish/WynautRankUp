package io.github.adainish.wynautrankup.database;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.data.PlayerDataStorage;
import io.github.adainish.wynautrankup.season.Messenger;
import io.github.adainish.wynautrankup.season.RewardCriteria;
import io.github.adainish.wynautrankup.season.Season;
import io.github.adainish.wynautrankup.season.SeasonProgress;
import io.github.adainish.wynautrankup.util.AsyncExecutor;
import io.github.adainish.wynautrankup.util.PermissionUtil;
import net.minecraft.server.level.ServerPlayer;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerDataManager {
    private static final int DEFAULT_ELO = 1000;
    private final PlayerDataStorage playerDataStorage;
    private final AsyncExecutor asyncExecutor;

    public static Messenger messenger = new Messenger();

    public PlayerDataManager(AsyncExecutor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
        this.playerDataStorage = new PlayerDataStorage();
        asyncExecutor.submitTask(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String createTableSQL = """
                            CREATE TABLE IF NOT EXISTS wynaut_rank_up_player_data (
                                player_id VARCHAR(36),
                                season_id VARCHAR(36),
                                elo INTEGER DEFAULT 1000,
                                win_streak INTEGER DEFAULT 0,
                                PRIMARY KEY (player_id, season_id)
                            );
                        """;
                connection.prepareStatement(createTableSQL).executeUpdate();

                String createResultsTableSQL = """
                    CREATE TABLE IF NOT EXISTS wynaut_rank_up_match_results (
                        id SERIAL PRIMARY KEY,
                        winner_id UUID NOT NULL,
                        loser_id UUID NOT NULL,
                        winner_elo_change INT NOT NULL,
                        loser_elo_change INT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                """;
                connection.prepareStatement(createResultsTableSQL).executeUpdate();

                // Pending rewards with idempotency (unique composite key)
                String createPendingRewardsTable = """
                            CREATE TABLE IF NOT EXISTS pending_rewards (
                                player_id VARCHAR(36) NOT NULL,
                                season_id VARCHAR(64) NOT NULL,
                                reward_type VARCHAR(32) NOT NULL,
                                reward_data TEXT NOT NULL,
                                UNIQUE KEY uq_pending (player_id, season_id, reward_type, reward_data)
                            );
                        """;
                connection.prepareStatement(createPendingRewardsTable).executeUpdate();
                // Best-effort backfill for older installs without the constraint
                try {
                    connection.prepareStatement(
                            "ALTER TABLE pending_rewards ADD UNIQUE KEY uq_pending (player_id, season_id, reward_type, reward_data)"
                    ).executeUpdate();
                } catch (SQLException ignored) { }

                // Season awards log to guard re-awards per season
                String createSeasonAwardsLog = """
                            CREATE TABLE IF NOT EXISTS season_awards_log (
                                season_id VARCHAR(64) PRIMARY KEY,
                                awarded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                            );
                        """;
                connection.prepareStatement(createSeasonAwardsLog).executeUpdate();

                String createBalancesTable = "CREATE TABLE IF NOT EXISTS player_balances (" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "balance INTEGER NOT NULL DEFAULT 0)";
                connection.prepareStatement(createBalancesTable).executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<SeasonProgress> getSeasonProgress(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String query = "SELECT current_season, win_streak FROM wynaut_rank_up_player_data WHERE player_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, playerId.toString());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String season = resultSet.getString("current_season");
                    int winStreak = resultSet.getInt("win_streak");
                    return new SeasonProgress(season, winStreak);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SeasonProgress("", 0);
        }, asyncExecutor.getExecutorService());
    }

    public void setSeasonProgress(UUID playerId, SeasonProgress progress) {
        asyncExecutor.submitTask(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String sql = """
                            INSERT INTO wynaut_rank_up_player_data (player_id, current_season, win_streak)
                            VALUES (?, ?, ?)
                            ON DUPLICATE KEY UPDATE current_season = VALUES(current_season), win_streak = VALUES(win_streak);
                        """;
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, playerId.toString());
                statement.setString(2, progress.getSeason());
                statement.setInt(3, progress.getWinStreak());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Integer> getElo(UUID playerId) {
        String seasonId = WynautRankUp.instance.seasonManager.getCurrentSeasonId();

        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String query = "SELECT elo FROM wynaut_rank_up_player_data WHERE player_id = ? AND season_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, playerId.toString());
                statement.setString(2, seasonId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("elo");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return DEFAULT_ELO;
        }, asyncExecutor.getExecutorService());
    }

    public void setElo(UUID playerId, int elo) {
        String seasonId = WynautRankUp.instance.seasonManager.getCurrentSeasonId();

        asyncExecutor.submitTask(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String insertOrUpdateSQL = """
                            INSERT INTO wynaut_rank_up_player_data (player_id, season_id, elo)
                            VALUES (?, ?, ?)
                            ON DUPLICATE KEY UPDATE elo = VALUES(elo);
                        """;
                PreparedStatement statement = connection.prepareStatement(insertOrUpdateSQL);
                statement.setString(1, playerId.toString());
                statement.setString(2, seasonId);
                statement.setInt(3, elo);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Integer> getElo(UUID playerId, String seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String query = "SELECT elo FROM wynaut_rank_up_player_data WHERE player_id = ? AND season_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, playerId.toString());
                statement.setString(2, seasonId);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("elo");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return DEFAULT_ELO;
        }, asyncExecutor.getExecutorService());
    }

    public void setElo(UUID playerId, String seasonId, int elo) {
        asyncExecutor.submitTask(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String insertOrUpdateSQL = """
                            INSERT INTO wynaut_rank_up_player_data (player_id, season_id, elo)
                            VALUES (?, ?, ?)
                            ON DUPLICATE KEY UPDATE elo = VALUES(elo);
                        """;
                PreparedStatement statement = connection.prepareStatement(insertOrUpdateSQL);
                statement.setString(1, playerId.toString());
                statement.setString(2, seasonId);
                statement.setInt(3, elo);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<List<PlayerEloEntry>> getLeaderboardForSeason(String seasonId, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<PlayerEloEntry> leaderboard = new ArrayList<>();
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String query = "SELECT player_id, elo FROM wynaut_rank_up_player_data WHERE season_id = ? ORDER BY elo DESC LIMIT ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, seasonId);
                statement.setInt(2, limit);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    leaderboard.add(new PlayerEloEntry(rs.getString("player_id"), rs.getInt("elo")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return leaderboard;
        }, asyncExecutor.getExecutorService());
    }

    public CompletableFuture<Integer> getPlayerRankInSeason(UUID playerId, String seasonId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String query = "SELECT player_id, elo, RANK() OVER (ORDER BY elo DESC) as rank FROM wynaut_rank_up_player_data WHERE season_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, seasonId);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    if (rs.getString("player_id").equals(playerId.toString())) {
                        return rs.getInt("rank");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        }, asyncExecutor.getExecutorService());
    }

    public CompletableFuture<Integer> getCurrentWinStreak(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String query = "SELECT win_streak FROM wynaut_rank_up_player_data WHERE player_id = ? AND season_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, uuid.toString());
                statement.setString(2, WynautRankUp.instance.seasonManager.getCurrentSeasonId());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("win_streak");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }, asyncExecutor.getExecutorService());
    }

    public record PlayerEloEntry(String playerId, int elo) { }

    public void saveAllPlayerData() { }

    // Unified evaluation: enqueue to pending_rewards for ALL players; idempotent and transactional.
    public CompletableFuture<Void> evaluateAndDistributeRewards(Season season) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                connection.setAutoCommit(false);

                // Idempotency guard per season
                String upsertSeasonAward = """
                    INSERT INTO season_awards_log (season_id) VALUES (?)
                    ON DUPLICATE KEY UPDATE season_id = season_id
                """;
                PreparedStatement seasonGuard = connection.prepareStatement(upsertSeasonAward);
                seasonGuard.setString(1, season.getName());
                int affected = seasonGuard.executeUpdate();
                // MySQL semantics: 1 = inserted (proceed), 2 = duplicate (already awarded)
                if (affected > 1) {
                    connection.rollback();
                    return null;
                }

                String queryPlayers = "SELECT player_id, elo FROM wynaut_rank_up_player_data WHERE season_id = ?";
                PreparedStatement ps = connection.prepareStatement(queryPlayers);
                ps.setString(1, season.getName());
                ResultSet rs = ps.executeQuery();

                String insertReward = """
                    INSERT INTO pending_rewards (player_id, season_id, reward_type, reward_data)
                    VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE reward_data = VALUES(reward_data)
                """;
                PreparedStatement insert = connection.prepareStatement(insertReward);

                while (rs.next()) {
                    UUID playerId = UUID.fromString(rs.getString("player_id"));
                    int elo = rs.getInt("elo");

                    for (RewardCriteria criteria : season.getRewardCriteria()) {
                        if (criteria.isMetBy(playerId, elo)) {
                            for (String item : criteria.getItems()) {
                                insert.setString(1, playerId.toString());
                                insert.setString(2, season.getName());
                                insert.setString(3, "item");
                                insert.setString(4, item);
                                insert.addBatch();
                            }
                            for (String command : criteria.getCommands()) {
                                insert.setString(1, playerId.toString());
                                insert.setString(2, season.getName());
                                insert.setString(3, "command");
                                insert.setString(4, command);
                                insert.addBatch();
                            }
                        }
                    }
                }

                insert.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        }, asyncExecutor.getExecutorService());
    }

    // Dispatcher: deliver pending rewards to online players only, then remove them.
    public CompletableFuture<Integer> dispatchPendingRewardsToOnlinePlayers() {
        return CompletableFuture.supplyAsync(() -> {
            int delivered = 0;
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String distinctPlayers = "SELECT DISTINCT player_id FROM pending_rewards";
                PreparedStatement ps = connection.prepareStatement(distinctPlayers);
                ResultSet rs = ps.executeQuery();
                List<UUID> candidates = new ArrayList<>();
                while (rs.next()) {
                    try {
                        candidates.add(UUID.fromString(rs.getString("player_id")));
                    } catch (IllegalArgumentException ignored) { }
                }

                for (UUID pid : candidates) {
                    Optional<ServerPlayer> opt = PermissionUtil.getOptionalServerPlayer(pid);
                    if (opt.isPresent()) {
                        ServerPlayer player = opt.get();
                        String userName = player.getGameProfile().getName();
                        List<PendingReward> rewards = getAndRemovePendingRewards(pid);
                        messenger.notify(player, "The season has ended! You have received " + rewards.size() + " rewards, " + userName + "!");
                        for (PendingReward pr : rewards) {
                            if ("item".equals(pr.type())) {
                                messenger.giveItem(player, pr.data());
                                delivered++;
                            } else if ("command".equals(pr.type())) {
                                try {
                                    messenger.executeCommandWithNoNotify(player, pr.data());
                                } catch (CommandSyntaxException e) {
                                    e.printStackTrace();
                                }
                                delivered++;
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return delivered;
        }, asyncExecutor.getExecutorService());
    }

    public List<PendingReward> getAndRemovePendingRewards(UUID playerId) {
        List<PendingReward> rewards = new ArrayList<>();
        try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
            String selectSQL = "SELECT reward_type, reward_data FROM pending_rewards WHERE player_id = ?";
            PreparedStatement ps = connection.prepareStatement(selectSQL);
            ps.setString(1, playerId.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("reward_type");
                String data = rs.getString("reward_data");
                rewards.add(new PendingReward(type, data));
            }
            String deleteSQL = "DELETE FROM pending_rewards WHERE player_id = ?";
            PreparedStatement del = connection.prepareStatement(deleteSQL);
            del.setString(1, playerId.toString());
            del.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rewards;
    }

    public record PendingReward(String type, String data) {
        public void sendToPlayer(ServerPlayer player) {
            if (type.equals("item")) {
                messenger.giveItem(player, data);
            } else if (type.equals("command")) {
                try {
                    messenger.executeCommandWithNoNotify(player, data);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getBalance(String uuid) {
        try (Connection conn = WynautRankUp.instance.databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT balance FROM player_balances WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setBalance(String uuid, int amount) {
        try (Connection conn = WynautRankUp.instance.databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO player_balances (uuid, balance) VALUES (?, ?) " +
                             "ON DUPLICATE KEY UPDATE balance = ?")) {
            ps.setString(1, uuid);
            ps.setInt(2, amount);
            ps.setInt(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void adjustBalance(String uuid, int delta) {
        int current = getBalance(uuid);
        setBalance(uuid, current + delta);
    }
}
