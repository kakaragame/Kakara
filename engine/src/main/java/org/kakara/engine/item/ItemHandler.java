package org.kakara.engine.item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemHandler {
    private List<GameItem> items;

    public ItemHandler() {
        items = new ArrayList<>();
    }

    public void addItem(GameItem obj) {
        this.items.add(obj);
    }

    /**
     * <p>Looping through this is extremely performance heavy!</p>
     * @return The list of gameitems
     */
    public List<GameItem> getItemList() {
        return this.items;
    }

    /**
     * Get gameobjects with a certain id.
     *
     * @param id The id
     * @return Returns the gameobject. (Returns null if none found).
     */
    public GameItem getItemWithId(UUID id) {
        for (GameItem obj : items) {
            if (obj.getId() == id) return obj;
        }
        return null;
    }

    /**
     * Cleanup all of the items.
     * <p>Internal Use Only.</p>
     */
    public void cleanup(){
        for(GameItem gi : items){
            gi.cleanup();
        }
    }
}