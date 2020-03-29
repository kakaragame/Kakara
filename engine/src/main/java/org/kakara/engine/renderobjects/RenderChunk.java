package org.kakara.engine.renderobjects;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.MeshGameItem;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.oct.OctChunk;
import org.kakara.engine.scene.AbstractGameScene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RenderChunk extends MeshGameItem {

    private List<RenderBlock> blocks;
    private RenderMesh mesh;
    private OctChunk octChunk;

    public RenderChunk(List<RenderBlock> blocks, TextureAtlas atlas){
        super();
        this.setPosition(new Vector3(0, 0, 0));
        this.blocks = blocks;
        this.octChunk = new OctChunk();
        regenerateChunk(atlas);
    }

    public List<RenderBlock> getBlocks(){
        return blocks;
    }

    public void addBlock(RenderBlock block){
        block.setParentChunk(this);
        blocks.add(block);
        octChunk.add(block);
    }

    public void removeBlock(RenderBlock block){
        blocks.remove(block);
        block.setParentChunk(null);
    }

    public List<RenderBlock> calculateVisibleBlocks(List<RenderBlock> blocks){
        final long startTime = System.currentTimeMillis();
        List<RenderBlock> output = new ArrayList<>();
        System.out.println("Chunk : " + octChunk.get(new Vector3(1, 1, 1)));
        for(RenderBlock block : blocks){
            Vector3 pos = block.getPosition();
            if(octChunk.get(pos.clone().add(1, 0, 0)) == null) {
                output.add(block);
                continue;
            }
            if(octChunk.get(pos.clone().add(-1, 0, 0)) == null) {
                output.add(block);
                continue;
            }
            if(octChunk.get(pos.clone().add(0, 1, 0)) == null) {
                output.add(block);
                continue;
            }
            if(octChunk.get(pos.clone().add(0, -1, 0)) == null) {
                output.add(block);
                continue;
            }
            if(octChunk.get(pos.clone().add(0, 0, 1)) == null) {
                output.add(block);
                continue;
            }
            if(octChunk.get(pos.clone().add(0, 0, -1)) == null) {
                output.add(block);
            }
        }
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
        return output;
    }

    public void regenerateChunk(TextureAtlas atlas){
        if(mesh != null){
            mesh.cleanUp();
        }
        List<RenderBlock> visBlocks = calculateVisibleBlocks(blocks);
        //TODO FIX THIS MESS
        this.mesh = new RenderMesh(visBlocks, atlas);
    }

    @Override
    public void render(){
        mesh.render();
    }

}
