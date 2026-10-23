package com._idrae.towers_of_the_wild.world;

import com._idrae.towers_of_the_wild.config.TowersOfTheWildConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

/**
 * A {@code random_spread} placement whose spacing and separation come from the mod config instead of the data pack.
 * Extending the vanilla placement keeps {@code /locate} and explorer maps working.
 */
public class ConfigurableSpreadPlacement extends RandomSpreadStructurePlacement {

    public static final MapCodec<ConfigurableSpreadPlacement> CODEC = RecordCodecBuilder.mapCodec(i -> placementCodec(i)
            .and(Rarity.CODEC.fieldOf("rarity").forGetter(p -> p.rarity))
            .apply(i, ConfigurableSpreadPlacement::new));

    private final Rarity rarity;

    public ConfigurableSpreadPlacement(
            Vec3i locateOffset,
            FrequencyReductionMethod frequencyReductionMethod,
            float frequency,
            int salt,
            Optional<ExclusionZone> exclusionZone,
            Rarity rarity
    ) {
        // Spacing is read once, when the world's registries are loaded.
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone,
                rarity.spacing(), rarity.separation(), RandomSpreadType.LINEAR);
        this.rarity = rarity;
    }

    @Override
    public boolean isStructureChunk(ChunkGeneratorStructureState state, int sourceX, int sourceZ) {
        return rarity.enabled.getAsBoolean() && super.isStructureChunk(state, sourceX, sourceZ);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MapCodec<RandomSpreadStructurePlacement> codec() {
        // The parent signature is not wildcarded; CODEC only ever encodes instances of this class.
        return (MapCodec<RandomSpreadStructurePlacement>) (MapCodec<?>) CODEC;
    }

    public enum Rarity implements StringRepresentable {
        REGULAR("regular", TowersOfTheWildConfig::rarity, () -> true),
        DERELICT("derelict", TowersOfTheWildConfig::derelictRarity, () -> true),
        OCEAN("ocean", TowersOfTheWildConfig::oceanRarity, TowersOfTheWildConfig::spawnOceanTowers);

        public static final Codec<Rarity> CODEC = StringRepresentable.fromEnum(Rarity::values);

        private final String name;
        private final IntSupplier spacing;
        private final BooleanSupplier enabled;

        Rarity(String name, IntSupplier spacing, BooleanSupplier enabled) {
            this.name = name;
            this.spacing = spacing;
            this.enabled = enabled;
        }

        int spacing() {
            return spacing.getAsInt();
        }

        // Same ratio as the 1.16 StructureSeparationSettings(rarity, rarity / 3, salt).
        int separation() {
            return Math.max(1, spacing() / 3);
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
