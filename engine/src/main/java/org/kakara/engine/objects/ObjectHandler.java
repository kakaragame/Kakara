package org.kakara.engine.objects;

import java.util.ArrayList;
import java.util.List;

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
}
