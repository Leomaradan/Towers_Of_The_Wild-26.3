# Towers Of The Wild (Minecraft 26.3)

Cool towers to enhance exploration! This is a port of the original 1.16.3 Forge mod to **Minecraft 26.3**, built for both **Fabric** and **NeoForge** from one code base.

The mod is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (see `LICENSE`).

## Project layout

| Folder     | Content |
|------------|---------|
| `common`   | Code and data shared by both loaders: the `towers_of_the_wild:tower` structure type, the config-driven structure placement, the config, and every structure/loot/worldgen file. |
| `fabric`   | Fabric entrypoint and `fabric.mod.json`. |
| `neoforge` | NeoForge entrypoint and `neoforge.mods.toml`. |

The build follows the [MultiLoader Template](https://github.com/jaredlll08/MultiLoader-Template): `common` is compiled against vanilla, and its sources and resources are merged into each loader jar.

## Building

Requires **Java 25**.

```sh
./gradlew build
```

The jars end up in `fabric/build/libs/` and `neoforge/build/libs/` (`towers_of_the_wild-<loader>-26.3-<version>.jar`). The Fabric jar needs Fabric API.

Run a dev client or server with `./gradlew :fabric:runClient`, `:fabric:runServer`, `:neoforge:runClient` or `:neoforge:runServer`.

## Towers

| Structure | Biomes (tag `towers_of_the_wild:has_structure/...`) | Structure set |
|-----------|------------------------------------------------------|---------------|
| `tower` | plains, forests, taigas, windswept hills, savannas, deserts, swamps, badlands, mushroom fields, meadows... | `towers` |
| `jungle_tower` | jungles | `towers` |
| `ice_tower` | snowy plains, ice spikes, snowy slopes | `towers` |
| `derelict_grass_tower` | plains, forests, taigas, windswept hills, savannas | `derelict_towers` |
| `derelict_tower` | deserts, swamps, badlands, mushroom fields | `derelict_towers` |
| `ocean_tower` | deep (cold/frozen) oceans | `ocean_towers` |
| `ocean_warm_tower` | deep lukewarm ocean | `ocean_towers` |

Towers only spawn on flat enough terrain, and ocean towers only where the sea floor is deep enough. Everything under `common/src/main/resources/data/towers_of_the_wild/` can be overridden by a data pack.

## Config

`config/towers_of_the_wild.json`, created on first launch. Changes apply on the next world load.

| Option | Default | Description |
|--------|---------|-------------|
| `towerRarity` | 20 | Spacing in chunks between regular/jungle/ice towers (3-200). |
| `derelictTowerRarity` | 72 | Spacing in chunks between derelict towers (3-200). |
| `spawnOceanTowers` | true | Whether ocean towers spawn at all. |
| `oceanTowerRarity` | 32 | Spacing in chunks between ocean towers (3-200). |
| `allModBiomesBlackList` | midnight, the_bumblezone | Mod ids whose biomes never get towers. |
| `biomeBlackList` | rivers, beaches | Biome ids that never get towers. |
| `waystonesCompat` | true | With the Waystones mod installed, towers get a waystone at the top. |
