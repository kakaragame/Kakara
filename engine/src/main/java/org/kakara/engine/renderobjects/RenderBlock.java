package org.kakara.engine.renderobjects;

import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.renderlayouts.BlockLayout;
import org.kakara.engine.renderobjects.renderlayouts.Face;
import org.kakara.engine.renderobjects.renderlayouts.Layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The individual blocks of the chunk.
 */
public class RenderBlock {

    private Layout layout;
    private RenderTexture texture;

    private Vector3 position;

    private RenderChunk parentChunk;
    private Vector3 relativePosition;
    private List<Face> visibleFaces;

    public RenderBlock(Layout layout, RenderTexture texture, Vector3 position){
        this.layout = layout;
        this.texture = texture;
        this.position = position;
        this.visibleFaces = new ArrayList<>();
    }

    public RenderBlock(RenderTexture texture, Vector3 position){
        this(new BlockLayout(), texture, position);
    }

    public void setPosition(Vector3 position){
        this.position = position.clone();
    }

    public List<Face> getVisibleFaces(){
        return this.visibleFaces;
    }

    public Vector3 getPosition(){
        return position.clone();
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

    public void addFace(Face f){
        visibleFaces.add(f);
    }

    public void clearFaces(){
        visibleFaces.clear();
    }

    protected float[] getVertexFromFaces(){
        float[] vertex = {};
        for(Face f : this.visibleFaces){
            float[] temp;
            switch(f){
                case FRONT:
                    temp = layout.getVertex(getPosition()).getFront();
                    break;
                case BACK:
                    temp = layout.getVertex(getPosition()).getBack();
                    break;
                case TOP:
                    temp = layout.getVertex(getPosition()).getTop();
                    break;
                case BOTTOM:
                    temp = layout.getVertex(getPosition()).getBottom();
                    break;
                case LEFT:
                    temp = layout.getVertex(getPosition()).getLeft();
                    break;
                case RIGHT:
                    temp = layout.getVertex(getPosition()).getRight();
                    break;
                default:
                    temp = new float[0];
                    break;
            }
            float[] both = Arrays.copyOf(vertex, vertex.length + temp.length);
            System.arraycopy(temp, 0, both, vertex.length, temp.length);
            vertex = both;
        }
        return vertex;
    }

    protected float[] getTextureFromFaces(TextureAtlas atlas){
        float[] vertex = {};
        for(Face f : this.visibleFaces){
            float[] temp;
            switch(f){
                case FRONT:
                    temp = layout.getTextureCords().getFront(getTexture().getXOffset(), getTexture().getYOffset(), atlas.getNumberOfRows());
                    break;
                case BACK:
                    temp = layout.getTextureCords().getBack(getTexture().getXOffset(), getTexture().getYOffset(), atlas.getNumberOfRows());
                    break;
                case TOP:
                    temp = layout.getTextureCords().getTop(getTexture().getXOffset(), getTexture().getYOffset(), atlas.getNumberOfRows());
                    break;
                case BOTTOM:
                    temp = layout.getTextureCords().getBottom(getTexture().getXOffset(), getTexture().getYOffset(), atlas.getNumberOfRows());
                    break;
                case LEFT:
                    temp = layout.getTextureCords().getLeft(getTexture().getXOffset(), getTexture().getYOffset(), atlas.getNumberOfRows());
                    break;
                case RIGHT:
                    temp = layout.getTextureCords().getRight(getTexture().getXOffset(), getTexture().getYOffset(), atlas.getNumberOfRows());
                    break;
                default:
                    temp = new float[0];
                    break;
            }
            float[] both = Arrays.copyOf(vertex, vertex.length + temp.length);
            System.arraycopy(temp, 0, both, vertex.length, temp.length);
            vertex = both;
        }
        return vertex;
    }

    protected float[] getNormalsFromFaces(){
        float[] vertex = {};
        for(Face f : this.visibleFaces){
            float[] temp;
            switch(f){
                case FRONT:
                    temp = layout.getNormal().getFront();
                    break;
                case BACK:
                    temp = layout.getNormal().getBack();
                    break;
                case TOP:
                    temp = layout.getNormal().getTop();
                    break;
                case BOTTOM:
                    temp = layout.getNormal().getBottom();
                    break;
                case LEFT:
                    temp = layout.getNormal().getLeft();
                    break;
                case RIGHT:
                    temp = layout.getNormal().getRight();
                    break;
                default:
                    temp = new float[0];
                    break;
            }
            float[] both = Arrays.copyOf(vertex, vertex.length + temp.length);
            System.arraycopy(temp, 0, both, vertex.length, temp.length);
            vertex = both;
        }
        return vertex;
    }

    protected int[] getIndicesFromFaces(int currentIndex){
        int[] vertex = {};
        int index = currentIndex;
        for(Face f : this.visibleFaces){
            int[] temp;
            switch(f){
                case FRONT:
                    temp = layout.getIndices().getFront(index);
                    break;
                case BACK:
                    temp = layout.getIndices().getBack(index);
                    break;
                case TOP:
                    temp = layout.getIndices().getTop(index);
                    break;
                case BOTTOM:
                    temp = layout.getIndices().getBottom(index);
                    break;
                case LEFT:
                    temp = layout.getIndices().getLeft(index);
                    break;
                case RIGHT:
                    temp = layout.getIndices().getRight(index);
                    break;
                default:
                    temp = new int[0];
                    break;
            }
            //Increase the current index by five.
            index += 4;
            int[] both = Arrays.copyOf(vertex, vertex.length + temp.length);
            System.arraycopy(temp, 0, both, vertex.length, temp.length);
            vertex = both;
        }
        return vertex;
    }

    @Override
    public String toString(){
        return "{RenderBlock: " + position.x + ", " + position.y + ", " + position.z + "}";
    }


}
