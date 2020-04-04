package org.kakara.engine.renderobjects.chunkcollision;

import org.joml.Quaternionf;
import org.kakara.engine.collision.Collider;
import org.kakara.engine.collision.ObjectBoxCollider;
import org.kakara.engine.item.Collidable;
import org.kakara.engine.item.GameItem;
import org.kakara.engine.item.Mesh;
import org.kakara.engine.math.Vector3;

import java.util.UUID;

public class ChunkCollidable implements Collidable {

    private Vector3 position = new Vector3(0, 0, 0);

    @Override
    public GameItem setCollider(Collider collider) {
        return null;
    }

    @Override
    public void removeCollider() {

    }

    @Override
    public Collider getCollider() {
        return new ObjectBoxCollider().setTrigger(true);
    }

    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public GameItem setPosition(float x, float y, float z) {
        this.position = new Vector3(x, y, z);
        return null;
    }

    @Override
    public GameItem setPosition(Vector3 position) {
        setPosition(position.x, position.y, position.z);
        return null;
    }

    @Override
    public GameItem translateBy(float x, float y, float z) {
        return null;
    }

    @Override
    public GameItem translateBy(Vector3 position) {
        return null;
    }

    @Override
    public float getScale() {
        return 0;
    }

    @Override
    public GameItem setScale(float scale) {
        return null;
    }

    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public Quaternionf getRotation() {
        return null;
    }

    @Override
    public GameItem setRotation(Quaternionf q) {
        return null;
    }

    @Override
    public GameItem rotateAboutAxis(float angle, Vector3 axis) {
        return null;
    }

    @Override
    public GameItem setRotationAboutAxis(float angle, Vector3 axis) {
        return null;
    }

    @Override
    public void render() {
        
    }

    @Override
    public void cleanup() {

    }

    @Override
    public GameItem clone(boolean exact) {
        return null;
    }

    @Override
    public Mesh getMesh() {
        return null;
    }

    @Override
    public int getTextPos() {
        return 0;
    }

    @Override
    public void setTextPos(int pos) {
        
    }
}
