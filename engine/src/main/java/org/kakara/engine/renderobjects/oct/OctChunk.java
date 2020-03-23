package org.kakara.engine.renderobjects.oct;

import org.kakara.engine.math.Vector3;
import org.kakara.engine.renderobjects.RenderBlock;

import java.util.ArrayList;
import java.util.List;

public class OctChunk {
    private List<SubOctChunk> octChunks;
    public OctChunk(){
        octChunks = new ArrayList<>();
    }

    public void add(RenderBlock block){
        Vector3 position = block.getPosition();
        int index = (int) (Math.abs(Math.floor(position.x/2) + Math.floor(position.y/2) + Math.floor(position.z/2)));
        if(octChunks.size() > index){
            octChunks.get(index).add(block);
        }else{
            while(octChunks.size() < index){
                octChunks.add(new SubOctChunk());
            }
        }
    }

    public RenderBlock get(Vector3 vec){
        int index = (int) (Math.floor(Math.abs(vec.x)/2) + Math.floor(Math.abs(vec.y)/2) + Math.floor(Math.abs(vec.z)/2));
        if(octChunks.size() <= index) return null;
        return octChunks.get(index).get(vec);
    }
}
