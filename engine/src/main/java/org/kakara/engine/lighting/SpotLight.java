package org.kakara.engine.lighting;

import org.joml.Vector3f;
import org.kakara.engine.GameHandler;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;

/**
 * Spot / Beam based lighting.
 */
public class SpotLight implements Comparable<SpotLight> {
    private Vector3f position;
    private Vector3f direction;

    private float cutOff;
    private float outerCutOff;

    private float constant;
    private float linear;
    private float quadratic;

    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;

    public SpotLight(){
        this(new Vector3(0, 0, 0), new Vector3(1, 0, 0));
    }

    public SpotLight(Vector3 position, Vector3 direction){
        this.position = position.toJoml();
        this.direction = direction.toJoml();
        this.ambient = new Vector3f(0, 0, 0);
        this.diffuse = new Vector3f(1, 1, 1);
        this.specular = new Vector3f(1, 1, 1);
        this.constant = 1;
        this.linear = 0.09f;
        this.quadratic = 0.032f;
        this.cutOff = (float) Math.cos(Math.toRadians(12.5));
        this.outerCutOff = (float) Math.cos(Math.toRadians(15));
    }

    /**
     * Set the position of the light.
     * @param x The x
     * @param y The y
     * @param z The z
     * @return The instance of the point light.
     */
    public SpotLight setPosition(float x, float y, float z){
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
    public SpotLight setPosition(Vector3 position){
        return setPosition(position.x, position.y, position.z);
    }

    /**
     * Get the position of the light
     * @return The position of the light.
     */
    public Vector3 getPosition(){
        return new Vector3(position);
    }

    public SpotLight translateBy(float x, float y, float z){
        position = new Vector3f(position.x + x, position.y + y, position.z + z);
        return this;
    }

    public SpotLight translateBy(Vector3 pos){
        return translateBy(pos.x, pos.y, pos.z);
    }

    public SpotLight setDirection(float x, float y, float z){
        this.direction = new Vector3f(x, y, z);
        return this;
    }

    public SpotLight setDirection(Vector3 direction){
        return setDirection(direction.x, direction.y, direction.z);
    }

    public Vector3 getDirection(){
        return new Vector3(direction);
    }

    public SpotLight setConstant(float constant){
        this.constant = constant;
        return this;
    }

    public float getConstant(){
        return constant;
    }

    public SpotLight setLinear(float linear){
        this.linear = linear;
        return this;
    }

    public float getLinear(){
        return linear;
    }

    public SpotLight setQuadratic(float quadratic){
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
    public SpotLight setAmbient(Vector3 ambient){
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
    public SpotLight setAmbient(float x, float y, float z){
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
    public SpotLight setDiffuse(float x, float y, float z){
        this.diffuse = new Vector3f(x, y, z);
        return this;
    }

    /**
     * Set the color of the outer radius of the light.
     * <p>All values range from 0 - 1 and represent r, g, b</p>
     * @param diffuse The r, g, b
     * @return The instance of the light
     */
    public SpotLight setDiffuse(Vector3 diffuse){
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
    public SpotLight setSpecular(float x, float y, float z) {
        this.specular = new Vector3f(x, y, z);
        return this;
    }

    /**
     * The color and intensity of the inner circle
     * <p>All values range from 0 - 1 and represent r, g, b</p>
     * @param specular The r, g, b
     * @return The instance of the light.
     */
    public SpotLight setSpecular(Vector3 specular){
        return setSpecular(specular.x, specular.y, specular.z);
    }

    public Vector3 getSpecular(){
        return new Vector3(specular);
    }

    public SpotLight setCutOff(float cutoff){
        this.cutOff = cutoff;
        return this;
    }

    public float getCutOff(){
        return cutOff;
    }

    public SpotLight setOuterCutOff(float outerCutOff){
        this.outerCutOff = outerCutOff;
        return this;
    }

    public float getOuterCutOff(){
        return outerCutOff;
    }

    @Override
    public int compareTo(SpotLight o) {
        Vector3 cameraPos = GameHandler.getInstance().getCamera().getPosition();
        return Math.round(KMath.distance(cameraPos, getPosition()) - KMath.distance(cameraPos, o.getPosition()));
    }

}
