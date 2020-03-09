package org.kakara.engine.item;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

;

public class Material {
    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private Texture specularMap;
    private float reflectance;
    private Texture texture;
    private Texture normalMap;

    private Vector4f ambientColor;
    private Vector4f diffuseColor;
    private Vector4f specularColor;

    private List<Texture> overlayTextures;

    public Material() {
        this.ambientColor = DEFAULT_COLOUR;
        this.diffuseColor = DEFAULT_COLOUR;
        this.specularColor = DEFAULT_COLOUR;
        this.texture = null;
        this.reflectance = 32f;
        this.overlayTextures = new ArrayList<>();
    }

    public Material(Vector4f color, float reflectance) {
        this(color, color, color, null, reflectance);
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, 32f);
    }

    public Material(Texture texture, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance);
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, Texture texture, float reflectance) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.texture = texture;
        this.reflectance = reflectance;
        this.overlayTextures = new ArrayList<>();
    }

    public void setAmbientColor(Vector4f ambientColor){
        this.ambientColor = ambientColor;
    }

    public Vector4f getAmbientColor(){
        return ambientColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor){
        this.diffuseColor = diffuseColor;
    }

    public Vector4f getDiffuseColor(){
        return diffuseColor;
    }

    public void setSpecularColor(Vector4f specularColor){
        this.specularColor = specularColor;
    }

    public Vector4f getSpecularColor(){
        return specularColor;
    }

    public void setReflectance(float reflectance){
        this.reflectance = reflectance;
    }

    public float getReflectance(){
        return reflectance;
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

    /**
     * Set the list of overlay textures.
     * <p>A maximum of 5 textures are allowed.</p>
     * @param overlayTextures The list
     * @throws Exception If the list exceeds 5 overlay textures.
     */
    public void setOverlayTextures(List<Texture> overlayTextures) throws Exception{
        if(overlayTextures.size() >= 5)
            throw new Exception("A maximum of 5 overlay textures only.");
        this.overlayTextures = overlayTextures;
    }

    /**
     * Add a texture to the list of overlay textures
     * @param overlayTexture The texture
     * @return True if successful, false if not. (Max limit of 5 overlay textures).
     */
    public boolean addOverlayTexture(Texture overlayTexture){
        if(overlayTextures.size() >= 5){
            return false;
        }
        overlayTextures.add(overlayTexture);
        return true;
    }

    public List<Texture> getOverlayTextures(){
        return overlayTextures;
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
