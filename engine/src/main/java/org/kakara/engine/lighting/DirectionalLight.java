package org.kakara.engine.lighting;

import org.joml.Vector3f;
import org.kakara.engine.math.Vector3;

/**
 * Handles Lighting that is similar to the sun.
 */
public class DirectionalLight {
    private Vector3 color;
    private Vector3 direction;
    private float intensity;

    public DirectionalLight(Vector3 color, Vector3 direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public DirectionalLight(DirectionalLight light) {
        this(light.getColor().clone(), light.getDirection().clone(), light.getIntensity());
    }

    public Vector3 getColor() {
        return color;
    }

    public void setColor(Vector3 color) {
        this.color = color;
    }

    public void setColor(float r, float g, float b){
        this.color = new Vector3(r, g, b);
    }

    public Vector3 getDirection() {
        return direction;
    }

    public void setDirection(Vector3 direction) {
        this.direction = direction;
    }

    public void setDirection(float x, float y, float z){
        this.direction = new Vector3(x, y, z);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
