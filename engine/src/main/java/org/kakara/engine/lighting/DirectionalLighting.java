package org.kakara.engine.lighting;

import org.joml.Vector3f;
import org.kakara.engine.math.Vector3;

/**
 * Handles Lighting that is similar to the sun.
 */
public class DirectionalLighting {
    private Vector3f direction;

    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;

    public DirectionalLighting(){
        this.direction = new Vector3f(0, -1.0f, 0);
        this.ambient = new Vector3f(0.3f, 0.24f, 0.14f);
        this.diffuse = new Vector3f(0.7f, 0.42f, 0.26f);
        this.specular = new Vector3f(0.5f, 0.5f, 0.5f);
    }

    public DirectionalLighting setDirection(float x, float y, float z){
        this.direction = new Vector3f(x, y, z);
        return this;
    }

    public DirectionalLighting setDirection(Vector3 direction){
        return setDirection(direction.x, direction.y, direction.z);
    }

    public Vector3 getDirection(){
        return new Vector3(direction);
    }

    public DirectionalLighting setAmbient(Vector3 ambient){
        this.ambient = ambient.toJoml();
        return this;
    }

    public Vector3 getAmbient(){
        return new Vector3(ambient);
    }

    public DirectionalLighting setDiffuse(Vector3 diffuse){
        this.diffuse = diffuse.toJoml();
        return this;
    }

    public Vector3 getDiffuse(){
        return new Vector3(diffuse);
    }

    public DirectionalLighting setSpecular(Vector3 specular){
        this.specular = specular.toJoml();
        return this;
    }

    public Vector3 getSpecular(){
        return new Vector3(specular);
    }
}
