package com._idrae.towers_of_the_wild;

import com._idrae.towers_of_the_wild.world.TowersWorldgen;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(TowersOfTheWild.MOD_ID)
public class TowersOfTheWildNeoForge {

    public TowersOfTheWildNeoForge(IEventBus modEventBus) {
        TowersOfTheWild.init();
        modEventBus.addListener(TowersOfTheWildNeoForge::register);
    }

    private static void register(RegisterEvent event) {
        event.register(Registries.STRUCTURE_TYPE, helper -> TowersWorldgen.registerStructureTypes(helper::register));
        event.register(Registries.STRUCTURE_PLACEMENT, helper -> TowersWorldgen.registerStructurePlacements(helper::register));
    }
}
