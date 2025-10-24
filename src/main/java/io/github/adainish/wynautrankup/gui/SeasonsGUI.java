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
package io.github.adainish.wynautrankup.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.season.Season;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SeasonsGUI {
    public void open(ServerPlayer player) {
        List<Season> seasons = WynautRankUp.instance.seasonManager.getSeasons();
        ChestTemplate.Builder builder = ChestTemplate.builder(3);

        // Decorative border (glass panes)
        ItemStack border = new ItemStack(Items.LIGHT_BLUE_STAINED_GLASS_PANE);
        border.set(DataComponents.CUSTOM_NAME, Component.literal(" "));
        GooeyButton borderButton = GooeyButton.builder().display(border).build();
        for (int slot = 0; slot < 27; slot++) {
            if (slot < 9 || slot > 17 || slot % 9 == 0 || slot % 9 == 8) {
                builder.set(slot, borderButton);
            }
        }

        // Info header
        ItemStack info = new ItemStack(Items.PAPER);
        info.set(DataComponents.CUSTOM_NAME, Component.literal("§b§lSeasons Overview"));
        info.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal("§7Browse active and past seasons."),
                Component.literal("§7Click a season for more details.")
        )));
        builder.set(4, GooeyButton.builder().display(info).build());

        // Center season buttons in row 2 (slots 10-16)
        int startSlot = 10;
        CompletableFuture<Integer> retrievablePlayerElo = WynautRankUp.instance.playerDataManager.getElo(player.getUUID());
        int playerElo = retrievablePlayerElo.join();
        CompletableFuture<Integer> retrievablePlayerWinStreak = WynautRankUp.instance.playerDataManager.getCurrentWinStreak(player.getUUID());
        int playerWinStreak = retrievablePlayerWinStreak.join();

        int i = 0;
        for (Season season : seasons) {
            ItemStack icon = new ItemStack(Items.CLOCK);
            icon.set(DataComponents.CUSTOM_NAME, Component.literal("§6§l" + season.getName()));
            icon.set(DataComponents.LORE, new ItemLore(List.of(
                    Component.literal("§7" + season.getDescription()),
                    Component.literal("§8----------------------"),
                    Component.literal("§f§lEnds: §c" + season.getRewardDate()),
                    Component.literal("§f§lStatus: " + (season.isActive() ? "§aActive" : "§cInactive")),
                    Component.literal("§e§lClick to view details")
            )));
            GooeyButton button = GooeyButton.builder()
                    .display(icon)
                    .onClick(a -> new SeasonInfoGUI().open(player, season, playerElo, playerWinStreak))
                    .build();
            builder.set(startSlot + i, button);
            i++;
            if (i >= 7) break; // Prevent overflow
        }

        LinkedPage page = LinkedPage.builder()
                .title(Component.literal("§b§lSeasons"))
                .template(builder.build())
                .build();
        UIManager.openUIForcefully(player, page);
    }
}
