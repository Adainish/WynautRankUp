package io.github.adainish.wynautrankup.season;

import java.util.List;
import java.util.Set;

public class PlayerProgress
{
    private int elo;
    private int winStreak;
    private List<String> battleHistory;
    private Set<String> pokemonTypesUsed;

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public void setWinStreak(int winStreak) {
        this.winStreak = winStreak;
    }

    public List<String> getBattleHistory() {
        return battleHistory;
    }

    public void setBattleHistory(List<String> battleHistory) {
        this.battleHistory = battleHistory;
    }

    public Set<String> getPokemonTypesUsed() {
        return pokemonTypesUsed;
    }

    public void setPokemonTypesUsed(Set<String> pokemonTypesUsed) {
        this.pokemonTypesUsed = pokemonTypesUsed;
    }
}
