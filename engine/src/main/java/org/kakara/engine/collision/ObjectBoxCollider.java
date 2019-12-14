package org.kakara.engine.collision;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.math.Vector3;

public class ObjectBoxCollider implements Collider {

    boolean useGravity;
    boolean isTrigger;
    float gravity;

    private Vector3 lastPosition;
    private Vector3 deltaPosition;
    private GameItem item;
    private GameHandler handler;
    public ObjectBoxCollider(boolean useGravity, boolean isTrigger){
        this.useGravity = useGravity;
        this.isTrigger = isTrigger;
        this.handler = GameHandler.getInstance();
        gravity = 0.07f;
    }

    public ObjectBoxCollider(){
        this.useGravity = false;
        this.isTrigger = false;
        this.handler = GameHandler.getInstance();
        gravity = 0.07f;
    }

    public boolean usesGravity(){
        return useGravity;
    }

    public Collider setUseGravity(boolean value){
        this.useGravity = value;
        return this;
    }

    public Collider setTrigger(boolean value){
        this.isTrigger = value;
        return this;
    }

    public float getGravity(){
        return gravity;
    }

    public void setGravity(float gravity){
        this.gravity = gravity;
    }

    public boolean isTrigger(){
        return isTrigger;
    }

    @Override
    public void update() {
        if(isTrigger) return;
        this.deltaPosition = item.getPosition().subtract(this.lastPosition);
        this.lastPosition = item.getPosition();

        CollisionManager cm = handler.getCollisionManager();
        for(GameItem gi : cm.getCollidngItems()){
            if(gi == item) continue;
            if(cm.isColliding(gi, item)){
                Vector3 currentPosition = item.getPosition().subtract(deltaPosition);
                this.lastPosition = currentPosition;
                item.setPosition(currentPosition.x, currentPosition.y, currentPosition.z);

            }
        }

        if(useGravity){
            item.translateBy(0, -gravity, 0);
        }
    }

    @Override
    public void onRegister(GameItem item) {
        this.item = item;
        lastPosition = new Vector3(0, 0, 0);
        deltaPosition = new Vector3(0, 0, 0);
        handler.getCollisionManager().addCollidingItem(item);
    }
}
