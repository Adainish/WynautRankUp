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

import io.github.adainish.wynautrankup.WynautRankUp;

import java.util.HashMap;
import java.util.Map;

public class ArenaManager
{
    private Map<String, Arena> arenas = new HashMap<>();

    public void addArena(Arena arena) {
        arenas.put(arena.getName(), arena);
    }

    public Arena getArena(String name) {
        return arenas.get(name);
    }

    public void removeArena(String name) {
        arenas.remove(name);
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }

    public Arena getAvailableArena() {
        return arenas.values().stream()
                .filter(arena -> !arena.isInUse())
                .findFirst()
                .orElse(null);
    }

    public void populateArenas(Map<String, Arena> arenas) {
        this.arenas = arenas;
    }

    public void reload() {
        this.populateArenas(WynautRankUp.instance.arenaConfig.loadArenas());
    }
}
