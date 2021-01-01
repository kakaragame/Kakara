package org.kakara.game.world;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.common.ControllerKey;
import org.kakara.core.common.GameInstance;
import org.kakara.core.common.world.WorldGenerationRegistry;
import org.kakara.core.common.world.WorldGenerator;
import org.kakara.core.common.world.region.Region;

import java.util.ArrayList;
import java.util.List;

public class GameWorldGenerationRegistry implements WorldGenerationRegistry {
    private GameInstance kakaraCore;
    private final List<Region> regions = new ArrayList<>();
    private final List<WorldGenerator> generators = new ArrayList<>();

    @Override
    public void registerRegion(Region region) {
        regions.add(region);
    }


    @Override
    public void registerChunkGenerator(@NotNull WorldGenerator worldGenerator) {
        generators.add(worldGenerator);

    }

    @Override
    public WorldGenerator getGenerator(ControllerKey nameKey) {
        for (WorldGenerator generator : generators) {
            if (generator.getControllerKey().equals(nameKey)) {
                return generator;
            }
        }
        return null;
    }

    @Override
    public List<Region> getRegions() {
        return regions;
    }

    @Override
    public List<WorldGenerator> getChunkGenerators() {
        return generators;
    }

    @Override
    public void load(GameInstance kakaraCore) {
        this.kakaraCore = kakaraCore;
    }

    @Override
    public Class<?> getStageClass() {
        return WorldGenerationRegistry.class;
    }
}
