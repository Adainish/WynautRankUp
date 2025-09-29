package io.github.adainish.wynautrankup.season;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RewardCriteria
{
    private String type;
    private int minElo;
    private int maxElo;
    private int streakCount;
    private String tier;
    private String pokemonType;
    private List<String> commands;
    private List<String> items;

    private List<Condition> conditions; // Multi-condition support
    private LocalDate startDate; // Time-based goal start
    private LocalDate endDate;   // Time-based goal end
    private List<String> requiredTeamTypes; // Team composition


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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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
}
