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
package io.github.adainish.wynautrankup.playerlistener;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import io.github.adainish.wynautrankup.WynautRankUp;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;



public class PlayerEvents
{

    public PlayerEvents()
    {
        PlatformEvents.SERVER_PLAYER_LOGIN.subscribe(Priority.NORMAL, event -> {
            ServerPlayer player = event.getPlayer();
            WynautRankUp.instance.playerDataManager.getAndRemovePendingRewards(player.getUUID()).forEach(pendingReward -> {
                pendingReward.sendToPlayer(player);
            });
            return Unit.INSTANCE;
        });
        PlatformEvents.SERVER_PLAYER_LOGOUT.subscribe(Priority.NORMAL, event -> {
            ServerPlayer player = event.getPlayer();
            //check if player is in queue, if so remove them
            if (WynautRankUp.instance.matchmakingQueue.isInQueue(player.getUUID())) {
                WynautRankUp.instance.matchmakingQueue.removeFromQueue(player.getUUID());
            }
            //check if player is in a ranked battle, if so make it count as a loss for that player
            if (WynautRankUp.instance.matchmakingQueue.getRankedBattleTracker().isPlayerInRankedBattle(player.getUUID())) {
                WynautRankUp.instance.matchmakingQueue.getRankedBattleTracker().forfeitPlayerFromBattle(player.getUUID());
            }
            event.getPlayer().setInvulnerable(false);
            //save player data
            return Unit.INSTANCE;
        });
    }
}
