package org.kakara.engine.renderobjects;

import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.renderobjects.renderlayouts.Layout;

/**
 * The individual blocks of the chunk.
 */
public class RenderBlock {

    private Layout layout;
    private RenderTexture texture;

    private Vector3 position;

    private RenderChunk parentChunk;
    private Vector3 relativePosition;

    public RenderBlock(Layout layout, RenderTexture texture, Vector3 position){
        this.layout = layout;
        this.texture = texture;
        this.position = position;
    }

    public RenderBlock(RenderTexture texture, Vector3 position){
        this(new BlockLayout(), texture, position);
    }

    public void setPosition(Vector3 position){
        this.position = position;
    }

    public Vector3 getPosition(){
        return position;
    }

    public RenderChunk getParentChunk(){
        return this.parentChunk;
    }

    protected void setParentChunk(RenderChunk chunk){
        this.parentChunk = chunk;
    }

    public RenderTexture getTexture(){
        return texture;
    }

    public Layout getLayout(){
        return layout;
    }


}
