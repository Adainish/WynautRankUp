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
package io.github.adainish.wynautrankup.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.adainish.wynautrankup.util.Location;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class GeneralConfig {
    private final Gson gson;
    private final String generalConfigPath;
    public Location tpBackLocation;
    public double repeatOpponentEloModifier = 0.5; // 50% by default

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
            this.repeatOpponentEloModifier = 0.5;
            save(); // Save the default config before reading
        }
        try (FileReader reader = new FileReader(generalConfigPath)) {
            Map<String, Object> root = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
            if (root != null && root.containsKey("tpBackLocation")) {
                this.tpBackLocation = gson.fromJson(gson.toJson(root.get("tpBackLocation")), Location.class);
            } else {
                this.tpBackLocation = new Location("minecraft:overworld", 0, 64, 0, 0, 0);
                save();
            }

            if (root != null && root.containsKey("repeatOpponentEloModifier")) {
                this.repeatOpponentEloModifier = Double.parseDouble(root.get("repeatOpponentEloModifier").toString());
            } else {
                this.repeatOpponentEloModifier = 0.5;
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            Map<String, Object> root = new HashMap<>();
            root.put("tpBackLocation", this.tpBackLocation);
            root.put("repeatOpponentEloModifier", this.repeatOpponentEloModifier);
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
