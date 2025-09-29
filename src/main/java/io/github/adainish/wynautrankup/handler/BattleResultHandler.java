package io.github.adainish.wynautrankup.handler;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.data.Match;
import kotlin.Unit;

import java.util.UUID;

public class BattleResultHandler
{

    public BattleResultHandler() {

    }

    public void registerEventListeners() {
        System.out.println("Registering Battle Event Listeners");
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL, battleVictoryEvent -> {
            PokemonBattle battle = battleVictoryEvent.getBattle();
            UUID battleUUID = battle.getBattleId();
            var rankedBattleTracker = WynautRankUp.instance.matchmakingQueue.getRankedBattleTracker();
            if (!rankedBattleTracker.isRanked(battleUUID)) {
                return Unit.INSTANCE; // Ignore non-ranked battles
            }

            UUID winnerId = null;
            UUID loserId = null;
            for (BattleActor battleActor : battleVictoryEvent.getBattle().getActors()) {
                if (battleVictoryEvent.getWinners().contains(battleActor))
                    winnerId = battleActor.getUuid();
                else
                    loserId = battleActor.getUuid();
            }
            if (winnerId == null || loserId == null) {
                return Unit.INSTANCE; // Handle error
            }
            Match match = rankedBattleTracker.getRankedBattle(battleUUID);
            WynautRankUp.instance.matchmakingQueue.getRankedBattleTracker().rankedBattleEnded(match, loserId, winnerId);
            return Unit.INSTANCE;
        });
    }
}
