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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
                                                player_id TEXT,
                                                season_id TEXT,
                                                elo INTEGER DEFAULT 1000,
                                                win_streak INTEGER DEFAULT 0,
                                                PRIMARY KEY (player_id, season_id)
                                            );
                        """;
                PreparedStatement statement = connection.prepareStatement(createTableSQL);
                statement.executeUpdate();
                String createPendingRewardsTable = """
                            CREATE TABLE IF NOT EXISTS pending_rewards (
                                player_id TEXT,
                                season_id TEXT,
                                reward_type TEXT,
                                reward_data TEXT
                            );
                        """;
                PreparedStatement pendingRewardsStatement = connection.prepareStatement(createPendingRewardsTable);
                pendingRewardsStatement.executeUpdate();

                String createBalancesTable = "CREATE TABLE IF NOT EXISTS player_balances (" +
                        "uuid TEXT PRIMARY KEY," +
                        "balance INTEGER NOT NULL DEFAULT 0)";
                PreparedStatement balanceStatement = connection.prepareStatement(createBalancesTable);
                balanceStatement.executeUpdate();
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

    // Add setSeasonProgress method:
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

    // Set elo for a specific season
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

    // Set elo for a specific season
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

    public void saveAllPlayerData() {
        asyncExecutor.submitTask(() -> {
            playerDataStorage.getAllPlayers().forEach(player -> {
                setElo(player.getId(), player.getElo());
            });
        });
    }

    // Only one loop per player, and reward distribution is grouped.
    // Optimized reward evaluation and distribution
    public void evaluateAndDistributeRewards(Season season) {
        asyncExecutor.submitTask(() -> {
            try (Connection connection = WynautRankUp.instance.databaseManager.getConnection()) {
                String query = "SELECT player_id, elo FROM wynaut_rank_up_player_data WHERE season_id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, season.getName());
                ResultSet resultSet = statement.executeQuery();

                String insertSQL = "INSERT INTO pending_rewards (player_id, season_id, reward_type, reward_data) VALUES (?, ?, ?, ?)";
                PreparedStatement insertReward = connection.prepareStatement(insertSQL);

                while (resultSet.next()) {
                    UUID playerId = UUID.fromString(resultSet.getString("player_id"));
                    int elo = resultSet.getInt("elo");

                    if (PermissionUtil.getOptionalServerPlayer(playerId).isEmpty()) {
                        for (RewardCriteria criteria : season.getRewardCriteria()) {
                            if (criteria.isMetBy(playerId, elo)) {
                                for (String item : criteria.getItems()) {
                                    insertReward.setString(1, playerId.toString());
                                    insertReward.setString(2, season.getName());
                                    insertReward.setString(3, "item");
                                    insertReward.setString(4, item);
                                    insertReward.addBatch();
                                }
                                for (String command : criteria.getCommands()) {
                                    insertReward.setString(1, playerId.toString());
                                    insertReward.setString(2, season.getName());
                                    insertReward.setString(3, "command");
                                    insertReward.setString(4, command);
                                    insertReward.addBatch();
                                }
                            }
                        }
                    } else {
                        ServerPlayer player = PermissionUtil.getOptionalServerPlayer(playerId).get();
                        String userName = player.getGameProfile().getName();
                        if (season.getRewardCriteria().stream().allMatch(c -> c.isMetBy(playerId, elo))) {
                            System.out.println("Player " + playerId + " qualifies for rewards in season " + season.getName());
                            season.getRewardCriteria().forEach(criteria -> {
                                criteria.getCommands().forEach(command -> {
                                    System.out.println("Executing command for player " + playerId + ": " + command);
                                    messenger.notifyReward(playerId, userName, command);
                                    try {
                                        messenger.executeCommand(player, command);
                                    } catch (CommandSyntaxException e) {
                                        e.printStackTrace();
                                    }

                                });
                                criteria.getItems().forEach(item -> {
                                    System.out.println("Giving item to player " + playerId + ": " + item);
                                    messenger.notifyReward(playerId, userName, item);
                                    messenger.giveItem(player, item);
                                });
                            });
                        }
                    }
                }
                insertReward.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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

    // Helper class
    public record PendingReward(String type, String data) {
        public void sendToPlayer(ServerPlayer player) {
            if (type.equals("item")) {
                messenger.giveItem(player, data);
            } else if (type.equals("command")) {
                try {
                    messenger.executeCommand(player, data);
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
                             "ON CONFLICT(uuid) DO UPDATE SET balance = ?")) {
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
