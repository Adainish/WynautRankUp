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
package io.github.adainish.wynautrankup.cmd;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.util.PermissionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class AdminCommand
{
    public static final String PERMISSION_NODE = "wynautrankup.admin";

    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("wynautadmin")
                .requires(source -> {
                    if (source.isPlayer()) {
                        try {
                            return PermissionUtil.hasPermission(source.getPlayerOrException().getUUID(), PERMISSION_NODE);
                        } catch (CommandSyntaxException e) {
                            source.sendSystemMessage(Component.literal("Oh no, an error occurred! Please contact an administrator.").withStyle(ChatFormatting.RED));
                        }
                    } else {
                        return true;
                    }
                    return true;
                })
                .then(Commands.literal("help")
                        .executes(ctx -> {
                            ctx.getSource().sendSuccess(() ->
                                    Component.literal(
                                            "§eWynautRankUp Admin Help:\n" +
                                                    "§a/wradmin reload §7- Reloads the plugin configuration\n" +
                                                    "§a/wradmin help §7- Shows this help message"
                                    ), false
                            );
                            return 1;
                        })
                )
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            WynautRankUp.instance.reloadConfig();
                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("§aWynautRankUp config reloaded!"), false
                            );
                            return 1;
                        })
                )
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(() ->
                            Component.literal(
                                    "§eWynautRankUp Admin Help:\n" +
                                            "§a/wradmin reload §7- Reloads the plugin configuration\n" +
                                            "§a/wradmin help §7- Shows this help message"
                            ), false
                    );
                    return 1;
                });
    }
}


