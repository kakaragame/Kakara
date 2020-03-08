package org.kakara.engine.item;

import java.util.*;

public class ItemHandler {
    private List<GameItem> items;

    private Map<Mesh, List<GameItem>> meshMap;

    public ItemHandler() {
        items = new ArrayList<>();
        meshMap = new HashMap<>();
    }

    public void addItem(GameItem obj) {
        Mesh mesh = obj.getMesh();
        List<GameItem> list = meshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
        list.add(obj);
    }

    /**
     * <p>Looping through this is extremely performance heavy!</p>
     * @return The list of gameitems
     */
//    public List<GameItem> getItemList() {
//        return this.items;
//    }

    public Map<Mesh, List<GameItem>> getMeshMap(){
        return meshMap;
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

    public List<GameItem> getItems() {
        return items;
    }

    /**
     * Cleanup all of the items.
     * <p>Internal Use Only.</p>
     */
    public void cleanup(){
        for(Mesh m : meshMap.keySet()){
            for(GameItem gi : meshMap.get(m)){
                gi.cleanup();
            }
        }
    }
}
