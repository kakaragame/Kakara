package org.kakara.engine.renderobjects;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.MeshGameItem;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.scene.AbstractGameScene;

import java.util.List;

public class RenderChunk extends MeshGameItem {

    private List<RenderBlock> blocks;
    private RenderMesh mesh;

    public RenderChunk(List<RenderBlock> blocks, TextureAtlas atlas){
        super();
        this.setPosition(new Vector3(0, 0, 0));
        this.blocks = blocks;
        regenerateChunk(atlas);
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

    public void regenerateChunk(TextureAtlas atlas){
        if(mesh != null){
            mesh.cleanUp();
        }
        //TODO FIX THIS MESS
        this.mesh = new RenderMesh(blocks, atlas);
    }

    @Override
    public void render(){
        mesh.render();
    }

}
