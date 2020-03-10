package org.kakara.engine.lighting;

import org.kakara.engine.GameHandler;
import org.kakara.engine.math.KMath;
import org.kakara.engine.math.Vector3;

/**
 * Point based lighting.
 */
public class PointLight implements Comparable<PointLight> {
    private LightColor color;
    private Vector3 position;
    private float intensity;

    private Attenuation attenuation;

    /**
     * Create a point light
     * @param color The color of the light
     * @param position The position of the light
     * @param intensity The intensity of the light.
     */
    public PointLight(LightColor color, Vector3 position, float intensity) {
        attenuation = new Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public PointLight(LightColor color, Vector3 position, float intensity, Attenuation attenuation) {
        this(color, position, intensity);
        this.attenuation = attenuation;
    }

    public PointLight(PointLight pointLight) {
        this(pointLight.getColor(), pointLight.getPosition().clone(),
                pointLight.getIntensity(), pointLight.getAttenuation());
    }

    public LightColor getColor() {
        return color;
    }

    public void setColor(LightColor color) {
        this.color = color;
    }

    public void setColor(int r, int g, int b){
        this.color = new LightColor(r, g, b);
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z){
        this.position = new Vector3(x, y, z);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    @Override
    public int compareTo(PointLight o) {
        Vector3 cameraPos = GameHandler.getInstance().getCamera().getPosition();
        return Math.round(KMath.distance(cameraPos, getPosition()) - KMath.distance(cameraPos, o.getPosition()));
    }

    /**
     * Handles the Attenuation information of the light.
     */
    public static class Attenuation{
        private float constant;
        private float linear;
        private float exponent;

        public Attenuation(float constant, float linear, float exponent){
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public float getConstant(){
            return  constant;
        }

        public void setConstant(float constant){
            this.constant = constant;
        }

        public float getLinear(){
            return linear;
        }

        public void setLinear(float linear){
            this.linear = linear;
        }

        public float getExponent(){
            return exponent;
        }

        public void setExponent(float exponent){
            this.exponent = exponent;
        }
    }
}
