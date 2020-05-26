package org.kakara.client.game.world;

import me.ryandw11.ods.tags.ListTag;
import me.ryandw11.ods.tags.ObjectTag;
import org.kakara.core.serializers.ods.ChunkLocationTag;
import org.kakara.core.serializers.ods.GameBlockTag;
import org.kakara.core.world.Chunk;
import org.kakara.core.world.GameBlock;

import java.util.ArrayList;
import java.util.List;

public class ChunkTag extends ObjectTag {

    public ChunkTag(Chunk chunk){
        super(chunk.getLocation().getX() + "-" + chunk.getLocation().getY() + "-" + chunk.getLocation().getZ());
        addTag(new ChunkLocationTag("location", chunk.getLocation()));
        List<GameBlockTag> gameBlocks = new ArrayList<>();
        chunk.getGameBlocks().forEach((block) -> {
            //Why do I need this?
            if(block != null)
                gameBlocks.add(new GameBlockTag(block));
        });
        addTag(new ListTag<>("blocks", gameBlocks));
    }

    public Chunk getChunk(){
        ChunkLocationTag loc = (ChunkLocationTag) getTag("location");
        ListTag<GameBlockTag> blocks = (ListTag<GameBlockTag>) getTag("blocks");
        List<GameBlock> gameBlocks = new ArrayList<>();
        blocks.getValue().forEach(block -> {
            gameBlocks.add(block.getGameBlock());
        });

        return new ClientChunk(loc.getChunkLocation(), gameBlocks);
    }
}
