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
package io.github.adainish.wynautrankup.arenas;

import io.github.adainish.wynautrankup.util.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class Arena
{
    private String name;
    private String world;
    private List<Location> playerPositions;
    private transient boolean inUse = false;

    public Arena(String name, String world, List<Location> playerPositions) {
        this.name = name;
        this.world = world;
        this.playerPositions = playerPositions;
    }

    public String getName() { return name; }
    public String getWorld() { return world; }
    public List<Location> getPlayerPositions() { return playerPositions; }
    public void setPlayerPosition(int index, Location loc) { this.playerPositions.set(index, loc); }

    public boolean isInUse() {
        return inUse;
    }
    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    // Teleport both players and complete only when both are fully loaded at the target.
    public CompletableFuture<Void> teleportPlayersToArenaAsync(ServerPlayer p1, ServerPlayer p2) {
        if (playerPositions.size() < 2) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("Arena has fewer than 2 positions."));
            return failed;
        }

        Location loc1 = playerPositions.get(0);
        Location loc2 = playerPositions.get(1);
        if (loc1 == null || loc2 == null) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("Arena player positions are not configured."));
            return failed;
        }

        // Initiate teleports (queued to server thread inside Location.teleport)
        teleportPlayersToArena(p1, p2);

        MinecraftServer server = p1.getServer();
        if (server == null) {
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("Server not available for teleport."));
            return failed;
        }

        // Wait until both players are settled at their target locations.
        CompletableFuture<Void> p1Ready = waitForPlayerLoaded(p1, loc1, 100, 3); // 100 ticks timeout, 3 stable ticks
        CompletableFuture<Void> p2Ready = waitForPlayerLoaded(p2, loc2, 100, 3);

        return CompletableFuture.allOf(p1Ready, p2Ready);
    }

    public void teleportPlayersToArena(ServerPlayer player1, ServerPlayer player2) {
        if (playerPositions.size() >= 2) {
            Location loc1 = playerPositions.get(0);
            Location loc2 = playerPositions.get(1);
            if (loc1 != null && loc2 != null) {
                loc1.teleport(player1);
                loc2.teleport(player2);
            }
        }
    }

    // Polls each server tick until the player is in the correct level, the target chunk is loaded,
    // the player is close to the target pos, and this is stable for "stableTicks" consecutive ticks.
    private CompletableFuture<Void> waitForPlayerLoaded(ServerPlayer player, Location target, int timeoutTicks, int stableTicks) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        MinecraftServer server = player.getServer();
        ServerLevel targetLevel = target.getServerWorld();

        if (server == null) {
            future.completeExceptionally(new IllegalStateException("Server not available."));
            return future;
        }
        if (targetLevel == null) {
            future.completeExceptionally(new IllegalStateException("Target world not loaded."));
            return future;
        }

        final BlockPos targetBlockPos = BlockPos.containing(target.getX(), target.getY(), target.getZ());
        final Vec3 targetVec = new Vec3(target.getX(), target.getY(), target.getZ());

        final int[] remaining = { timeoutTicks };
        final int[] stable = { 0 };

        Runnable poll = new Runnable() {
            @Override
            public void run() {
                if (future.isDone()) return;

                boolean dimOk = player.level() == targetLevel;
                boolean chunkOk = targetLevel.hasChunkAt(targetBlockPos);
                boolean posOk = player.position().distanceToSqr(targetVec) <= 10.0D;
                boolean aliveOk = player.isAlive();

                boolean ready = dimOk && chunkOk && posOk && aliveOk;

                if (ready) {
                    stable[0]++;
                } else {
                    stable[0] = 0;
                }

                if (stable[0] >= stableTicks) {
                    future.complete(null);
                    return;
                }

                if (remaining[0]-- <= 0) {
                    future.completeExceptionally(new TimeoutException("Player did not settle at target location in time."));
                    return;
                }

                // Re-schedule on the next server tick
                server.execute(this);
            }
        };

        // Start polling on the next tick to give teleport a chance to apply
        server.execute(poll);
        return future;
    }

    public void addPlayerPosition(Location loc) {
        this.playerPositions.add(loc);
    }
}
