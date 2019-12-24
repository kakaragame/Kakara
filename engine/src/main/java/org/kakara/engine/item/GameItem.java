package org.kakara.engine.item;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.Collider;
import org.kakara.engine.math.Vector3;
import org.kakara.engine.render.Shader;

import java.util.UUID;

public class GameItem {

    private boolean selected;

    private Mesh[] meshes;

    private final Vector3f position;

    private float scale;

    private final Quaternionf rotation;

    private int textPos;

    private boolean disableFrustumCulling;

    private boolean insideFrustum;
    private final UUID uuid;
    private Collider collider;

    public GameItem() {
        selected = false;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Quaternionf();
        textPos = 0;
        insideFrustum = true;
        disableFrustumCulling = false;
        uuid = UUID.randomUUID();
    }

    public GameItem(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    /**
     * Get the current position.
     * @return The current position.
     */
    public Vector3 getPosition() {
        return new Vector3(position);
    }

    public int getTextPos() {
        return textPos;
    }

    public boolean isSelected() {
        return selected;
    }

    /**
     * Set the position of the game item.
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
     * @param position The position in vector form
     * @return The instance of the Game Item.
     */
    public GameItem setPosition(Vector3 position){
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
        return this;
    }

    /**
     * Change the position of the game item by x, y, and z values.
     * @param x Change in x
     * @param y Change in y
     * @param z Change in z
     * @return The instance of the game item.
     */
    public GameItem translateBy(float x, float y, float z){
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
        return this;
    }

    /**
     * Change the position of the game item by a vector.
     * @param position The vector to change by.
     * @return The instance of the game item.
     */
    public GameItem translateBy(Vector3 position){
        this.position.x += position.x;
        this.position.y += position.y;
        this.position.z += position.z;
        return this;
    }

    /**
     * Get the scale of the item.
     * @return The scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Set the scale of the Game Item
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
     * @param q The quaternion
     * @return The instance of the game item.
     */
    public final GameItem setRotation(Quaternionf q) {
        this.rotation.set(q);
        return this;
    }

    /**
     * Change the rotation by the angle on the axis.
     * @param angle The angle to change by. (In radians)
     * @param axis The vector of the axis (without magnitude)
     * @return The instance of the game item.
     */
    public GameItem rotateAboutAxis(float angle, Vector3 axis){
        this.rotation.rotateAxis(angle, axis.toJoml());
        return this;
    }

    /**
     * Set the rotation to the angle on the axis.
     * @param angle The angle to set to. (In Radians).
     * @param axis The vector of the axis (without magnitude)
     * @return The instance of the game item.
     */
    public GameItem setRotationAboutAxis(float angle, Vector3 axis){
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
     * @param collider The instance of the collider.
     * @return The instance of the game item.
     */
    public GameItem setCollider(Collider collider){
        this.collider = collider;
        collider.onRegister(this);
        return this;
    }

    /**
     * Remove the currently active collider.
     */
    public void removeCollider(){
        this.collider = null;
        GameHandler.getInstance().getCollisionManager().removeCollidingItem(this);
    }

    /**
     * Get the currently active collider
     * @return The collider. (Null if none applied)
     */
    public Collider getCollider(){
        return this.collider;
    }

    public void render(Shader shader) {
        for (Mesh mesh : meshes) {
            mesh.render(shader);
        }
    }

    public void cleanup() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public boolean isInsideFrustum() {
        return insideFrustum;
    }

    public void setInsideFrustum(boolean insideFrustum) {
        this.insideFrustum = insideFrustum;
    }

    public boolean isDisableFrustumCulling() {
        return disableFrustumCulling;
    }

    public void setDisableFrustumCulling(boolean disableFrustumCulling) {
        this.disableFrustumCulling = disableFrustumCulling;
    }

    /**
     * A safe way to clone a gameobject.
     * @param exact If you want it to be an exact copy.
     * @return The clone of the gameobject.
     */
    public GameItem clone(boolean exact){
        GameItem clone = new GameItem(this.meshes);
        if(exact){
            clone.setPosition(this.position.x, this.position.y, this.position.z);
            clone.setDisableFrustumCulling(this.disableFrustumCulling);
            clone.setInsideFrustum(this.insideFrustum);
            clone.setRotation(this.rotation);
            clone.setSelected(this.selected);
            clone.setScale(this.scale);
            clone.setTextPos(this.textPos);
        }
        return clone;
    }

    /**
     * Get the UUID of the game item.
     * @return The id.
     */
    public UUID getUUID(){
        return this.uuid;
    }
}
