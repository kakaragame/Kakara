package org.kakara.engine.item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemHandler {
    private List<GameItem> items;

    public ItemHandler(){
        items = new ArrayList<>();
    }

    public void addItem(GameItem obj){
        this.items.add(obj);
    }

    public List<GameItem> getItemList(){
        return this.items;
    }

    /**
     * Get gameobjects with a certain id.
     * @param id The id
     * @return Returns the gameobject. (Returns null if none found).
     */
    public GameItem getItemWithId(UUID id){
        for(GameItem obj : items){
            if(obj.getId() == id) return obj;
        }
        return null;
    }
}
