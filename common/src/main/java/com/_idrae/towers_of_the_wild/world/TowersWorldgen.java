package com._idrae.towers_of_the_wild.world;

import com._idrae.towers_of_the_wild.TowersOfTheWild;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.function.BiConsumer;

/**
 * The only code-defined worldgen objects; structures, structure sets, template pools and biome tags are all
 * plain data under {@code data/towers_of_the_wild/}.
 */
public class TowersWorldgen {

    public static final Identifier TOWER_STRUCTURE_ID = TowersOfTheWild.id("tower");
    public static final Identifier CONFIGURABLE_SPREAD_ID = TowersOfTheWild.id("configurable_spread");

    public static final StructureType<TowerStructure> TOWER_STRUCTURE = () -> TowerStructure.CODEC;

    public static void registerStructureTypes(BiConsumer<Identifier, StructureType<?>> register) {
        register.accept(TOWER_STRUCTURE_ID, TOWER_STRUCTURE);
    }

    public static void registerStructurePlacements(BiConsumer<Identifier, MapCodec<? extends StructurePlacement>> register) {
        register.accept(CONFIGURABLE_SPREAD_ID, ConfigurableSpreadPlacement.CODEC);
    }

    // For loaders that let mods write to the vanilla registries directly.
    public static void registerDirectly() {
        registerStructureTypes((id, type) -> Registry.register(BuiltInRegistries.STRUCTURE_TYPE, id, type));
        registerStructurePlacements((id, codec) -> Registry.register(BuiltInRegistries.STRUCTURE_PLACEMENT, id, codec));
    }
}
