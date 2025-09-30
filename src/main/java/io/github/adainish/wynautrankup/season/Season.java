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
