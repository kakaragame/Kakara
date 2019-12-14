package org.kakara.engine;

import org.joml.Vector3f;
import org.kakara.engine.math.Vector3;

public class Camera {
    private final Vector3 position;
    private final Vector3 rotation;

    public Camera(){
        position = new Vector3(0, 0, 0);
        rotation = new Vector3(0, 0, 0);
    }

    public Camera(Vector3 position, Vector3 rotation){
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3 getPosition(){
        return position.clone();
    }

    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setPosition(Vector3 position){
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public void movePosition(Vector3 offset) {
        if ( offset.z != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offset.z;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offset.z;
        }
        if ( offset.x != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offset.x;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offset.x;
        }
        position.y += offset.y;
    }

    public void setRotation(float x, float y, float z){
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void setRotation(Vector3 rotation){
        this.rotation.x = rotation.x;
        this.rotation.y = rotation.y;
        this.rotation.z = rotation.z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ){
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

    public void moveRotation(Vector3 offset){
        rotation.x += offset.x;
        rotation.y += offset.y;
        rotation.z += offset.z;
    }

    public Vector3 getRotation(){
        return rotation.clone();
    }
}
