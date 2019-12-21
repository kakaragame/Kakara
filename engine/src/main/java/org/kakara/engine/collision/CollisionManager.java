package org.kakara.engine.collision;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to handle colliding objects.
 * (This class prevents the calculation of collision for non-colliding game items.)
 */
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
     * @param item The first game item.
     * @param other The second game item.
     * @return If they are colliding.
     */
    public boolean isColliding(GameItem item, GameItem other){
        boolean xCollision;
        boolean yCollision;
        boolean zCollision;
        if(item.getCollider() instanceof BoxCollider && other.getCollider() instanceof ObjectBoxCollider){
            xCollision = (item.getPosition().x + ((BoxCollider) item.getCollider()).getRelativePoint1().x) + (((BoxCollider) item.getCollider()).getRelativePoint2().x) >= other.getPosition().x && other.getPosition().x + other.getScale()
                    >= item.getPosition().x + ((BoxCollider) item.getCollider()).getRelativePoint1().x;
            yCollision = (item.getPosition().y + ((BoxCollider) item.getCollider()).getRelativePoint1().y) + (((BoxCollider) item.getCollider()).getRelativePoint2().y) >= other.getPosition().y && other.getPosition().y + other.getScale()
                    >= item.getPosition().y + ((BoxCollider) item.getCollider()).getRelativePoint1().y;
            zCollision = (item.getPosition().z + ((BoxCollider) item.getCollider()).getRelativePoint1().z) + (((BoxCollider) item.getCollider()).getRelativePoint2().z) >= other.getPosition().z && other.getPosition().z + other.getScale()
                    >= item.getPosition().z + ((BoxCollider) item.getCollider()).getRelativePoint1().z;
        }
        else if(item.getCollider() instanceof BoxCollider && other.getCollider() instanceof BoxCollider){
            xCollision = (item.getPosition().x + ((BoxCollider) item.getCollider()).getRelativePoint1().x) + (((BoxCollider) item.getCollider()).getRelativePoint2().x) >= (other.getPosition().x + ((BoxCollider) other.getCollider()).getRelativePoint1().x)
                    && (other.getPosition().x + ((BoxCollider) other.getCollider()).getRelativePoint1().x) + (((BoxCollider) other.getCollider()).getRelativePoint2().x)
                    >= item.getPosition().x + ((BoxCollider) item.getCollider()).getRelativePoint1().x;
            yCollision = (item.getPosition().y + ((BoxCollider) item.getCollider()).getRelativePoint1().y) + (((BoxCollider) item.getCollider()).getRelativePoint2().y) >= (other.getPosition().y + ((BoxCollider) other.getCollider()).getRelativePoint1().y)
                    && (other.getPosition().y + ((BoxCollider) other.getCollider()).getRelativePoint1().y) + ( ((BoxCollider) other.getCollider()).getRelativePoint2().y)
                    >= item.getPosition().y + ((BoxCollider) item.getCollider()).getRelativePoint1().y;
            zCollision = (item.getPosition().z + ((BoxCollider) item.getCollider()).getRelativePoint1().z) + (((BoxCollider) item.getCollider()).getRelativePoint2().z) >= (other.getPosition().z + ((BoxCollider) other.getCollider()).getRelativePoint1().z)
                    && (other.getPosition().z + ((BoxCollider) other.getCollider()).getRelativePoint1().z) + (((BoxCollider) other.getCollider()).getRelativePoint2().z)
                    >= item.getPosition().z + ((BoxCollider) item.getCollider()).getRelativePoint1().z;
        }
        else if(item.getCollider() instanceof  ObjectBoxCollider && other.getCollider() instanceof ObjectBoxCollider){
            xCollision = item.getPosition().x + item.getScale() >= other.getPosition().x && other.getPosition().x + other.getScale() >= item.getPosition().x;
            yCollision = item.getPosition().y + item.getScale() >= other.getPosition().y && other.getPosition().y + other.getScale() >= item.getPosition().y;
            zCollision = item.getPosition().z + item.getScale() >= other.getPosition().z && other.getPosition().z + other.getScale() >= item.getPosition().z;
        }
        else if(item.getCollider() instanceof ObjectBoxCollider && other.getCollider() instanceof BoxCollider){
            GameItem itemCopy = other;
            GameItem otherCopy = item;
            xCollision = (itemCopy.getPosition().x + ((BoxCollider) itemCopy.getCollider()).getRelativePoint1().x) + (((BoxCollider) itemCopy.getCollider()).getRelativePoint2().x) >= otherCopy.getPosition().x && otherCopy.getPosition().x + otherCopy.getScale()
                    >= itemCopy.getPosition().x + ((BoxCollider) itemCopy.getCollider()).getRelativePoint1().x;
            yCollision = (itemCopy.getPosition().y + ((BoxCollider) itemCopy.getCollider()).getRelativePoint1().y) + (((BoxCollider) itemCopy.getCollider()).getRelativePoint2().y) >= otherCopy.getPosition().y && otherCopy.getPosition().y + otherCopy.getScale()
                    >= itemCopy.getPosition().y + ((BoxCollider) itemCopy.getCollider()).getRelativePoint1().y;
            zCollision = (itemCopy.getPosition().z + ((BoxCollider) itemCopy.getCollider()).getRelativePoint1().z) + (((BoxCollider) itemCopy.getCollider()).getRelativePoint2().z) >= otherCopy.getPosition().z && otherCopy.getPosition().z + otherCopy.getScale()
                    >= itemCopy.getPosition().z + ((BoxCollider) itemCopy.getCollider()).getRelativePoint1().z;
        }
        else{
            return false;
        }
        return xCollision && yCollision && zCollision;
    }
}
