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
            player.sendSystemMessage(
                Component.literal("[WynautRankUp] Congratulations " + playerName + "! You have earned a new reward: " + rewardDetails)
                    .withStyle(ChatFormatting.GOLD)
            );
        });
    }

    public void giveItem(ServerPlayer player, ItemStack stack) {
        stack = stack.copy();
        notifyReward(player.getUUID(), player.getDisplayName().getString(), stack.getHoverName().getString());
        //give item to player
        if (player.getInventory().add(stack))
        {
            notify(player, "You have received: " + stack.getDisplayName().getString());
        } else {
            notify(player, "Your inventory is full, dropping item: " + stack.getDisplayName().getString());
            player.drop(stack, false);
        }
    }

    public void giveItem(ServerPlayer player, String data) {
        notifyReward(player.getUUID(), player.getDisplayName().getString(), data);
        //convert data to item and give to player
        //get item from builtin minecraft item registry

        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(data));
        ItemStack stack = new ItemStack(item, 1);
        if (player.getInventory().add(stack))
        {
            notify(player, "You have received: " + stack.getHoverName().getString());
        } else {
            notify(player, "Your inventory is full, dropping item: " + stack.getHoverName().getString());
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
        notifyReward(player.getUUID(), player.getDisplayName().getString(), data);
        //execute command from server console, replacing {player} with the player's name
        String command = data.replace("{player}", player.getDisplayName().getString());
        WynautRankUp.instance.server.getCommands().getDispatcher().execute(command, WynautRankUp.instance.server.createCommandSourceStack());
    }
}
