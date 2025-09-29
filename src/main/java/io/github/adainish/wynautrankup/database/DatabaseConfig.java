package io.github.adainish.wynautrankup.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DatabaseConfig
{
    public String databaseUrl = "";

    public static DatabaseConfig loadFromFile() {
        File dir = new File("config/WynautRankup");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "database_config.json");
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

        if (!file.exists()) {
            DatabaseConfig defaultConfig = new DatabaseConfig();
            defaultConfig.databaseUrl = "jdbc:mysql://host:port/dbname";
            try (FileWriter writer = new FileWriter(file, false)) {
                gson.toJson(defaultConfig, writer);
            } catch (IOException e) {
                System.out.println("Failed to write default DB config: " + e.getMessage());
            }
            System.out.println("Default DB config written. Please edit and restart.");
            return defaultConfig;
        }

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, DatabaseConfig.class);
        } catch (IOException e) {
            System.out.println("Failed to load DB config: " + e.getMessage());
            return new DatabaseConfig();
        }
    }
}
