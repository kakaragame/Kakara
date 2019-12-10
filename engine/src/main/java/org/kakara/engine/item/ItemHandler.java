package org.kakara.engine.item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemHandler {
    private List<GameItem> objects;

    public ItemHandler(){
        objects = new ArrayList<>();
    }

    public void addObject(GameItem obj){
        this.objects.add(obj);
    }

    public List<GameItem> getObjectList(){
        return this.objects;
    }

    /**
     * Get gameobjects with a certain id.
     * @param id The id
     * @return Returns the gameobject. (Returns null if none found).
     */
    public GameItem getObjectWithId(UUID id){
        for(GameItem obj : objects){
            if(obj.getId() == id) return obj;
        }
        return null;
    }
}
