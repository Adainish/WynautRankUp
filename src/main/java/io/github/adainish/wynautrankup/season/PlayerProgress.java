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
