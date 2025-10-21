package io.github.adainish.wynautrankup.season;

import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.database.PlayerDataManager;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class RewardScheduler
{
    public RewardScheduler() { }

    public void start() {
        SeasonManager seasonManager = WynautRankUp.instance.seasonManager;
        PlayerDataManager playerManager = WynautRankUp.instance.playerDataManager;
        Timer timer = new Timer(true);

        // Daily evaluation: enqueue all rewards (offline/online unified)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalDate today = LocalDate.now();
                seasonManager.getSeasons().forEach(season -> {
                    if (season.isRewardDay(today)) {
                        playerManager.evaluateAndDistributeRewards(season).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        });
                    }
                });
            }
        }, 0, 24L * 60L * 60L * 1000L);

        // Frequent dispatcher: deliver pending rewards to online players
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                playerManager.dispatchPendingRewardsToOnlinePlayers().exceptionally(ex -> {
                    ex.printStackTrace();
                    return 0;
                });
            }
        }, 10_000L, 30_000L); // start after 10s, then every 30s
    }
}
