package org.kakara.engine.renderobjects;

import java.util.ArrayList;
import java.util.List;

public class ChunkHandler {
    private List<RenderChunk> renderChunkList;

    public ChunkHandler(){
        renderChunkList = new ArrayList<>();
    }

    public void addChunk(RenderChunk chunk){
        renderChunkList.add(chunk);
    }

    public List<RenderChunk> getRenderChunkList(){
        return renderChunkList;
    }
}
