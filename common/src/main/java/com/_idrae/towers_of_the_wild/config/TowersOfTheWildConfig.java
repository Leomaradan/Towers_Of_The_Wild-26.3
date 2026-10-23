package com._idrae.towers_of_the_wild.config;

import com._idrae.towers_of_the_wild.TowersOfTheWild;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loader-agnostic replacement for the old ForgeConfigSpec common config.
 * Stored as {@code config/towers_of_the_wild.json}; values are read when a world is loaded,
 * so changes need a world reload (or a server restart) to apply.
 */
public class TowersOfTheWildConfig {

    public static final int MIN_RARITY = 3;
    public static final int MAX_RARITY = 200;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String FILE_NAME = TowersOfTheWild.MOD_ID + ".json";

    private static Values values = new Values();

    public static int rarity() {
        return clampRarity(values.towerRarity);
    }

    public static int derelictRarity() {
        return clampRarity(values.derelictTowerRarity);
    }

    public static int oceanRarity() {
        return clampRarity(values.oceanTowerRarity);
    }

    public static boolean spawnOceanTowers() {
        return values.spawnOceanTowers;
    }

    public static List<String> allModBiomesBlackList() {
        return values.allModBiomesBlackList;
    }

    public static List<String> biomeBlackList() {
        return values.biomeBlackList;
    }

    public static boolean waystonesCompat() {
        return values.waystonesCompat;
    }

    public static void load(Path configDir) {
        Path file = configDir.resolve(FILE_NAME);
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                Values loaded = GSON.fromJson(reader, Values.class);
                if (loaded != null) {
                    values = loaded.withDefaults();
                }
            } catch (IOException | JsonParseException e) {
                TowersOfTheWild.LOGGER.error("Could not read {}, using default values", file, e);
                return;
            }
        }
        // Rewrite the file so that new options and comments show up after an update.
        save(file);
        TowersOfTheWild.LOGGER.info("Config set");
    }

    private static void save(Path file) {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                GSON.toJson(values, writer);
            }
        } catch (IOException e) {
            TowersOfTheWild.LOGGER.error("Could not write {}", file, e);
        }
    }

    private static int clampRarity(int rarity) {
        return Math.max(MIN_RARITY, Math.min(MAX_RARITY, rarity));
    }

    private static class Values {
        String _towerRarity = "How rarely the towers will spawn (low: common, high: rare). Range: 3-200. Default: 20";
        int towerRarity = 20;
        String _derelictTowerRarity = "How rarely the derelict towers will spawn (low: common, high: rare). Range: 3-200. Default: 72";
        int derelictTowerRarity = 72;
        String _spawnOceanTowers = "Make towers spawn in the ocean. Default: true";
        boolean spawnOceanTowers = true;
        String _oceanTowerRarity = "How rarely the ocean towers will spawn (low: common, high: rare). Range: 3-200. Default: 32";
        int oceanTowerRarity = 32;
        String _allModBiomesBlackList = "A list of mod ids. In each and every biome added by those mods, the towers will not spawn. Default: The Midnight, The Bumblezone";
        List<String> allModBiomesBlackList = new ArrayList<>(List.of(
                "midnight",
                "the_bumblezone"
        ));
        String _biomeBlackList = "A list of biomes where the towers will not spawn. Default: Rivers, Beaches";
        List<String> biomeBlackList = new ArrayList<>(List.of(
                "minecraft:river",
                "minecraft:frozen_river",
                "minecraft:beach",
                "minecraft:stony_shore",
                "minecraft:snowy_beach",
                "biomesoplenty:gravel_beach",
                "biomesoplenty:white_beach"
        ));
        String _waystonesCompat = "If the Waystones mod is installed and this is set to true, towers will spawn with a waystone at the top. If the Waystones mod is not installed, this will have no effect. Default: true";
        boolean waystonesCompat = true;

        // Gson leaves fields missing from the file as null, and comments must always reflect the current version.
        Values withDefaults() {
            Values defaults = new Values();
            if (allModBiomesBlackList == null) allModBiomesBlackList = defaults.allModBiomesBlackList;
            if (biomeBlackList == null) biomeBlackList = defaults.biomeBlackList;
            _towerRarity = defaults._towerRarity;
            _derelictTowerRarity = defaults._derelictTowerRarity;
            _spawnOceanTowers = defaults._spawnOceanTowers;
            _oceanTowerRarity = defaults._oceanTowerRarity;
            _allModBiomesBlackList = defaults._allModBiomesBlackList;
            _biomeBlackList = defaults._biomeBlackList;
            _waystonesCompat = defaults._waystonesCompat;
            return this;
        }
    }
}
