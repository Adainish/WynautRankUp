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

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.util.PermissionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class Messenger
{
    public void notify(ServerPlayer player, String message)
    {
            player.sendSystemMessage(
                    Component.literal("[WynautRankUp] " + message.replace("{player}", player.getDisplayName().plainCopy().getString()))
                            .withStyle(ChatFormatting.GOLD)
            );
    }
    public void notifyReward(UUID uuid, String playerName, String rewardDetails) {
        // Send message to player
        PermissionUtil.getOptionalServerPlayer(uuid).ifPresent(player -> {
            if (rewardDetails.isEmpty()) {
                player.sendSystemMessage(
                    Component.literal("[WynautRankUp] Congratulations " + playerName + "! You have earned a new reward!")
                        .withStyle(ChatFormatting.GOLD)
                );
                return;
            }
            player.sendSystemMessage(
                Component.literal("[WynautRankUp] Congratulations " + playerName + "! You have earned a new reward: " + rewardDetails)
                    .withStyle(ChatFormatting.GOLD)
            );
        });
    }

    public void giveItem(ServerPlayer player, ItemStack stack) {
        stack = stack.copy();
        //give item to player
        if (player.getInventory().add(stack))
        {
            notify(player, "Your item has been added to your inventory");
        } else {
            notify(player, "Your inventory is full, dropping item: " + stack.getDisplayName().getString());
            player.drop(stack, false);
        }
    }

    public void giveItem(ServerPlayer player, String data) {
        //convert data to item and give to player
        //get item from builtin minecraft item registry

        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(data));
        ItemStack stack = new ItemStack(item, 1);
        if (player.getInventory().add(stack))
        {
            notify(player, "Your item has been added to your inventory");
        } else {
            notify(player, "Your inventory is full, dropping item: " + stack.getDisplayName().getString());
            player.drop(stack, false);
        }
    }

    public void executeCommandWithNoNotify(ServerPlayer player, String data) throws CommandSyntaxException {
        //execute command from server console, replacing {player} with the player's name
        String command = data.replace("{player}", player.getDisplayName().getString());
        WynautRankUp.instance.server.getCommands().getDispatcher().execute(command, WynautRankUp.instance.server.createCommandSourceStack());
    }

    public void executeCommandWithNotify(ServerPlayer player, String command, String message) throws CommandSyntaxException {
        notify(player, message);
        //execute command from server console, replacing {player} with the player's name
        command = command.replace("{player}", player.getDisplayName().getString());
        WynautRankUp.instance.server.getCommands().getDispatcher().execute(command, WynautRankUp.instance.server.createCommandSourceStack());
    }

    public void executeCommand(ServerPlayer player, String data) throws CommandSyntaxException {
        notifyReward(player.getUUID(), player.getDisplayName().getString(), "");
        //execute command from server console, replacing {player} with the player's name
        String command = data.replace("{player}", player.getDisplayName().getString());
        WynautRankUp.instance.server.getCommands().getDispatcher().execute(command, WynautRankUp.instance.server.createCommandSourceStack());
    }
}
