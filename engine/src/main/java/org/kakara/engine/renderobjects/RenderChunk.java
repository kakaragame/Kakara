package org.kakara.engine.renderobjects;

import org.kakara.engine.item.MeshGameItem;

import java.util.List;

public class RenderChunk extends MeshGameItem {

    private List<RenderBlock> blocks;
    private RenderMesh mesh;

    public RenderChunk(List<RenderBlock> blocks){
        this.blocks = blocks;
    }

    public List<RenderBlock> getBlocks(){
        return blocks;
    }

    public void addBlock(RenderBlock block){
        block.setParentChunk(this);
        blocks.add(block);
    }

    public void removeBlock(RenderBlock block){
        blocks.remove(block);
        block.setParentChunk(null);
    }

    public void regenerateChunk(){

    }

    public void render(){

    }

}
