package io.github.adainish.wynautrankup.season;

import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.database.PlayerDataManager;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class RewardScheduler
{

    public RewardScheduler() {

    }

    public void start() {
        SeasonManager seasonManager = WynautRankUp.instance.seasonManager;
        PlayerDataManager playerManager = WynautRankUp.instance.playerDataManager;
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalDate today = LocalDate.now();
                seasonManager.getSeasons().forEach(season -> {
                    if (season.isRewardDay(today)) {
                        playerManager.evaluateAndDistributeRewards(season);
                    }
                });
            }
        }, 0, 24 * 60 * 60 * 1000); // daily
    }
}
