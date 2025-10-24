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
