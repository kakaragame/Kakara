package org.kakara.engine.collision;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.render.DebugRender;
import org.kakara.engine.utils.Time;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * The BoxCollider class is to be used on non-primitive objects that are created using the model loader.
 * It is recommended to use the relative mode to define points.
 * To define the points pick two opposite corners.
 */
public class BoxCollider implements Collider {

    private boolean useGravity;
    private boolean isTrigger;
    private float gravity;

    private Vector3 point1;
    private Vector3 point2;
    private Vector3 offset;
    private boolean relative;

    private boolean isInAir = false;
    private float timeInAir;

    private Vector3 lastPosition;
    private Vector3 deltaPosition;
    private GameItem item;
    private GameHandler handler;

    public BoxCollider(Vector3 point1, Vector3 point2, boolean useGravity, boolean isTrigger, boolean relative){
        this.useGravity = useGravity;
        this.isTrigger = isTrigger;
        this.handler = GameHandler.getInstance();
        gravity = 0.07f;
        this.point1 = point1;
        this.point2 = point2;
        this.relative = true;
        this.offset = new Vector3(0, 0, 0);
        timeInAir = 0;
    }
    public BoxCollider(Vector3 point1, Vector3 point2, boolean relative){
        this(point1, point2,false, false, relative);
    }
    public BoxCollider(Vector3 point1, Vector3 point2){
        this(point1, point2, false, false, true);
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

    public float getGravityVelocity(){
        if(timeInAir < 1f) return getGravity();
        return getGravity() * timeInAir;
    }

    public void setGravity(float gravity){
        this.gravity = gravity;
    }

    public boolean isTrigger(){
        return isTrigger;
    }

    /**
     * If the collider is in realtive mode.
     * @return
     */
    public boolean isRelative(){
        return relative;
    }

    /**
     * Set if the points provided are relative or absolute.
     * @param relative If the points provided are relative.
     */
    public void setRelative(boolean relative){
        this.relative = relative;
    }


    /**
     * Set the offset of the collider.
     * <p>Relative points will relate to (0,0,0) minus this vector.</p>
     * @param offset
     */
    public void setOffset(Vector3 offset){
        this.offset = offset;
    }

    /**
     * Get the offset for this collider.
     * @return The offset.
     */
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

    /**
     * Set point 1.
     * @param point1 The vector for the point.
     */
    public void setPoint1(Vector3 point1){
        this.point1 = point1;
    }

    /**
     * Get point 1 no matter if it is relative or absolute.
     * @return the vector.
     */
    public Vector3 getPoint1(){
        return point1;
    }

    /**
     * Get the second point no matter if it is relative or absolute.
     * @return the vector.
     */
    public Vector3 getPoint2(){
        return point2;
    }

    /**
     * Set the second point.
     * @param point2 The vector.
     */
    public void setPoint2(Vector3 point2){
        this.point2 = point2;
    }

    @Override
    public void update() {
        if(isTrigger) return;
        //Calculate delta position.
        this.deltaPosition = item.getPosition().subtract(this.lastPosition);
        this.lastPosition = item.getPosition();

        CollisionManager cm = handler.getCollisionManager();
        //Loop through the colliding item list.
        for(GameItem gi : cm.getCollidngItems()){
            // Prevent collision with itself.
            if(gi == item) continue;
            // If the objects are colliding. (Gravity will never cause this).
            if(cm.isColliding(gi, item)){
                // Remove gravity forces from vector rollback.
                if(deltaPosition.y > -gravity + KMath.FLOAT_MIN_ERROR && deltaPosition.y < -gravity + KMath.FLOAT_MAX_ERROR) deltaPosition.y = 0;
                Vector3 currentPosition = item.getPosition().subtract(deltaPosition);
                this.lastPosition = currentPosition;
                // Set the calculated vector rollback.
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

        enableDebugRender();
    }


    /*
     *
     * Bellow is the code for the debug mode.
     *
     */

    private int vaoId;
    private int indicesLength;
    private boolean debug;


    /**
     * Enable the experimental visual debug mode. (Note: this functionality is broken and is not currently functioning as normal).
     * @param value
     */
    public void setDebug(boolean value){
        this.debug = value;
    }

    /**
     * Generates the VBO and VAO data.
     */
    public void enableDebugRender(){
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        float[] position = DebugRender.getPositions(this.getRelativePoint1().add(item.getPosition()), this.getRelativePoint2().add(item.getPosition()));
        int[] indices = DebugRender.getIndices();

        FloatBuffer posBuffer;
        IntBuffer indicesBuffer;

        int vboId = glGenBuffers();
        posBuffer = MemoryUtil.memAllocFloat(position.length);
        posBuffer.put(position).flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        vboId = glGenBuffers();
        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        MemoryUtil.memFree(posBuffer);
        MemoryUtil.memFree(indicesBuffer);

        indicesLength = indices.length;
    }

    /**
     * Renders the debug visual.
     */
    public void render(){
        if(debug){

            glBindVertexArray(vaoId);
            glEnableVertexAttribArray(0);
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

            glDrawElements(GL_TRIANGLES, this.indicesLength, GL_UNSIGNED_INT, 0);

            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );

            glDisableVertexAttribArray(0);

        }
    }

}
