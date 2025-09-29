package io.github.adainish.wynautrankup.cmd;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.season.RewardCriteria;
import io.github.adainish.wynautrankup.season.Season;
import io.github.adainish.wynautrankup.util.PermissionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.time.LocalDate;

public class SeasonCommand {
    public static final String PERMISSION_NODE = "wynautrankup.user.season";
    public static final String ADMIN_PERMISSION_NODE = "wynautrankup.admin";

    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("season")
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
                .executes(ctx -> {
                    String id = WynautRankUp.instance.seasonManager.getCurrentSeasonId();
                    Season currentSeason = WynautRankUp.instance.seasonManager.getSeasonById(id);
                    if (currentSeason != null) {
                        ctx.getSource().sendMessage(Component.literal("Current season: " + currentSeason.getDisplayName()));
                        ctx.getSource().sendMessage(Component.literal("Time until season ends: " + WynautRankUp.instance.seasonManager.getTimeUntilSeasonEnds()));
                    } else {
                        ctx.getSource().sendMessage(Component.literal("No current season set."));
                    }
                    return 1;
                })
                .then(Commands.literal("list")
                        .requires(source -> {
                            if (source.isPlayer()) {
                                try {
                                    return PermissionUtil.hasPermission(source.getPlayerOrException().getUUID(), ADMIN_PERMISSION_NODE);
                                } catch (CommandSyntaxException e) {
                                    source.sendSystemMessage(Component.literal("Ruh roh raggy. You shouldn't try that.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                                }
                            } else {
                                return true;
                            }
                            return true;
                        })
                        .executes(ctx -> {
                            for (Season season : WynautRankUp.instance.seasonManager.getSeasons()) {
                                ctx.getSource().sendMessage(Component.literal(season.getName() + " - " + season.getDisplayName()));
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("reload")
                        .requires(source -> {
                            if (source.isPlayer()) {
                                try {
                                    return PermissionUtil.hasPermission(source.getPlayerOrException().getUUID(), ADMIN_PERMISSION_NODE);
                                } catch (CommandSyntaxException e) {
                                    source.sendSystemMessage(Component.literal("Ruh roh raggy. You shouldn't try that.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                                }
                            } else {
                                return true;
                            }
                            return true;
                        })
                        .executes(ctx -> {
                            WynautRankUp.instance.seasonManager.reloadSeasons();
                            ctx.getSource().sendMessage(Component.literal("Seasons reloaded."));
                            return 1;
                        })
                )
                .then(Commands.literal("set")
                        .requires(source -> {
                            if (source.isPlayer()) {
                                try {
                                    return PermissionUtil.hasPermission(source.getPlayerOrException().getUUID(), ADMIN_PERMISSION_NODE);
                                } catch (CommandSyntaxException e) {
                                    source.sendSystemMessage(Component.literal("Ruh roh raggy. You shouldn't try that.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                                }
                            } else {
                                return true;
                            }
                            return true;
                        })
                        .then(Commands.argument("seasonName", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "seasonName");
                                    for (Season season : WynautRankUp.instance.seasonManager.getSeasons()) {
                                        if (season.getName().equalsIgnoreCase(name)) {
                                            WynautRankUp.instance.seasonManager.setCurrentSeason(season);
                                            ctx.getSource().sendMessage(Component.literal("Current season set to: " + season.getDisplayName()));
                                            return 1;
                                        }
                                    }
                                    ctx.getSource().sendMessage(Component.literal("Season not found: " + name));
                                    return 0;
                                })
                        )
                )
                .then(Commands.literal("create")
                        .requires(source -> {
                            if (source.isPlayer()) {
                                try {
                                    return PermissionUtil.hasPermission(source.getPlayerOrException().getUUID(), ADMIN_PERMISSION_NODE);
                                } catch (CommandSyntaxException e) {
                                    source.sendSystemMessage(Component.literal("Ruh roh raggy. You shouldn't try that.").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
                                }
                            } else {
                                return true;
                            }
                            return true;
                        })
                        .then(Commands.argument("seasonName", StringArgumentType.word())
                                .then(Commands.argument("displayName", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "seasonName");
                                            String displayName = StringArgumentType.getString(ctx, "displayName");
                                            if (WynautRankUp.instance.seasonManager.getSeasonById(name) != null) {
                                                ctx.getSource().sendMessage(Component.literal("Season already exists: " + name));
                                                return 0;
                                            }
                                            Season newSeason = new Season();
                                            newSeason.setName(name);
                                            newSeason.setDisplayName(displayName);
                                            newSeason.setDescription("A new season.");
                                            //current date + 4 months for configurability later
                                            LocalDate rewardDate = LocalDate.now().plusMonths(4);
                                            String rewardDateString = String.format("%02d/%02d/%04d", rewardDate.getDayOfMonth(), rewardDate.getMonthValue(), rewardDate.getYear());
                                            newSeason.setRewardDate(rewardDateString);

                                            // example reward criteria
                                            RewardCriteria rewardCriteria = new RewardCriteria();
                                            rewardCriteria.setType("item");
                                            rewardCriteria.getCommands().add("give {player} minecraft:diamond 1");
                                            rewardCriteria.getItems().add("minecraft:emerald");
                                            rewardCriteria.setEndDate(rewardDate);
                                            rewardCriteria.setPokemonType("");
                                            rewardCriteria.setMinElo(1000);
                                            rewardCriteria.setMaxElo(3000);
                                            rewardCriteria.setStreakCount(0);

                                            newSeason.getRewardCriteria().add(rewardCriteria);

                                            try {
                                                WynautRankUp.instance.seasonManager.addSeason(newSeason);
                                                ctx.getSource().sendMessage(Component.literal("Created new season: " + displayName));
                                            } catch (Exception e) {
                                                ctx.getSource().sendMessage(Component.literal("Failed to create season: " + e.getMessage()));
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                ;
    }
}
