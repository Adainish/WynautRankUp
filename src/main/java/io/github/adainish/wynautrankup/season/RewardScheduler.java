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
