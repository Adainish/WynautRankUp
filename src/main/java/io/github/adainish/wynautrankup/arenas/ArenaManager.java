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
