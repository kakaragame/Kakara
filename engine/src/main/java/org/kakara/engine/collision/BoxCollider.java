package org.kakara.engine.collision;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.math.Vector3;

import static org.lwjgl.opengl.GL11.*;

public class BoxCollider implements Collider {

    boolean useGravity;
    boolean isTrigger;
    float gravity;

    private Vector3 point1;
    private Vector3 point2;
    private Vector3 offset;
    private boolean relative;

    private Vector3 lastPosition;
    private Vector3 deltaPosition;
    private GameItem item;
    private GameHandler handler;
    public BoxCollider(Vector3 point1, Vector3 point2, boolean useGravity, boolean isTrigger){
        this.useGravity = useGravity;
        this.isTrigger = isTrigger;
        this.handler = GameHandler.getInstance();
        gravity = 0.07f;
        this.point1 = point1;
        this.point2 = point2;
        this.relative = true;
        this.offset = new Vector3(0, 0, 0);
    }

    public BoxCollider(Vector3 point1, Vector3 point2, boolean relative){
        this.useGravity = false;
        this.isTrigger = false;
        this.handler = GameHandler.getInstance();
        gravity = 0.07f;
        this.point1 = point1;
        this.point2 = point2;
        this.relative = relative;
        this.offset = new Vector3(0, 0, 0);
    }

    public BoxCollider(Vector3 point1, Vector3 point2){
        this.useGravity = false;
        this.isTrigger = false;
        this.handler = GameHandler.getInstance();
        gravity = 0.07f;
        this.point1 = point1;
        this.point2 = point2;
        this.relative = true;
        this.offset = new Vector3(0, 0, 0);
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

    public boolean isRelative(){
        return relative;
    }

    public void setRelative(boolean relative){
        this.relative = relative;
    }

    public Vector3 getPoint1(){
        return point1;
    }

    public void setOffset(Vector3 offset){
        this.offset = offset;
    }

    public Vector3 getOffset(){
        return offset;
    }

    public Vector3 getRelativePoint1(){
        if(!relative)
            return point1.add(offset).subtract(item.getPosition());
        return point1.add(offset);
    }

    public Vector3 getAbsolutePoint1(){
        if(relative)
            return point1.add(offset).add(item.getPosition());
        return point1.add(offset);
    }

    public void setPoint1(Vector3 point1){
        this.point1 = point1;
    }

    public Vector3 getPoint2(){
        return point2;
    }

    public Vector3 getRelativePoint2(){
        if(!relative)
            return point2.add(offset).subtract(item.getPosition());
        return point2.add(offset);
    }

    public Vector3 getAbsolutePoint2(){
        if(relative)
            return point2.add(offset).add(item.getPosition());
        return point2.add(offset);
    }

    public void setPoint2(Vector3 point2){
        this.point2 = point2;
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
