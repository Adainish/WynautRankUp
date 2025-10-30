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
package io.github.adainish.wynautrankup.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.wynautrankup.WynautRankUp;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for permission handling and command execution
 */
public class PermissionUtil
{

    /**
     * Get the server player object from the player's UUID
     *
     * @return the optional player object
     */
    public static Optional<ServerPlayer> getOptionalServerPlayer(UUID uuid) {
        return Optional.ofNullable(WynautRankUp.instance.server.getPlayerList().getPlayer(uuid));
    }


    /**
     * Checks whether a player has a specific permission with luckperms, if luckperms is not loaded, return false
     * @param permission the permission to check
     * @return true if the player has the permission, false otherwise
     */
    public static boolean hasPermission(UUID uuid, String permission) {
        boolean hasPermission = false;
        try {
            Optional<ServerPlayer> optionalServerPlayer = getOptionalServerPlayer(uuid);
            if (optionalServerPlayer.isPresent()) {
                LuckPerms api = LuckPermsProvider.get();
                var lpPlayer = optionalServerPlayer.get();
                PlayerAdapter<ServerPlayer> adapter = api.getPlayerAdapter(ServerPlayer.class);
                User user = adapter.getUser(lpPlayer);
                hasPermission = user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
            }
        } catch (NoClassDefFoundError ignored)
        {
        }
        return hasPermission;
    }

    public static boolean hasCachedLuckPerms(CommandSourceStack source, String node) {
        if (!source.isPlayer()) return true; // allow console
        try {
            var player = source.getPlayerOrException();
            // fast cached LP check
            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().getUser(player.getUUID()); // cached user if loaded
            if (user == null) {
                // user not cached — fallback to vanilla op check (or return false)
                return source.hasPermission(2);
            }
            return user.getCachedData().getPermissionData().checkPermission(node).asBoolean();
        } catch (CommandSyntaxException e) {
            return false;
        }
    }

    /**
     * Execute a command as the server
     * @param s the command to execute
     * @throws CommandSyntaxException if the command syntax is incorrect
     */
    public static void executeCommandAsServer(String name, String s) throws CommandSyntaxException
    {
        MinecraftServer server = WynautRankUp.instance.server;
        String parsedCommand = s.replace("%player%", name);
        WynautRankUp.instance.server.getCommands().getDispatcher().execute(parsedCommand, server.createCommandSourceStack());
    }

    /**
     * Execute a command as the player
     * @param s the command to execute
     * @throws CommandSyntaxException if the command syntax is incorrect
     */
    public static void executeCommandAsPlayer(UUID uuid, String name, String s) throws CommandSyntaxException
    {
        Optional<ServerPlayer> optionalServerPlayer = getOptionalServerPlayer(uuid);
        if (optionalServerPlayer.isPresent()) {
            String parsedCommand = s.replace("%player%", name);
            WynautRankUp.instance.server.getCommands().getDispatcher().execute(parsedCommand, optionalServerPlayer.get().createCommandSourceStack());
        }
    }

    /**
     * Execute a command as the player
     * @param s the command to execute
     * @param executor the executor of the command
     * @throws CommandSyntaxException if the command syntax is incorrect
     */
    public static void executeCommand(UUID uuid, String name, String s, String executor) throws CommandSyntaxException {
        if (executor.equalsIgnoreCase("server")) executeCommandAsServer(name, s);
        else executeCommandAsPlayer(uuid, name, s);
    }
}
