package io.github.adainish.wynautrankup.ranks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankManager {
    public String configPath = "config/WynautRankup/ranks.json";
    private final List<Rank> ranks = new ArrayList<>();

    public RankManager() {
        loadRanks();
    }

    private void loadRanks() {
        File file = new File(configPath);
        if (!file.exists()) {
            // Create parent directories if needed
            file.getParentFile().mkdirs();
            // Default ranks
            List<Rank> defaults = List.of(
                    new Rank("Bronze","Bronze", 1000),
                    new Rank("Silver","Silver", 1500),
                    new Rank("Gold", "Gold", 2000),
                    new Rank("Platinum","Platinum", 2500),
                    new Rank("Diamond", "Diamond", 3000)
            );
            try (FileWriter writer = new FileWriter(file)) {
                new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(defaults, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try (FileReader reader = new FileReader(configPath)) {
            List<Rank> loaded = new Gson().fromJson(reader, new TypeToken<List<Rank>>(){}.getType());
            loaded.sort(Comparator.comparingInt(Rank::getMinElo));
            ranks.clear();
            ranks.addAll(loaded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Rank getRankForElo(int elo) {
        List<Rank> ranks = this.ranks;
        if (ranks.isEmpty()) {
            throw new IllegalStateException("No ranks configured");
        }
        ranks.sort(Comparator.comparingInt(Rank::getMinElo));
        Rank result = ranks.getFirst();
        for (Rank rank : ranks)
            if (elo >= rank.getMinElo()) {
                result = rank;
            } else {
                break;
            }
        return result;
    }

    public String getRankStringForElo(int elo) {
        Rank rank = getRankForElo(elo);
        return rank.getDisplayName();
    }
}
