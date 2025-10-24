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
package io.github.adainish.wynautrankup.arenas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.adainish.wynautrankup.util.Location;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaConfig
{
    private final Gson gson;
    private final String arenaConfigPath;
    private final Type arenaMapType = new TypeToken<Map<String, Arena>>(){}.getType();

    public ArenaConfig() {
        this("config/WynautRankup/arenas.json");
    }

    public ArenaConfig(String arenaConfigPath) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
        this.arenaConfigPath = arenaConfigPath;
    }

    public Map<String, Arena> loadArenas() {
        File file = new File(arenaConfigPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            // Populate with a default arena
            Map<String, Arena> defaultArenas = new HashMap<>();
            //populate a list of default player locations
            List<Location> defaultPositions = new ArrayList<>();
            defaultPositions.add(new Location("minecraft:overworld", 0, 64, 0, 0, 0));
            defaultPositions.add(new Location("minecraft:overworld", 10, 64, 0, 0, 0));
            Arena defaultArena = new Arena("default", "minecraft:overworld", defaultPositions);
            // Set other default fields as needed, e.g. positions
            defaultArenas.put("default", defaultArena);
            saveArenas(defaultArenas);
        }
        try (FileReader reader = new FileReader(arenaConfigPath)) {
            Map<String, Map<String, Arena>> root = gson.fromJson(reader, new TypeToken<Map<String, Map<String, Arena>>>(){}.getType());
            return root != null && root.containsKey("arenas") ? root.get("arenas") : new HashMap<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }


    public void saveArenas(Map<String, Arena> arenas) {
        try (FileWriter writer = new FileWriter(arenaConfigPath)) {
            Map<String, Object> root = new HashMap<>();
            root.put("arenas", arenas);
            gson.toJson(root, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
