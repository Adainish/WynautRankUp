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