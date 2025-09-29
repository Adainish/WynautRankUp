package io.github.adainish.wynautrankup.cmd;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.arenas.Arena;
import io.github.adainish.wynautrankup.util.PermissionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class ArenaCommand {
    public static final String PERMISSION_NODE = "wynautrankup.admin";


    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("wynautarena")
                .requires(source -> {
                    if (source.isPlayer()) {
                        try {
                            return PermissionUtil.hasPermission(source.getPlayerOrException().getUUID(), PERMISSION_NODE);
                        } catch (CommandSyntaxException e) {
                            source.sendSystemMessage(Component.literal("Ruh roh raggy. You shouldn't try that.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                        }
                    } else {
                        return true;
                    }
                    return true;
                })
                .then(Commands.literal("list")
                        .executes(ctx -> {
                            Map<String, Arena> arenas = WynautRankUp.instance.arenaManager.getArenas();
                            if (arenas.isEmpty()) {
                                ctx.getSource().sendSuccess(() -> Component.literal("§cNo arenas found."), false);
                            } else {
                                ctx.getSource().sendSuccess(() -> Component.literal("§eArenas: " + String.join(", ", arenas.keySet())), false);
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("info")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    Arena arena = WynautRankUp.instance.arenaManager.getArena(name);
                                    if (arena == null) {
                                        ctx.getSource().sendSuccess(() -> Component.literal("§cArena not found: " + name), false);
                                    } else {
                                        ctx.getSource().sendSuccess(() -> Component.literal(
                                                "§eArena: §a" + arena.getName() +
                                                        "\n§eWorld: §a" + arena.getWorld() +
                                                        "\n§ePlayer Positions: §a" + arena.getPlayerPositions().size() +
                                                        "\n§eIn Use: §a" + arena.isInUse()
                                        ), false);
                                    }
                                    return 1;
                                })
                        )
                )
                //add a teleport command to tp to an arena
                .then(Commands.literal("tp")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    if (!ctx.getSource().isPlayer()) {
                                        ctx.getSource().sendSystemMessage(Component.literal("§cThis command can only be run by a player."));
                                        return 1;
                                    }
                                            String name = StringArgumentType.getString(ctx, "name");
                                            Arena arena = WynautRankUp.instance.arenaManager.getArena(name);
                                            if (arena == null) {
                                                ctx.getSource().sendSuccess(() -> Component.literal("§cArena not found: " + name), false);
                                            } else {
                                                if (arena.getPlayerPositions().isEmpty()) {
                                                    ctx.getSource().sendSuccess(() -> Component.literal("§cArena has no player positions set: " + name), false);
                                                } else {
                                                    //teleport player to first position
                                                    arena.getPlayerPositions().getFirst().teleport(ctx.getSource().getPlayerOrException());
                                                    ctx.getSource().sendSuccess(() -> Component.literal("§aTeleported to arena: " + name), false);
                                                }
                                            }
                                    return 1;
                                }
                                ))
                )
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            WynautRankUp.instance.arenaManager.reload();
                            ctx.getSource().sendSuccess(() -> Component.literal("§aArenas reloaded!"), false);
                            return 1;
                        })
                );
    }
}
