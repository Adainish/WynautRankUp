// java
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
