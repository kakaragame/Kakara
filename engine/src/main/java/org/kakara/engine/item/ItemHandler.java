package org.kakara.engine.item;

import java.util.*;

public class ItemHandler {
    private List<GameItem> items;

    private Map<Mesh, List<GameItem>> nonInstancedMeshMap;
    private Map<InstancedMesh, List<GameItem>> instancedMeshMap;

    public ItemHandler() {
        items = new ArrayList<>();
        nonInstancedMeshMap = new HashMap<>();
        instancedMeshMap = new HashMap<>();
    }

    public void addItem(GameItem obj) {
        Mesh mesh = obj.getMesh();
        if(mesh instanceof InstancedMesh){
            List<GameItem> list = instancedMeshMap.computeIfAbsent((InstancedMesh) mesh, k -> new ArrayList<>());
            list.add(obj);
        }else{
            List<GameItem> list = nonInstancedMeshMap.computeIfAbsent(mesh, k -> new ArrayList<>());
            list.add(obj);
        }
    }

    /**
     * <p>Looping through this is extremely performance heavy!</p>
     * @return The list of gameitems
     */
//    public List<GameItem> getItemList() {
//        return this.items;
//    }

    public Map<Mesh, List<GameItem>> getNonInstancedMeshMap(){
        return nonInstancedMeshMap;
    }

    public Map<InstancedMesh, List<GameItem>> getInstancedMeshMap(){
        return instancedMeshMap;
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
        for(Mesh m : instancedMeshMap.keySet()){
            for(GameItem gi : instancedMeshMap.get(m)){
                gi.cleanup();
            }
        }

        for(Mesh m : nonInstancedMeshMap.keySet()){
            for(GameItem gi : nonInstancedMeshMap.get(m)){
                gi.cleanup();
            }
        }
    }
}
