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
package io.github.adainish.wynautrankup.placeholders;

import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.miniplaceholders.api.Expansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class WynautPlaceholders {
    public static void register() {
        final Expansion expansion = Expansion.builder("wynautrankup")
                .audiencePlaceholder("rank", (audience, ctx, queue) -> {
                    ServerPlayer player = (ServerPlayer) audience;
                    int elo = safeGetElo(player.getUUID());
                    String rankName = WynautRankUp.instance.rankManager.getRankStringForElo(elo);
                    return Tag.selfClosingInserting(Component.text(rankName));
                })
                .audiencePlaceholder( "elo", (audience, ctx, queue) -> {
                    ServerPlayer player = (ServerPlayer) audience;
                    int elo = safeGetElo(player.getUUID());
                    return Tag.selfClosingInserting(Component.text(Integer.toString(elo)));
                })
                .audiencePlaceholder("leaderboard_position", (audience, ctx, queue) -> {
                    ServerPlayer player = (ServerPlayer) audience;
                    String seasonId = WynautRankUp.instance.seasonManager.getCurrentSeasonId();
                    int pos = -1;
                    try {
                        pos = WynautRankUp.instance.playerDataManager
                                .getPlayerRankInSeason(player.getUUID(), seasonId)
                                .get(750, TimeUnit.MILLISECONDS);
                    } catch (Exception ignored) { }
                    String display = (pos > 0) ? Integer.toString(pos) : "N/A";
                    return Tag.selfClosingInserting(Component.text(display));
                })
                .build();

        expansion.register();
    }

    private static int safeGetElo(UUID uuid) {
        try {
            return WynautRankUp.instance.playerDataManager
                    .getElo(uuid)
                    .get(750, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return 0;
        }
    }
}
