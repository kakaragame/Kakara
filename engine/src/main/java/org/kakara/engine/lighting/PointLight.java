package org.kakara.engine.lighting;

import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;

/**
 * Point based lighting.
 */
public class PointLight implements Comparable<PointLight> {
    private Vector3f position;

    private float constant;
    private float linear;
    private float quadratic;

    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;

    public PointLight(){
        this(new Vector3f(0, 0, 0));
    }

    public PointLight(Vector3f position){
        this.position = position;

        this.constant = 1.0f;
        this.linear = 0.09f;
        this.quadratic = 0.032f;

        this.ambient = new Vector3f(1.0f * 0.1f, 0.6f * 0.1f, 0.0f);
        this.diffuse = new Vector3f(1.0f, 0.6f, 0.0f);
        this.specular = new Vector3f(1.0f, 0.6f, 0.0f);
    }

    /**
     * Set the position of the light.
     * @param x The x
     * @param y The y
     * @param z The z
     * @return The instance of the point light.
     */
    public PointLight setPosition(float x, float y, float z){
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
        return this;
    }

    /**
     * Set the position of the light.
     * @param position The vector position
     * @return The instace of the point light.
     */
    public PointLight setPosition(Vector3 position){
        return setPosition(position.x, position.y, position.z);
    }

    /**
     * Get the position of the light
     * @return The position of the light.
     */
    public Vector3 getPosition(){
        return new Vector3(position);
    }

    public PointLight translateBy(float x, float y, float z){
        position = new Vector3f(position.x + x, position.y + y, position.z + z);
        return this;
    }

    public PointLight translateBy(Vector3 pos){
        return translateBy(pos.x, pos.y, pos.z);
    }

    public PointLight setConstant(float constant){
        this.constant = constant;
        return this;
    }

    public float getConstant(){
        return constant;
    }

    public PointLight setLinear(float linear){
        this.linear = linear;
        return this;
    }

    public float getLinear(){
        return linear;
    }

    public PointLight setQuadratic(float quadratic){
        this.quadratic = quadratic;
        return this;
    }

    public float getQuadratic(){
        return quadratic;
    }

    /**
     * The light around the point light.
     * <p>Normally this is kept at default.</p>
     * <p>All values range from 0 - 1</p>
     * @param ambient The vector containing the data.
     * @return The instance of this light.
     */
    public PointLight setAmbient(Vector3 ambient){
        this.ambient = ambient.toJoml();
        return this;
    }

    /**
     * The light around the point light.
     * <p>Normally this is kept at default.</p>
     * <p>All values range from 0 - 1</p>
     * @param x The x value
     * @param y The y value
     * @param z The z value
     * @return The instance of this light.
     */
    public PointLight setAmbient(float x, float y, float z){
        this.ambient = new Vector3f(x, y, z);
        return this;
    }

    /**
     * Get the ambient of the light.
     * @return
     */
    public Vector3 getAmbient(){
        return new Vector3(ambient);
    }

    /**
     * Set the color of the outer radius of the light.
     * <p>All values range from 0 - 1 and represent r, g, b</p>
     * @param x The Red
     * @param y The Green
     * @param z The Blue
     * @return The instance of the light
     */
    public PointLight setDiffuse(float x, float y, float z){
        this.diffuse = new Vector3f(x, y, z);
        return this;
    }

    /**
     * Set the color of the outer radius of the light.
     * <p>All values range from 0 - 1 and represent r, g, b</p>
     * @param diffuse The r, g, b
     * @return The instance of the light
     */
    public PointLight setDiffuse(Vector3 diffuse){
        return setDiffuse(diffuse.x, diffuse.y, diffuse.z);
    }

    public Vector3 getDiffuse(){
        return new Vector3(diffuse);
    }

    /**
     * The color and intensity of the inner circle.
     * <p>All values range from 0 - 1 and represent r, g, b</p>
     * @param x The Red
     * @param y The Green
     * @param z The Blue
     * @return The instance of the light
     */
    public PointLight setSpecular(float x, float y, float z) {
        this.specular = new Vector3f(x, y, z);
        return this;
    }

    /**
     * The color and intensity of the inner circle
     * <p>All values range from 0 - 1 and represent r, g, b</p>
     * @param specular The r, g, b
     * @return The instance of the light.
     */
    public PointLight setSpecular(Vector3 specular){
        return setSpecular(specular.x, specular.y, specular.z);
    }

    public Vector3 getSpecular(){
        return new Vector3(specular);
    }

    @Override
    public int compareTo(PointLight o) {
        Vector3 cameraPos = GameHandler.getInstance().getCamera().getPosition();
        return Math.round(KMath.distance(cameraPos, getPosition()) - KMath.distance(cameraPos, o.getPosition()));
    }
}
