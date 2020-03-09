package org.kakara.engine.lighting;

import org.kakara.engine.math.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles the lighting for the engine.
 */
public class LightHandler {

    public static final int MAX_POINT_LIGHTS = 5;
    public static final int MAX_SPOT_LIGHTS = 5;

    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;
    private DirectionalLight directionalLight;
    private Vector3 ambientLight;

    public LightHandler(){
        this.pointLights = new ArrayList<>();
        this.spotLights = new ArrayList<>();
        this.directionalLight = new DirectionalLight(new LightColor(255, 255, 255), new Vector3(0, 1, 0), 0.5f);
        ambientLight = new Vector3(0.3f, 0.3f, 0.3f);
    }

    public void addPointLight(PointLight pl){
        pointLights.add(pl);
    }

    public void removePointLight(PointLight pl){
        pointLights.remove(pl);
    }

    public void addSpotLight(SpotLight sl){
        spotLights.add(sl);
    }

    public void removeSpotLight(SpotLight sl){
        spotLights.remove(sl);
    }

    /**
     * Gets the list of point lights that are going to be displayed.
     * @return The list of displayed point lights.
     */
    public List<PointLight> getDisplayPointLights(){
        if(pointLights.size() <= MAX_POINT_LIGHTS){
            return pointLights;
        }
        List<PointLight> lights = new ArrayList<>(pointLights);
        Collections.sort(lights);
        lights.subList(MAX_POINT_LIGHTS, lights.size()).clear();
        return lights;
    }

    /**
     * Gets the list of point lights that are going to be displayed.
     * @return The list of displayed point lights.
     */
    public List<SpotLight> getDisplaySpotLights(){
        if(spotLights.size() <= MAX_SPOT_LIGHTS){
            return spotLights;
        }
        List<SpotLight> lights = new ArrayList<>(spotLights);
        Collections.sort(lights);
        lights.subList(MAX_SPOT_LIGHTS, lights.size()).clear();
        return lights;
    }

    public PointLight getPointLight(int id){
        return pointLights.get(id);
    }

    public List<PointLight> getPointLights(){
        return pointLights;
    }

    public SpotLight getSpotLight(int id){
        return spotLights.get(id);
    }

    public List<SpotLight> getSpotLights(){
        return spotLights;
    }

    public DirectionalLight getDirectionalLight(){
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight dl){
        this.directionalLight = dl;
    }

    public Vector3 getAmbientLight(){
        return ambientLight;
    }

    public void setAmbientLight(Vector3 ambientLight){
        this.ambientLight = ambientLight;
    }

    public void setAmbientLight(float r, float g, float b){
        this.ambientLight = new Vector3(r, g, b);
    }

}
