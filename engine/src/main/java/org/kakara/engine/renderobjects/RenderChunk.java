package org.kakara.engine.renderobjects;

import me.ryandw11.octree.Octree;
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
    private Octree<RenderBlock> octChunk;
    private UUID chunkId;

    public RenderChunk(List<RenderBlock> blocks, TextureAtlas atlas){
        super();
        this.setPosition(new Vector3(0, 0, 0));
        this.octChunk = new Octree<>(0,0,0,17,17,17);
        this.blocks = blocks;
        for(RenderBlock blck : blocks){
            blck.setParentChunk(this);
            octChunk.insert((int)blck.getPosition().x, (int)blck.getPosition().y, (int)blck.getPosition().z, blck);
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
        octChunk.insert(Math.round(block.getPosition().x), Math.round(block.getPosition().y), Math.round(block.getPosition().z), block);
    }

    public void removeBlock(RenderBlock block){
        blocks.remove(block);
        octChunk.remove((int)block.getPosition().x, (int)block.getPosition().y, (int)block.getPosition().z);
        block.setParentChunk(null);
    }

    /**
     * Get the ID of the chunk.
     * @return The id
     */
    public UUID getId(){
        return chunkId;
    }

    public Octree<RenderBlock> getOctChunk(){
        return octChunk;
    }

    public List<RenderBlock> calculateVisibleBlocks(List<RenderBlock> blocks){
        List<RenderBlock> output = new ArrayList<>();
        for(RenderBlock block : blocks){
            Vector3 pos = block.getPosition();
            int x = (int) pos.x;
            int y = (int) pos.y;
            int z = (int) pos.z;
            block.clearFaces();
            boolean found = false;
            if(!octChunk.find(x, y, z + 1)) {
                block.addFace(Face.FRONT);
                output.add(block);
                found = true;
            }
            if(!octChunk.find(x, y, z - 1)) {
                block.addFace(Face.BACK);
                if(!found)
                    output.add(block);
            }
            if(!octChunk.find(x, y + 1, z)) {
                block.addFace(Face.TOP);
                if(!found)
                    output.add(block);
                found = true;
            }
            if(!octChunk.find(x, y -1, z)) {
                block.addFace(Face.BOTTOM);
                if(!found)
                    output.add(block);
                found = true;
            }
            if(!octChunk.find(x + 1, y, z)) {
                block.addFace(Face.RIGHT);
                if(!found)
                    output.add(block);
                found = true;
            }
            if(!octChunk.find(x - 1, y, z)) {
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
