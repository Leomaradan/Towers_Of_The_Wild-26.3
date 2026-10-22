package com._idrae.towers_of_the_wild;

import com._idrae.towers_of_the_wild.world.TowersWorldgen;
import net.fabricmc.api.ModInitializer;

public class TowersOfTheWildFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        TowersOfTheWild.init();
        TowersWorldgen.registerDirectly();
    }
}
