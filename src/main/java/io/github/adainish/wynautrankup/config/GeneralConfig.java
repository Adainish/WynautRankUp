package io.github.adainish.wynautrankup.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.adainish.wynautrankup.arenas.Arena;
import io.github.adainish.wynautrankup.util.Location;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeneralConfig {
    private final Gson gson;
    private final String generalConfigPath;
    private Location tpBackLocation;

    public GeneralConfig() {
        this("config/WynautRankup/general_config.json");
    }

    public GeneralConfig(String generalConfigPath) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        this.generalConfigPath = generalConfigPath;
    }

    public void load() {
        File file = new File(generalConfigPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            // Populate a default tpBackLocation
            this.tpBackLocation = new Location("minecraft:overworld", 0, 64, 0, 0, 0);
            save(); // Save the default config before reading
        }
        try (FileReader reader = new FileReader(generalConfigPath)) {
            Map<String, Location> root = gson.fromJson(reader, new TypeToken<Map<String, Location>>(){}.getType());
            if (root != null && root.containsKey("tpBackLocation")) {
                this.tpBackLocation = root.get("tpBackLocation");
            } else {
                this.tpBackLocation = new Location("minecraft:overworld", 0, 64, 0, 0, 0);
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        // Implementation for saving the general configuration
        try {
            Map<String, Location> root = new HashMap<>();
            root.put("tpBackLocation", this.tpBackLocation);
            File file = new File(generalConfigPath);
            file.getParentFile().mkdirs();
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                gson.toJson(root, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
