package com._idrae.towers_of_the_wild.world;

import com._idrae.towers_of_the_wild.config.TowersOfTheWildConfig;
import com._idrae.towers_of_the_wild.platform.Services;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
import java.util.Optional;

/**
 * A jigsaw structure that only generates on flat enough terrain, outside of the biomes blacklisted in the config,
 * and that swaps its top piece for the waystone variant when Waystones is installed.
 */
public class TowerStructure extends Structure {

    public static final MapCodec<TowerStructure> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            settingsCodec(i),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
            Codec.intRange(0, JigsawStructure.MAX_DEPTH).fieldOf("size").forGetter(s -> s.maxDepth),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(s -> s.startHeight),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(s -> s.projectStartToHeightmap),
            JigsawStructure.MaxDistance.CODEC.fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(s -> s.liquidSettings),
            Codec.intRange(1, 64).fieldOf("flatness_check_size").forGetter(s -> s.flatnessCheckSize),
            Codec.BOOL.optionalFieldOf("flatness_check_center", false).forGetter(s -> s.flatnessCheckCenter),
            Codec.intRange(0, 384).optionalFieldOf("max_height_difference", 4).forGetter(s -> s.maxHeightDifference),
            Codec.INT.optionalFieldOf("max_ocean_floor_height").forGetter(s -> s.maxOceanFloorHeight),
            Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("waystone_pool_aliases", List.of()).forGetter(s -> s.waystonePoolAliases)
    ).apply(i, TowerStructure::new));

    private final Holder<StructureTemplatePool> startPool;
    private final int maxDepth;
    private final HeightProvider startHeight;
    private final Optional<Heightmap.Types> projectStartToHeightmap;
    private final JigsawStructure.MaxDistance maxDistanceFromCenter;
    private final LiquidSettings liquidSettings;
    private final int flatnessCheckSize;
    private final boolean flatnessCheckCenter;
    private final int maxHeightDifference;
    private final Optional<Integer> maxOceanFloorHeight;
    private final List<PoolAliasBinding> waystonePoolAliases;

    public TowerStructure(
            Structure.StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            int maxDepth,
            HeightProvider startHeight,
            Optional<Heightmap.Types> projectStartToHeightmap,
            JigsawStructure.MaxDistance maxDistanceFromCenter,
            LiquidSettings liquidSettings,
            int flatnessCheckSize,
            boolean flatnessCheckCenter,
            int maxHeightDifference,
            Optional<Integer> maxOceanFloorHeight,
            List<PoolAliasBinding> waystonePoolAliases
    ) {
        super(settings);
        this.startPool = startPool;
        this.maxDepth = maxDepth;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.liquidSettings = liquidSettings;
        this.flatnessCheckSize = flatnessCheckSize;
        this.flatnessCheckCenter = flatnessCheckCenter;
        this.maxHeightDifference = maxHeightDifference;
        this.maxOceanFloorHeight = maxOceanFloorHeight;
        this.waystonePoolAliases = waystonePoolAliases;
    }

    @Override
    protected Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        int x = chunkPos.getMinBlockX();
        int z = chunkPos.getMinBlockZ();

        if (!isTerrainFlat(context, x, z)) {
            return Optional.empty();
        }
        if (maxOceanFloorHeight.isPresent() && height(context, x, z, Heightmap.Types.OCEAN_FLOOR_WG) > maxOceanFloorHeight.get()) {
            return Optional.empty();
        }
        if (isBlacklistedBiome(context, x + flatnessCheckSize / 2, z + flatnessCheckSize / 2)) {
            return Optional.empty();
        }

        int y = startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
        BlockPos startPos = new BlockPos(x, y, z);
        List<PoolAliasBinding> poolAliases = useWaystones() ? waystonePoolAliases : List.of();
        return JigsawPlacement.addPieces(
                context,
                startPool,
                Optional.empty(),
                maxDepth,
                startPos,
                false,
                projectStartToHeightmap,
                maxDistanceFromCenter,
                PoolAliasLookup.create(poolAliases, startPos, context.seed()),
                DimensionPadding.ZERO,
                liquidSettings
        );
    }

    private boolean isTerrainFlat(Structure.GenerationContext context, int x, int z) {
        int size = flatnessCheckSize;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int[][] samples = flatnessCheckCenter
                ? new int[][]{{0, 0}, {0, size}, {size, 0}, {size, size}, {size / 2, size / 2}}
                : new int[][]{{0, 0}, {0, size}, {size, 0}, {size, size}};
        for (int[] offset : samples) {
            int height = height(context, x + offset[0], z + offset[1], Heightmap.Types.WORLD_SURFACE_WG);
            min = Math.min(min, height);
            max = Math.max(max, height);
        }
        return max - min <= maxHeightDifference;
    }

    private static int height(Structure.GenerationContext context, int x, int z, Heightmap.Types heightmap) {
        ChunkGenerator generator = context.chunkGenerator();
        return generator.getBaseHeight(x, z, heightmap, context.heightAccessor(), context.randomState());
    }

    private static boolean isBlacklistedBiome(Structure.GenerationContext context, int x, int z) {
        int y = height(context, x, z, Heightmap.Types.WORLD_SURFACE_WG);
        Holder<Biome> biome = context.biomeResolver().getNoiseBiome(
                QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z));
        return biome.unwrapKey().map(key -> {
            Identifier id = key.identifier();
            return TowersOfTheWildConfig.biomeBlackList().contains(id.toString())
                    || TowersOfTheWildConfig.allModBiomesBlackList().contains(id.getNamespace());
        }).orElse(false);
    }

    private static boolean useWaystones() {
        return TowersOfTheWildConfig.waystonesCompat() && Services.PLATFORM.isModLoaded("waystones");
    }

    @Override
    public StructureType<?> type() {
        return TowersWorldgen.TOWER_STRUCTURE;
    }
}
