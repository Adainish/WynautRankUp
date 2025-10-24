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
package io.github.adainish.wynautrankup.validator;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TeamValidationConfig
{
    public List<BannedPokemonRule> bannedPokemon = new ArrayList<>();

    private void writeErrorsToFile(List<String> errors) {
        File dir = new File("config/WynautRankup");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "banned_rules_errors.txt");
        try (FileWriter writer = new FileWriter(file, false)) {
            for (String error : errors) writer.write(error + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Failed to write config errors: " + e.getMessage());
        }
    }
    public TeamValidationConfig loadFromFile() {
        File file = new File("config/WynautRankup/banned_pokemon_rules.json");
        if (!file.exists()) {
            System.out.println("Config file not found, writing default config.");
            writeDefaultConfig();
            System.out.println("Config file written successfully.");
            System.out.println("Please review and modify the config file as needed, then restart the server. (Or use the reload command)");
            System.out.println("Loading newly created config file.");
            return loadFromFile();
        }
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        List<String> errors = new ArrayList<>();
        try (FileReader reader = new FileReader(file)) {
            List<String> ruleStrings = gson.fromJson(reader, new TypeToken<List<String>>(){}.getType());
            TeamValidationConfig config = new TeamValidationConfig();
            if (ruleStrings != null) {
                ruleStrings.forEach(ruleStr -> {
                    BannedPokemonRule rule = BannedPokemonRule.parse(ruleStr, errors);
                    if (rule != null) {
                        config.bannedPokemon.add(rule);
                    } else {
                        errors.add("Failed to parse rule: " + ruleStr);
                    }
                });
            }
            if (!errors.isEmpty()) {
                writeErrorsToFile(errors);
            }
            return config;
        } catch (IOException e) {
            System.out.println("Failed to load config: " + e.getMessage());
            return new TeamValidationConfig();
        }
    }


    //write the default config to file
    public void writeDefaultConfig() {
        File dir = new File("config/WynautRankup");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "banned_pokemon_rules.json");
        if (file.exists()) {
            return; // Do not overwrite existing config
        }
        List<String> defaultRules = new ArrayList<>();
        defaultRules.add("species:pikachu;form:cosplay;ability:static;held_items:cobblemon:leftovers;moves:thunderbolt");
        defaultRules.add("species:charizard;form:mega-x;ability:tough-claws;held_items:cobblemon:charizardite_x;moves:flareblitz");
            defaultRules.add("species:mewtwo;form:mega-y;ability:insomnia;held_items:cobblemon:mewtwonite_y;moves:psystrike");
            defaultRules.add("species:garchomp;ability:roughskin;held_items:cobblemon:lifeorb;moves:earthquake");
            defaultRules.add("species:greninja;ability:protean;held_items:cobblemon:choice-scarf;moves:hydropump");
            defaultRules.add("species:dragonite;ability:multiscale;held_items:cobblemon:assault-vest;moves:outrage");
            defaultRules.add("species:blaziken;ability:speedboost;held_items:cobblemon:blazikenite;moves:flareblitz");
            defaultRules.add("species:lucario;ability:adaptability;held_items:cobblemon:lucarionite;moves:aurasphere");
            defaultRules.add("species:gengar;form:mega;ability:levitate;held_items:cobblemon:gengarite;moves:shadowball");
            defaultRules.add("species:alakazam;form:mega;ability:trace;held_items:cobblemon:alakazite;moves:psychic");
            defaultRules.add("species:tyranitar;form:mega;ability:sandstream;held_items:cobblemon:tyranitarite;moves:stoneedge");
            defaultRules.add("species:metagross;form:mega;ability:toughclaws;held_items:cobblemon:metagrossite;moves:meteormash");
            defaultRules.add("species:salamence;form:mega;ability:aerilate;held_items:cobblemon:salamencite;moves:return");
            defaultRules.add("species:scizor;form:mega;ability:toughclaws;held_items:cobblemon:scizorite;moves:bulletpunch");
            defaultRules.add("species:aegislash;ability:stancechange;held_items:cobblemon:leftovers;moves:kingsshield");
            defaultRules.add("species:hydreigon;ability:levitate;held_items:cobblemon:blackglasses;moves:darkpulse");
            defaultRules.add("species:volcarona;ability:flamebody;held_items:cobblemon:sitrusberry;moves:morningsun");
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file, false)) {
            gson.toJson(defaultRules, writer);
        } catch (IOException e) {
            System.out.println("Failed to write default config: " + e.getMessage());
        }
    }

}
