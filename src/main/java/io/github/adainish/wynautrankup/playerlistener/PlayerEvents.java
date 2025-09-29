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
