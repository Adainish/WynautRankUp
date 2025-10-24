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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Season
{
    private String name = "";
    private String displayName = "";
    private String description = "";
    private String rewardDate = ""; // e.g. "1" for 1st of month
    private List<RewardCriteria> rewardCriteria = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRewardDate() {
        return rewardDate;
    }

    public void setRewardDate(String rewardDate) {
        this.rewardDate = rewardDate;
    }

    public List<RewardCriteria> getRewardCriteria() {
        return rewardCriteria;
    }

    public void setRewardCriteria(List<RewardCriteria> rewardCriteria) {
        this.rewardCriteria = rewardCriteria;
    }

    public boolean isRewardDay(LocalDate today) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate rewardDay = LocalDate.parse(rewardDate, formatter);
            return today.equals(rewardDay);
        } catch (DateTimeParseException e) {
            return false; // Invalid format
        }
    }

    public boolean isActive() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate rewardDay = LocalDate.parse(rewardDate, formatter);
            return LocalDate.now().isBefore(rewardDay) || LocalDate.now().isEqual(rewardDay);
        } catch (DateTimeParseException e) {
            return false; // Invalid format
        }
    }
}
