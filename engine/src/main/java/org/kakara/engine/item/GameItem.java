package org.kakara.engine.item;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.collision.Collider;
import org.kakara.engine.collision.CollisionManager;
import org.kakara.engine.math.Vector3;

import java.util.UUID;

import static org.lwjgl.opengl.GL11.*;

public class GameItem {

    private boolean selected;

    private Mesh[] meshes;

    private final Vector3f position;

    private float scale;

    private final Quaternionf rotation;

    private int textPos;

    private boolean disableFrustumCulling;

    private boolean insideFrustum;
    private UUID uuid = UUID.randomUUID();
    private Collider collider;

    public GameItem() {
        selected = false;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Quaternionf();
        textPos = 0;
        insideFrustum = true;
        disableFrustumCulling = false;
    }

    public GameItem(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public Vector3 getPosition() {
        return new Vector3(position);
    }

    public int getTextPos() {
        return textPos;
    }

    public boolean isSelected() {
        return selected;
    }

    public GameItem setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        return this;
    }

    public GameItem setPosition(Vector3 position){
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
        return this;
    }

    public GameItem translateBy(float x, float y, float z){
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
        return this;
    }

    public GameItem translateBy(Vector3 position){
        this.position.x += position.x;
        this.position.y += position.y;
        this.position.z += position.z;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public final GameItem setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public UUID getId() {
        return uuid;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final GameItem setRotation(Quaternionf q) {
        this.rotation.set(q);
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

    public GameItem setCollider(Collider collider){
        this.collider = collider;
        collider.onRegister(this);
        return this;
    }

    public void removeCollider(){
        this.collider = null;
        GameHandler.getInstance().getCollisionManager().removeCollidingItem(this);
    }

    public Collider getCollider(){
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

    public UUID getUuid(){
        return this.uuid;
    }
}
