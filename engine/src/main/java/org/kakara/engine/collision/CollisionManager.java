package org.kakara.engine.collision;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager {

    private GameHandler handler;
    private List<GameItem> collidingItems = new ArrayList<>();

    public CollisionManager(GameHandler handler){
        this.handler = handler;
    }

    public void addCollidingItem(GameItem item){
        collidingItems.add(item);
    }

    public void removeCollidingItem(GameItem item){
        collidingItems.remove(item);
    }

    public List<GameItem> getCollidngItems(){
        return collidingItems;
    }

    /**
     * Detect collision between two items.
     * @param item1 The first game item.
     * @param item2 The second game item.
     * @return If they are colliding.
     */
    public boolean isColliding(GameItem item1, GameItem item2){
        boolean xCollision = item1.getPosition().x + item1.getScale() >= item2.getPosition().x && item2.getPosition().x + item2.getScale() >= item1.getPosition().x;
        boolean yCollision = item1.getPosition().y + item1.getScale() >= item2.getPosition().y && item2.getPosition().y + item2.getScale() >= item1.getPosition().y;
        boolean zCollision = item1.getPosition().z + item1.getScale() >= item2.getPosition().z && item2.getPosition().z + item2.getScale() >= item1.getPosition().z;
        return xCollision && yCollision && zCollision;
    }
}
