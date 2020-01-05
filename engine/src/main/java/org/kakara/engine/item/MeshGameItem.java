package org.kakara.engine.item;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.Collider;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.render.Shader;

import java.util.UUID;

/**
 * The most basic implementation of the GameItem.
 * <p>
 * This is a Collidable GameItem. That uses meshes to create an item
 */
public class MeshGameItem implements Collidable {

    private Mesh[] meshes;
    private float scale;
    private Quaternionf rotation;
    private Vector3 position;
    private final UUID uuid;
    private Collider collider;

    public MeshGameItem() {
        this(new Mesh[0]);
    }

    public MeshGameItem(Mesh mesh) {
        this(new Mesh[]{mesh});
    }

    public MeshGameItem(Mesh[] meshes) {
        this.meshes = meshes;
        position = new Vector3(0, 0, 0);
        scale = 1;
        rotation = new Quaternionf();
        uuid = UUID.randomUUID();
    }

    /**
     * Get the current position.
     *
     * @return The current position.
     */
    public Vector3 getPosition() {
        return position;
    }


    /**
     * Set the position of the game item.
     *
     * @param x x value
     * @param y y value
     * @param z z value
     * @return The instance of the game item.
     */
    public GameItem setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        return this;
    }

    /**
     * Set the position of the item
     *
     * @param position The position in vector form
     * @return The instance of the Game Item.
     */
    public GameItem setPosition(Vector3 position) {
        this.position = position;
        return this;
    }

    /**
     * Change the position of the game item by x, y, and z values.
     *
     * @param x Change in x
     * @param y Change in y
     * @param z Change in z
     * @return The instance of the game item.
     */
    public GameItem translateBy(float x, float y, float z) {
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
        return this;
    }

    /**
     * Change the position of the game item by a vector.
     *
     * @param position The vector to change by.
     * @return The instance of the game item.
     */
    public GameItem translateBy(Vector3 position) {
        this.position.x += position.x;
        this.position.y += position.y;
        this.position.z += position.z;
        return this;
    }

    /**
     * Get the scale of the item.
     *
     * @return The scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Set the scale of the Game Item
     *
     * @param scale The scale value
     * @return The instance of the game item.
     */
    public final GameItem setScale(float scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Get the ID of the game item.
     * <p>This method is the same as getUUID()</p>
     *
     * @return The ID.
     */
    public UUID getId() {
        return uuid;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    /**
     * Set the rotation of the Object
     *
     * @param q The quaternion
     * @return The instance of the game item.
     */
    public final GameItem setRotation(Quaternionf q) {
        this.rotation.set(q);
        return this;
    }

    /**
     * Change the rotation by the angle on the axis.
     *
     * @param angle The angle to change by. (In radians)
     * @param axis  The vector of the axis (without magnitude)
     * @return The instance of the game item.
     */
    public GameItem rotateAboutAxis(float angle, Vector3 axis) {
        this.rotation.rotateAxis(angle, axis.toJoml());
        return this;
    }

    /**
     * Set the rotation to the angle on the axis.
     *
     * @param angle The angle to set to. (In Radians).
     * @param axis  The vector of the axis (without magnitude)
     * @return The instance of the game item.
     */
    public GameItem setRotationAboutAxis(float angle, Vector3 axis) {
        this.rotation.rotationAxis(angle, axis.toJoml());
        return this;
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    /**
     * Set the collider for a game item
     *
     * @param collider The instance of the collider.
     * @return The instance of the game item.
     */
    public GameItem setCollider(Collider collider) {
        this.collider = collider;
        collider.onRegister(this);
        return this;
    }

    /**
     * Remove the currently active collider.
     */
    public void removeCollider() {
        this.collider = null;
        GameHandler.getInstance().getCollisionManager().removeCollidingItem(this);
    }

    /**
     * Get the currently active collider
     *
     * @return The collider. (Null if none applied)
     */
    public Collider getCollider() {
        return this.collider;
    }

    public void render() {
        for (Mesh mesh : meshes) {
            mesh.render();
        }
    }

    public void cleanup() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }


    /**
     * A safe way to clone a gameobject.
     *
     * @param exact If you want it to be an exact copy.
     * @return The clone of the gameobject.
     */
    public GameItem clone(boolean exact) {
        Collidable clone = new MeshGameItem(this.meshes);
        if (exact) {
            clone.setPosition(this.position.x, this.position.y, this.position.z);
            clone.setRotation(this.rotation);
            clone.setScale(this.scale);
        }
        return clone;
    }


}
