package org.kakara.engine.renderobjects;

import org.kakara.engine.collision.Collidable;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkHandler {
    private List<RenderChunk> renderChunkList;

    public ChunkHandler(){
        renderChunkList = new ArrayList<>();
    }

    public void addChunk(RenderChunk chunk){
        renderChunkList.add(chunk);
    }

    /**
     * Remove a render chunk using its chunk id.
     * @param chunkId The id of the chunk. (chunk.getId()).
     */
    public void removeChunk(UUID chunkId){
        int ind= -1;
        for(RenderChunk rc : renderChunkList){
            if(rc.getId() == chunkId){
                ind = renderChunkList.indexOf(rc);
                break;
            }
        }
        if(ind != -1){
            renderChunkList.remove(ind);
        }
    }

    /**
     * Get a list of the chunk collisions.
     * <p>For performance reasons only the blocks around the position provided are returned.</p>
     * @param position The position where the collider is to check around.
     * @return The list of colliders.
     */
    public List<Collidable> getChunkCollisions(Vector3 position){
        Vector3 pos = new Vector3((int)Math.floor(position.x), (int)Math.floor(position.y), (int)Math.floor(position.z));
        List<Collidable> collisionList = new ArrayList<>();
        List<RenderChunk> renderChunks = new ArrayList<>(renderChunkList);
        for(RenderChunk chunk : renderChunks){
            if(KMath.distance(new Vector3(0, chunk.getPosition().y, 0), new Vector3(0, pos.y, 0)) > 16) continue;
            if(KMath.distance(new Vector3(chunk.getPosition().x, 0, chunk.getPosition().z), new Vector3(pos.x, 0, pos.z)) < 17){
                for(int x = -1; x < 2; x++){
                    for(int y=-2; y < 3; y++){
                        for(int z = -1; z < 2; z++){
                            Vector3 coords = coordsToRenderCoords(chunk.getPosition(), pos.clone().add(x, y, z));
                            RenderBlock blck = chunk.getOctChunk().get((int) coords.x, (int) coords.y, (int) coords.z);
                            if(blck != null)
                                collisionList.add(blck);
                        }
                    }
                }
            }
        }
        return collisionList;
    }

    public List<Collidable> getChunkSelections(Vector3 position){
        Vector3 pos = new Vector3((int)Math.floor(position.x), (int)Math.floor(position.y), (int)Math.floor(position.z));
        List<Collidable> collisionList = new ArrayList<>();
        for(RenderChunk chunk : renderChunkList){
            if(KMath.distance(new Vector3(0, chunk.getPosition().y, 0), new Vector3(0, pos.y, 0)) > 16) continue;
            if(KMath.distance(new Vector3(chunk.getPosition().x, 0, chunk.getPosition().z), new Vector3(pos.x, 0, pos.z)) < 25){
                for(int x = -10; x < 10; x++){
                    for(int y= -10; y < 10; y++){
                        for(int z = -10; z < 10; z++){
                            Vector3 coords = coordsToRenderCoords(chunk.getPosition(), pos.clone().add(x, y, z));
                            RenderBlock blck = chunk.getOctChunk().get((int) coords.x, (int) coords.y, (int) coords.z);
                            if(blck != null)
                                collisionList.add(blck);
                        }
                    }
                }
            }
        }
        return collisionList;
    }

    public List<RenderChunk> getRenderChunkList(){
        return new ArrayList<>(renderChunkList);
    }

    public Vector3 coordsToRenderCoords(Vector3 chunkpos, Vector3 input){
        return input.clone().subtract(chunkpos);
    }
}
