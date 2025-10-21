package io.github.adainish.wynautrankup.season;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.adainish.wynautrankup.WynautRankUp;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SeasonManager
{
    private String configDir = "config/WynautRankup/seasons";
    private Season currentSeason;
    private final List<Season> seasons = new ArrayList<>();

    public void loadSeasons() {
        File dir = new File(configDir);
        if (!dir.exists()) dir.mkdirs();
        for (File file : dir.listFiles((d, name) -> name.endsWith(".json"))) {
            try (FileReader reader = new FileReader(file)) {
                Season season = new Gson().fromJson(reader, Season.class);
                if (isValidSeason(season)) {
                    seasons.add(season);
                } else {
                    System.err.println("Invalid season config: " + file.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Fixed to parse dd/MM/yyyy and compute remaining time until that day ends.
    public String getTimeUntilSeasonEnds() {
        if (currentSeason == null || currentSeason.getRewardDate() == null) return "No active season";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate rewardDay = LocalDate.parse(currentSeason.getRewardDate(), formatter);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = rewardDay.atTime(LocalTime.of(23, 59, 59));
            if (now.isAfter(end)) return "Season ended";

            Duration duration = Duration.between(now, end);
            long days = duration.toDays();
            long hours = duration.minusDays(days).toHours();
            long minutes = duration.minusDays(days).minusHours(hours).toMinutes();
            long seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).getSeconds();
            return String.format("%d days %d hours %d minutes %d seconds", days, hours, minutes, seconds);
        } catch (Exception e) {
            return "Invalid season end date format";
        }
    }

    public void setCurrentSeasonByActiveDate()
    {
        if (seasons.isEmpty()) {
            currentSeason = null;
            return;
        }
        seasons.sort((s1, s2) -> s2.getRewardDate().compareTo(s1.getRewardDate()));
        currentSeason = seasons.getFirst();
    }

    public void setCurrentSeason(Season season) {
        if (season != null && seasons.contains(season)) {
            this.currentSeason = season;
        } else {
            throw new IllegalArgumentException("Season is null or not loaded.");
        }
    }

    // Now returns CompletableFuture, awaits DB evaluation and dispatch, then clears the season.
    public CompletableFuture<Void> forceEndCurrentSeasonAndReward() {
        if (currentSeason == null) {
            System.out.println("[SeasonManager] No active season to force-end.");
            return CompletableFuture.completedFuture(null);
        }

        Season seasonToEnd = currentSeason;
        return WynautRankUp.instance.playerDataManager
                .evaluateAndDistributeRewards(seasonToEnd)
                .thenCompose(v -> WynautRankUp.instance.playerDataManager.dispatchPendingRewardsToOnlinePlayers())
                .thenAccept(delivered -> {
                    System.out.println("[SeasonManager] Force-ended season '" + seasonToEnd.getName() + "'. Delivered " + delivered + " reward actions to online players.");
                })
                .whenComplete((v, t) -> currentSeason = null);
    }

    public void reloadSeasons() {
        seasons.clear();
        loadSeasons();
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    private boolean isValidSeason(Season season) {
        return season != null
                && season.getName() != null
                && season.getDisplayName() != null
                && season.getDescription() != null;
    }

    public String getCurrentSeasonId() {
        return currentSeason != null ? currentSeason.getName() : "default";
    }

    public Season getSeasonById(String id) {
        return seasons.stream().filter(season -> season.getName().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public void addSeason(Season newSeason) {
        if (isValidSeason(newSeason) && getSeasonById(newSeason.getName()) == null) {
            seasons.add(newSeason);
            File file = new File(configDir, newSeason.getName() + ".json");
            try (java.io.FileWriter writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(newSeason, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Invalid or duplicate season.");
        }
    }
}
