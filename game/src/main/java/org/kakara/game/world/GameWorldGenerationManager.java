package org.kakara.game.world;

import org.kakara.core.KakaraCore;
import org.kakara.core.world.ChunkGenerator;
import org.kakara.core.world.WorldGenerationManager;
import org.kakara.core.world.region.Region;

import java.util.ArrayList;
import java.util.List;

public class GameWorldGenerationManager implements WorldGenerationManager {
    private KakaraCore kakaraCore;
    private List<Region> regions = new ArrayList<>();
    private List<ChunkGenerator> generators = new ArrayList<>();

    @Override
    public void registerRegion(Region region) {
        regions.add(region);
    }

    @Override
    public void registerChunkGenerator(ChunkGenerator chunkGenerator) {
        generators.add(chunkGenerator);
    }

    @Override
    public List<Region> getRegions() {
        return regions;
    }

    @Override
    public List<ChunkGenerator> getChunkGenerators() {
        return generators;
    }

    @Override
    public void load(KakaraCore kakaraCore) {
        this.kakaraCore = kakaraCore;
    }
}
