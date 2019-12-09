package org.kakara.engine.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ObjectHandler {
    private List<GameObject> objects;

    public ObjectHandler(){
        objects = new ArrayList<>();
    }

    public void addObject(GameObject obj){
        this.objects.add(obj);
    }

    public List<GameObject> getObjectList(){
        return this.objects;
    }

    /**
     * Get gameobjects with a certain id.
     * @param id The id
     * @return Returns the gameobject. (Returns null if none found).
     */
    public GameObject getObjectWithId(UUID id){
        for(GameObject obj : objects){
            if(obj.getId() == id) return obj;
        }
        return null;
    }
}
