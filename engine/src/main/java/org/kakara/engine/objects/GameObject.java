package org.kakara.engine.objects;

import org.joml.Vector3f;

public interface GameObject {
    void render();
    void cleanup();


    Vector3f getPosition();
    Vector3f getRotation();
    float getScale();

    void setPosition(float x, float y, float z);
    void setRotation(float x, float y, float z);
    void setScale(float scale);
}
