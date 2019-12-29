package org.kakara.engine.item;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.kakara.engine.math.Vector3;

public class Material {
    public static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector3f specularColor;
    private Texture specularMap;
    private float shininess;
    private Texture texture;
    private Texture overlayTexture;
    private Texture normalMap;

    public Material() {
        this.specularColor = DEFAULT_COLOUR;
        this.texture = null;
        this.shininess = 32f;
    }

    public Material(Vector3f specularColor, float shininess) {
        this(specularColor, null, shininess);
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOUR, texture, 32f);
    }

    public Material(Texture texture, float shininess) {
        this(DEFAULT_COLOUR, texture, shininess);
    }

    public Material(Texture specularMap, Texture texture) {
        this(specularMap, texture, 32f);
    }

    public Material(Vector3f specularColor, Texture texture, float shininess) {
        this.specularColor = specularColor;
        this.texture = texture;
        this.shininess = shininess;
    }

    public Material(Texture specularMap, Texture texture, float shininess){
        this.specularMap = specularMap;
        this.texture = texture;
        this.shininess = shininess;
    }

    public void setSpecularColor(Vector3f specularColor){
        this.specularColor = specularColor;
    }

    public Vector3f getSpecularColor(){
        return specularColor;
    }

    public void setSpecularMap(Texture specularMap){
        this.specularMap = specularMap;
    }

    public Texture getSpecularMap(){
        return this.specularMap;
    }

    public void setShininess(float shininess){
        this.shininess = shininess;
    }

    public float getShininess(){
        return shininess;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setOverlayTexture(Texture overlayTexture){
        this.overlayTexture = overlayTexture;
    }

    public Texture getOverlayTexture(){
        return overlayTexture;
    }


    public boolean hasNormalMap() {
        return this.normalMap != null;
    }

    public Texture getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
    }
}
