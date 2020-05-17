package org.kakara.game.world;

import org.jetbrains.annotations.NotNull;
import org.kakara.core.GameInstance;
import org.kakara.core.Kakara;
import org.kakara.core.NameKey;
import org.kakara.core.world.WorldGenerationManager;
import org.kakara.core.world.WorldGenerator;
import org.kakara.core.world.region.Region;

import java.util.ArrayList;
import java.util.List;

public class GameWorldGenerationManager implements WorldGenerationManager {
    private GameInstance kakaraCore;
    private List<Region> regions = new ArrayList<>();
    private List<WorldGenerator> generators = new ArrayList<>();

    @Override
    public void registerRegion(Region region) {
        regions.add(region);
    }

    @Override
    public void registerChunkGenerator(@NotNull WorldGenerator worldGenerator) {
        generators.add(worldGenerator);

    }

    @Override
    public WorldGenerator getGenerator(NameKey nameKey) {
        for (WorldGenerator generator : generators) {
            if (generator.getNameKey().equals(nameKey)) {
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
        return WorldGenerationManager.class;
    }
}
