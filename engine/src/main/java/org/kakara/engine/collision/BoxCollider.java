package org.kakara.engine.collision;

import org.kakara.engine.GameHandler;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.render.DebugRender;
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

public class BoxCollider implements Collider {

    boolean useGravity;
    boolean isTrigger;
    float gravity;

    private Vector3 point1;
    private Vector3 point2;
    private Vector3 offset;
    private boolean relative;

    private boolean debug;

    private boolean isInAir = false;

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
                if(deltaPosition.y > -gravity + KMath.FLOAT_MIN_ERROR && deltaPosition.y < -gravity + KMath.FLOAT_MAX_ERROR) deltaPosition.y = 0;
                Vector3 currentPosition = item.getPosition().subtract(deltaPosition);
                this.lastPosition = currentPosition;
                item.setPosition(currentPosition.x, currentPosition.y, currentPosition.z);
            }
        }
        if(useGravity){
            item.translateBy(0, -gravity, 0);
        }
        boolean found = false;
        for(GameItem gi : cm.getCollidngItems()){
            if(gi == item) continue;
            if(!cm.isColliding(gi, item)) continue;
            if(KMath.distance(gi.getPosition(), item.getPosition()) > 20) continue;
            Vector3 point1 = item.getCollider().getAbsolutePoint1().greaterThan(item.getCollider().getAbsolutePoint2()) ? item.getCollider().getAbsolutePoint2() : item.getCollider().getAbsolutePoint1();
            Vector3 point2 = !gi.getCollider().getAbsolutePoint1().greaterThan(gi.getCollider().getAbsolutePoint2()) ? gi.getCollider().getAbsolutePoint2() : gi.getCollider().getAbsolutePoint1();
            point1.x = 0;
            point1.z = 0;
            point2.x = 0;
            point2.z = 0;
            if(KMath.distance(point1, point2) <= gravity){
                isInAir = false;
                found = true;
                item.translateBy(0, gravity, 0);

            }
        }
        if(!found)
            isInAir = true;
    }

    @Override
    public void onRegister(GameItem item) {
        this.item = item;
        lastPosition = new Vector3(0, 0, 0);
        deltaPosition = new Vector3(0, 0, 0);
        handler.getCollisionManager().addCollidingItem(item);
    }

    public void setDebug(boolean value){
        this.debug = value;
    }

    public void render(){
        if(debug){
            int vaoId = glGenVertexArrays();
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

            glBindVertexArray(vaoId);
            glEnableVertexAttribArray(0);
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

            glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );

            glDisableVertexAttribArray(0);

        }
    }

}
