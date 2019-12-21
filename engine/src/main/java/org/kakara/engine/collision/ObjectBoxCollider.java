package org.kakara.engine.collision;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.utils.Time;

/**
 * Handles collision for primative types. For objects using the model loader
 * @see BoxCollider
 */
public class ObjectBoxCollider implements Collider {

    boolean useGravity;
    boolean isTrigger;
    float gravity;

    private boolean isInAir = false;
    private float timeInAir;

    private Vector3 lastPosition;
    private Vector3 deltaPosition;
    private GameItem item;
    private GameHandler handler;

    public ObjectBoxCollider(boolean useGravity, boolean isTrigger, float gravity){
        this.useGravity = useGravity;
        this.isTrigger = isTrigger;
        this.handler = GameHandler.getInstance();
        this.gravity = gravity;
    }

    public ObjectBoxCollider(boolean useGravity, boolean isTrigger){
        this(useGravity, isTrigger, 0.07f);
    }

    public ObjectBoxCollider(){
        this(false, false, 0.07f);
    }

    public boolean usesGravity(){
        return useGravity;
    }

    public Collider setUseGravity(boolean value){
        this.useGravity = value;
        return this;
    }

    @Override
    public Vector3 getRelativePoint1() {
        return new Vector3(0, 0, 0);
    }

    @Override
    public Vector3 getAbsolutePoint1() {
        return item.getPosition();
    }

    @Override
    public Vector3 getRelativePoint2() {
        return new Vector3(item.getScale(), item.getScale(), item.getScale());
    }

    @Override
    public Vector3 getAbsolutePoint2() {
        return item.getPosition().add(item.getScale(), item.getScale(), item.getScale());
    }

    public Collider setTrigger(boolean value){
        this.isTrigger = value;
        return this;
    }

    public float getGravity(){
        return gravity;
    }

    @Override
    public float getGravityVelocity() {
        if(timeInAir < 1f) return getGravity();
        return getGravity() * timeInAir;
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
        // If gravity is enabled move it by the gravitational velocity.
        if(useGravity){
            item.translateBy(0, -getGravityVelocity(), 0);
        }

        boolean found = false;
        // Handle collision for gravity.
        for(GameItem gi : cm.getCollidngItems()){
            // Prevent object from colliding with itself.
            if(gi == item) continue;
            // If the object is not colliding, then prevent further calculations.
            if(!cm.isColliding(gi, item)) continue;
            // Check to see if it is possible for the object to collide. If not stop calculations.
            if(KMath.distance(gi.getPosition(), item.getPosition()) > 20) continue;
            //The bottom collision point of this object.
            Vector3 point1 = KMath.distance(this.getAbsolutePoint1(), item.getPosition()) > KMath.distance(this.getAbsolutePoint2(), item.getPosition()) ? item.getCollider().getAbsolutePoint2() : item.getCollider().getAbsolutePoint1();
            // The top collision point of the colliding object.
            Vector3 point2 = KMath.distance(gi.getCollider().getAbsolutePoint1(), gi.getPosition()) < KMath.distance(gi.getCollider().getAbsolutePoint2(), gi.getPosition()) ? gi.getCollider().getAbsolutePoint2() : gi.getCollider().getAbsolutePoint1();

            // Negate x and z.
            point1.x = 0;
            point1.z = 0;
            point2.x = 0;
            point2.z = 0;
            if(KMath.distance(point1, point2) <= getGravityVelocity()){
                isInAir = false;
                found = true;
                // Undo last gravitational action.
                item.translateBy(0, getGravityVelocity(), 0);
            }
        }
        // If no collision actions are done then it is in the air.
        if(!found)
            isInAir = true;

        if(isInAir)
            timeInAir += Time.deltaTime;
        else
            timeInAir = 0;
    }

    @Override
    public void onRegister(GameItem item) {
        this.item = item;
        lastPosition = new Vector3(0, 0, 0);
        deltaPosition = new Vector3(0, 0, 0);
        handler.getCollisionManager().addCollidingItem(item);
    }
}
