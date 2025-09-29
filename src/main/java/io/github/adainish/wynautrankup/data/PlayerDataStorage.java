package io.github.adainish.wynautrankup.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStorage
{
    private final Map<UUID, Player> playerData = new HashMap<>();

    public void savePlayer(Player player) {
        playerData.put(player.getId(), player);
    }

    public Player getPlayer(UUID playerId) {
        return playerData.get(playerId);
    }

    public void removePlayer(UUID playerId) {
        playerData.remove(playerId);
    }

    public boolean hasPlayer(UUID playerId) {
        return playerData.containsKey(playerId);
    }

    public Iterable<Player> getAllPlayers() {
        return playerData.values();
    }
}
