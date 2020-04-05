package org.kakara.engine.renderobjects;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.MeshGameItem;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.oct.OctChunk;
import org.kakara.engine.renderobjects.renderlayouts.Face;
import org.kakara.engine.scene.AbstractGameScene;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RenderChunk extends MeshGameItem {

    private List<RenderBlock> blocks;
    private RenderMesh mesh;
    private OctChunk octChunk;
    private UUID chunkId;

    public RenderChunk(List<RenderBlock> blocks, TextureAtlas atlas){
        super();
        this.setPosition(new Vector3(0, 0, 0));
        this.octChunk = new OctChunk();
        this.blocks = blocks;
        for(RenderBlock blck : blocks){
            blck.setParentChunk(this);
            octChunk.add(blck);
        }
        chunkId = UUID.randomUUID();
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

    /**
     * Get the ID of the chunk.
     * @return The id
     */
    public UUID getId(){
        return chunkId;
    }

    public OctChunk getOctChunk(){
        return octChunk;
    }

    public List<RenderBlock> calculateVisibleBlocks(List<RenderBlock> blocks){
        List<RenderBlock> output = new ArrayList<>();
        for(RenderBlock block : blocks){
            Vector3 pos = block.getPosition();
            block.clearFaces();
            boolean found = false;
            if(octChunk.get(new Vector3(pos.x, pos.y, pos.z + 1)) == null) {
                block.addFace(Face.FRONT);
                output.add(block);
                found = true;
            }
            if(octChunk.get(new Vector3(pos.x, pos.y, pos.z - 1)) == null) {
                block.addFace(Face.BACK);
                if(!found)
                    output.add(block);
            }
            if(octChunk.get(new Vector3(pos.x, pos.y + 1, pos.z)) == null) {
                block.addFace(Face.TOP);
                if(!found)
                    output.add(block);
                found = true;
            }
            if(octChunk.get(new Vector3(pos.x, pos.y -1, pos.z)) == null) {
                block.addFace(Face.BOTTOM);
                if(!found)
                    output.add(block);
                found = true;
            }
            if(octChunk.get(new Vector3(pos.x + 1, pos.y, pos.z)) == null) {
                block.addFace(Face.RIGHT);
                if(!found)
                    output.add(block);
                found = true;
            }
            if(octChunk.get(new Vector3(pos.x - 1, pos.y, pos.z)) == null) {
                block.addFace(Face.LEFT);
                if(!found)
                    output.add(block);
            }
        }
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
