package org.kakara.engine.objects;

import org.joml.Vector3f;
import org.kakara.engine.render.Mesh;

import java.util.UUID;

/**
 * The main game object.
 */
public class MeshObject implements GameObject {
    private final Mesh mesh;
    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;
    private UUID id;


    public MeshObject(Mesh mesh){
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
        id = UUID.randomUUID();
    }

    /**
     * Set the position of the MeshObject
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(float x, float y, float z){
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getPosition(){
        return position;
    }

    /**
     * Set the scale of the MeshObject
     * @param scale
     */
    public void setScale(float scale){
        this.scale = scale;
    }

    public float getScale(){
        return scale;
    }

    /**
     * Set the rotation of the MeshObject.
     * @param x
     * @param y
     * @param z
     */
    public void setRotation(float x, float y, float z){
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public Vector3f getRotation(){
        return rotation;
    }
    public Mesh getMesh(){
        return mesh;
    }

    @Override
    public void render() {
        getMesh().render();
    }

    @Override
    public void cleanup() {
        getMesh().cleanup();
    }

    @Override
    public UUID getId(){
        return id;
    }
}
