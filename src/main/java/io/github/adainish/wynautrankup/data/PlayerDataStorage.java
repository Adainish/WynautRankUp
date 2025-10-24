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
