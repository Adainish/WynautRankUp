package io.github.adainish.wynautrankup.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.wynautrankup.WynautRankUp;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.platform.PlayerAdapter;
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
