package org.kakara.engine.lighting;

import org.joml.Vector3f;

public class PointLight {
    public Vector3f position;

    public float constant;
    public float linear;
    public float quadratic;

    public Vector3f ambient;
    public Vector3f diffuse;
    public Vector3f specular;

    public PointLight(Vector3f position){
        this.position = position;

        this.constant = 1.0f;
        this.linear = 0.09f;
        this.quadratic = 0.032f;

        this.ambient = new Vector3f(0.05f, 0.05f, 0.05f);
        this.diffuse = new Vector3f(0.8f, 0.8f, 0.8f);
        this.specular = new Vector3f(1.0f, 1.0f, 1.0f);
    }
}
