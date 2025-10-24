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

import com.cobblemon.mod.common.pokemon.Pokemon;

import java.util.List;

public class PlayerQueueEntry {
    public Player player;
    public List<Pokemon> team;

    public PlayerQueueEntry(Player player, List<Pokemon> team) {
        this.player = player;
        this.team = team;
    }

    public Player getPlayer() { return player; }
    public List<Pokemon> getTeam() { return team; }
}