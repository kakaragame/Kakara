package org.kakara.game.client;

import org.kakara.core.world.Chunk;
import org.kakara.core.world.ChunkBase;
import org.kakara.core.world.GameBlock;
import org.kakara.core.world.Location;

import java.util.List;

public class ClientChunk implements Chunk {
    private Location location;
    private List<GameBlock> gameBlockList;

    public ClientChunk(ChunkBase base) {
        location = new Location(base.getX(), base.getY(), base.getZ());
        gameBlockList = base.getGameBlocks();
    }

    @Override
    public List<GameBlock> getGameBlocks() {
        return gameBlockList;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
