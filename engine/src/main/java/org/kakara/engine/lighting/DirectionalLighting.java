package org.kakara.engine.lighting;

import org.joml.Vector3f;

public class DirectionalLighting {
    public Vector3f direction;

    public Vector3f ambient;
    public Vector3f diffuse;
    public Vector3f specular;

    public DirectionalLighting(){
        this.direction = new Vector3f(0, -1.0f, 0);
        this.ambient = new Vector3f(0.3f, 0.24f, 0.14f);
        this.diffuse = new Vector3f(0.7f, 0.42f, 0.26f);
        this.specular = new Vector3f(0.5f, 0.5f, 0.5f);
    }
}
