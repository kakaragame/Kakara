package org.kakara.engine.item;
;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Material {
    public static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

    private Vector3f specularColor;
    private Texture specularMap;
    private float shininess;
    private Texture texture;
    private Texture normalMap;

    private List<Texture> overlayTextures;

    public Material() {
        this.specularColor = DEFAULT_COLOUR;
        this.texture = null;
        this.shininess = 32f;
        this.overlayTextures = new ArrayList<>();
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
        this.overlayTextures = new ArrayList<>();
    }

    public Material(Texture specularMap, Texture texture, float shininess){
        this.specularMap = specularMap;
        this.texture = texture;
        this.shininess = shininess;
        this.overlayTextures = new ArrayList<>();
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
