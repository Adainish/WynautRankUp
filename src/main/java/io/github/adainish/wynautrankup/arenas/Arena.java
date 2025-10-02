package io.github.adainish.wynautrankup.arenas;

import io.github.adainish.wynautrankup.util.Location;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

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

    public void addPlayerPosition(Location loc) {
        this.playerPositions.add(loc);
    }
}
