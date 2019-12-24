package org.kakara.engine.lighting;

import org.joml.Vector3f;

public class DirectionalLighting {
    public Vector3f direction;

    public Vector3f ambient;
    public Vector3f diffuse;
    public Vector3f specular;

    public DirectionalLighting(){
        this.direction = new Vector3f(-0.2f, -1.0f, -0.3f);
        this.ambient = new Vector3f(0.05f, 0.05f, 0.05f);
        this.diffuse = new Vector3f(0.4f, 0.4f, 0.4f);
        this.specular = new Vector3f(0.5f, 0.5f, 0.5f);
    }
}
