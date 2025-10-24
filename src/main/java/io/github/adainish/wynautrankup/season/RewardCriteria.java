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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RewardCriteria
{
    private String name = "";
    private String description = "";
    private String type = "";
    private int minElo = 0;
    private int maxElo = 0;
    private int streakCount = 0;
    private String tier = "";
    private String pokemonType = "";
    private List<String> commands =  new ArrayList<>();
    private List<String> items = new ArrayList<>();
    private List<String> rewardsDescriptions = new ArrayList<>();

    private List<Condition> conditions = new ArrayList<>(); // Multi-condition support
    private String startDate = ""; // Store as ISO string, e.g. "2024-06-01"
    private String endDate = "";   // Store as ISO string, e.g. "2024-06-30"
    private List<String> requiredTeamTypes = new ArrayList<>(); // Team composition


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinElo() {
        return minElo;
    }

    public void setMinElo(int minElo) {
        this.minElo = minElo;
    }

    public int getMaxElo() {
        return maxElo;
    }

    public void setMaxElo(int maxElo) {
        this.maxElo = maxElo;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getPokemonType() {
        return pokemonType;
    }

    public void setPokemonType(String pokemonType) {
        this.pokemonType = pokemonType;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getRequiredTeamTypes() {
        return requiredTeamTypes;
    }

    public void setRequiredTeamTypes(List<String> requiredTeamTypes) {
        this.requiredTeamTypes = requiredTeamTypes;
    }

    public boolean isMetBy(UUID playerId, int elo) {
        // Placeholder logic for checking if criteria is met
        // This should be expanded based on actual player data and conditions
        boolean meetsElo = (elo >= minElo && elo <= maxElo);
        // Additional checks for streakCount, tier, pokemonType, etc. would go here

        return meetsElo; // && other conditions
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRewardsDescriptions() {
        return rewardsDescriptions;
    }

    public void setRewardsDescriptions(List<String> rewardsDescriptions) {
        this.rewardsDescriptions = rewardsDescriptions;
    }
}
