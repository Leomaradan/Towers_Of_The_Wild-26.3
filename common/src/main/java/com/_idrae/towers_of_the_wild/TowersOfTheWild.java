package com._idrae.towers_of_the_wild;

import com._idrae.towers_of_the_wild.config.TowersOfTheWildConfig;
import com._idrae.towers_of_the_wild.platform.Services;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TowersOfTheWild {

    public static final String MOD_ID = "towers_of_the_wild";
    public static final String MOD_NAME = "Towers Of The Wild";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    // Called by each loader's entrypoint, before any world is loaded.
    public static void init() {
        TowersOfTheWildConfig.load(Services.PLATFORM.getConfigDirectory());
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
